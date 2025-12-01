package org.krmdemo.techlabs.ghapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.krmdemo.techlabs.ghapi.GithubHeaders.PARAM_NAME__PAGE_NUM;
import static org.krmdemo.techlabs.ghapi.GithubHeaders.PARAM_NAME__PER_PAGE;
import static org.krmdemo.techlabs.ghapi.GithubHeaders.extractInteger;
import static org.krmdemo.techlabs.ghapi.GithubHeaders.relToLinkUri;

/**
 * This class represents the paging-result of some endpoints of GitHub-API,
 * where the total number of items could be huge enough, and we have to fetch them page by page.
 *
 * @param <T> the type of item in the paging result
 *
 * @see <a href="https://docs.github.com/en/rest/using-the-rest-api/using-pagination-in-the-rest-api?apiVersion=2022-11-28">
 *     (GitHub docs) Using pagination in the REST API
 * </a>
 */
@Getter
@Accessors(fluent = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
    "request-url", "page-num", "per-page",
    "http-status", "http-reason", "next-page", "last-page",
    "rate-limit-per-hour", "rate-limit-remaining", "rate-limit-used",
    "items-list"
})
@Slf4j
public class PagingResult<T> {

    @JsonProperty("items-list")private final List<T> itemsList;

    @JsonProperty("request-url") private final String requestUrl;
    @JsonProperty("page-num") private final Integer pageNum;
    @JsonProperty("per-page") private final Integer perPage;

    @JsonProperty("http-status") private final int httpStatus;
    @JsonProperty("http-reason") private final String httpReason;
    @JsonProperty("next-page") private final Integer nextPage;
    @JsonProperty("last-page") private final Integer lastPage;

    @JsonProperty("rate-limit-per-hour") private final Integer rateLimitPerHour;
    @JsonProperty("rate-limit-remaining") private final Integer rateLimitRemaining;
    @JsonProperty("rate-limit-used") private final Integer rateLimitUsed;

    private PagingResult(Builder<T> builder) {
        this.itemsList = builder.itemsList;
        this.requestUrl = builder.requestUrl;
        this.perPage = builder.perPage;
        this.pageNum = builder.pageNum;
        this.httpStatus = builder.httpStatus;
        this.httpReason = builder.httpReason;
        this.nextPage = builder.nextPage;
        this.lastPage = builder.lastPage;
        this.rateLimitPerHour = builder.rateLimitPerHour;
        this.rateLimitRemaining = builder.rateLimitRemaining;
        this.rateLimitUsed = builder.rateLimitUsed;
    }

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }

    /**
     * This class represents a factory to produce the instances of {@link GithubApi}
     *
     * @see <a href="https://www.digitalocean.com/community/tutorials/builder-design-pattern-in-java">
     *     Builder Design Pattern in Java
     * </a>
     * @see <a href="https://www.baeldung.com/java-builder-pattern">
     *     Implement the Builder Pattern in <code>Java</code>
     * </a>
     */
    @Setter
    @Accessors(fluent = true, chain = true)
    public static class Builder<T> {
        private List<T> itemsList;

        private String requestUrl;
        private Integer pageNum;
        private Integer perPage;

        private int httpStatus;
        private String httpReason;
        private Integer nextPage;
        private Integer lastPage;

        private Integer rateLimitPerHour;
        private Integer rateLimitRemaining;
        private Integer rateLimitUsed;

        private Builder() {
        }

        @SuppressWarnings("unchecked")
        public Builder<T> itemsListObj(Object itemsListObj) {
            itemsList = (List<T>) itemsListObj;
            return this;
        }

        public PagingResult<T> build() {
            return new PagingResult<>(this);
        }
    }

    /**
     * The only way to get the instance of {@link Builder}, because its constructor is private.
     *
     * @return the instance of {@link Builder} that produces the instances of {@link PagingResult}
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Creating the instance of {@link Builder} and populate the values from Open-Feign {@link Response}
     *
     * @param feighResponse feign-response as {@link Response}
     * @return the instance of {@link Builder} which later could be used to instantiate the {@link PagingResult}
     * @param <T> a type of item in paging result
     */
    public static <T> Builder<T> fromFeignResponse(Response feighResponse) {
        Builder<T> builder = PagingResult.<T>builder()
            .httpStatus(feighResponse.status())
            .httpReason(feighResponse.reason())
            .requestUrl(feighResponse.request().requestTemplate().url());

        Map<String, Collection<String>> requestParams = feighResponse.request().requestTemplate().queries();
        builder.pageNum(extractInteger(requestParams, PARAM_NAME__PAGE_NUM));
        builder.perPage(extractInteger(requestParams, PARAM_NAME__PER_PAGE));

        Map<String, GithubHeaders.LinkUri> linkUriMap = relToLinkUri(feighResponse);
        GithubHeaders.LinkUri nextLink = linkUriMap.get("next");
        GithubHeaders.LinkUri lastLink = linkUriMap.get("last");
        builder.nextPage(nextLink == null ? null : nextLink.getPageNum());
        builder.lastPage(lastLink == null ? null : lastLink.getPageNum());

        return builder
            .rateLimitPerHour(GithubHeaders.extractRateLimitPerHour(feighResponse))
            .rateLimitRemaining(GithubHeaders.extractRateLimitRemaining(feighResponse))
            .rateLimitUsed(GithubHeaders.extractRateLimitUsed(feighResponse));
    }

    /**
     * Getting the instance of Open-Feign decoder that make an attempt to treat the input as {@link PagingResult}
     *
     * @param delegate an Open-Feign decoder to delegate the job
     * @return the instance of Open-Feign decoder for response of type {@link PagingResult}
     */
    public static Decoder feignDecoder(Decoder delegate) {
        return new PagingResultDecoder(delegate);
    }

    static class PagingResultDecoder implements Decoder {
        private final static TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();
        private final Decoder delegate;
        PagingResultDecoder(Decoder delegate) {
            this.delegate = Objects.requireNonNull(delegate);
        }
        @Override
        public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
            int httpStatus = response.status();
            String httpReason = response.reason();
            String requestUrl = response.request().url();
            Map<String, Collection<String>> requestHeaders = response.request().requestTemplate().headers();
            Map<String, Collection<String>> requestQueryParams = response.request().requestTemplate().queries();
            log.info("""
                
                --> requestUrl = '{}';
                --> requestHeaders --> {};
                --> requestQueryParams --> {};
                <-- httpStatus = {};
                <-- httpReason = '{}';
                <-- response.headers() --> {};
                <-- Type = {}:
                <-- - type.name = '{}',
                <-- - type.class = {};""",
                requestUrl, requestHeaders, requestQueryParams,
                httpStatus, httpReason,
                response.headers(),
                type, type.getTypeName(), type.getClass()
            );
            if (isSpecifiedBy(type) && type instanceof ParameterizedType parameterizedType) {
                JavaType javaTypeItem = TYPE_FACTORY.constructType(parameterizedType.getActualTypeArguments()[0]);
                JavaType javaTypeItemsList = TYPE_FACTORY.constructCollectionType(List.class, javaTypeItem);
                log.info("creating the paging-result of type {} :: {}", type, javaTypeItem);
                var itemsListObj = (List<?>)this.delegate.decode(response, javaTypeItemsList);
                log.debug("itemsListObj of type {} --> {}",
                    javaTypeItemsList,
                    DumpUtils.dumpAsJsonTxt(itemsListObj));
                return fromFeignResponse(response)
                    .itemsListObj(itemsListObj)  // <-- it looks like it's the simplest way to assign items of unknown class
                    .build();
            }
            log.info("delegating the whole decoding to " + delegate.getClass().getSimpleName());
            Object delegateResult = this.delegate.decode(response, type);
            log.debug("delegateResult of type {} --> {}",
                delegateResult.getClass(),
                DumpUtils.dumpAsJsonTxt(delegateResult));
            return delegateResult;
        }

        public static boolean isSpecifiedBy(Type type) {
            if (!(type instanceof ParameterizedType parameterizedType)) {
                return false;
            }
            String typeName = parameterizedType.getRawType().getTypeName();
            return PagingResult.class.getName().equals(typeName);
        }
    }
}
