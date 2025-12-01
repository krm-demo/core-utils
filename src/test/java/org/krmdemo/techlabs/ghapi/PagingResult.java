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
import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.krmdemo.techlabs.core.utils.CoreCollectors.toLinkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;

/**
 * This class represents the paging-result of some endpoints of GitHub-API,
 * where the total number of items could be huge enough and we have to fetch them page by page.
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
    "items-list"
})
@Slf4j
public class PagingResult<T> {

    /**
     * HTTP-Query parameter that corresponds to the {@code '1'}-based sequence number of the page to fetch
     */
    public final static String PARAM_NAME__PAGE_NUM = "page";

    /**
     * HTTP-Query parameter that corresponds to the number of items in each page
     */
    public final static String PARAM_NAME__PER_PAGE = "per_page";

    @JsonProperty("items-list")private final List<T> itemsList;

    @JsonProperty("request-url") private final String requestUrl;
    @JsonProperty("page-num") private final Integer pageNum;
    @JsonProperty("per-page") private final Integer perPage;

    @JsonProperty("http-status") private final int httpStatus;
    @JsonProperty("http-reason") private final String httpReason;
    @JsonProperty("next-page") private final Integer nextPage;
    @JsonProperty("last-page") private final Integer lastPage;

    private PagingResult(Builder<T> builder) {
        this.itemsList = builder.itemsList;
        this.requestUrl = builder.requestUrl;
        this.perPage = builder.perPage;
        this.pageNum = builder.pageNum;
        this.httpStatus = builder.httpStatus;
        this.httpReason = builder.httpReason;
        this.nextPage = builder.nextPage;
        this.lastPage = builder.lastPage;
    }

    @Override
    public String toString() {
        return DumpUtils.dumpAsJsonTxt(this);
    }

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

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> Builder<T> fromFeignResponse(Response feighResponse) {
        Builder<T> builder = PagingResult.<T>builder()
            .httpStatus(feighResponse.status())
            .httpReason(feighResponse.reason())
            .requestUrl(feighResponse.request().requestTemplate().url());
        Map<String, Collection<String>> requestParams = feighResponse.request().requestTemplate().queries();
        builder.pageNum(extractInteger(requestParams, PARAM_NAME__PAGE_NUM));
        builder.perPage(extractInteger(requestParams, PARAM_NAME__PER_PAGE));
        String headerLink = extractString(feighResponse.headers(), "link");
        Map<String, LinkUri> linkUriMap = relToLinkUri(headerLink);
        LinkUri nextLink = linkUriMap.get("next");
        LinkUri lastLink = linkUriMap.get("last");
        builder.nextPage(nextLink == null ? null : nextLink.getPageNum());
        builder.lastPage(lastLink == null ? null : lastLink.getPageNum());
        // TODO: parse rate-limits
        return builder;
    }

    private static String extractString(Map<String, Collection<String>> nameToColl, String name) {
        Collection<String> coll = nameToColl.get(name);
        if (coll == null || coll.isEmpty()) {
            return null;
        } else {
            return coll.iterator().next();
        }
    }

    private static Integer extractInteger(Map<String, Collection<String>> nameToColl, String name) {
        return parseInteger(extractString(nameToColl, name));
    }

    private static Integer parseInteger(String intStr) {
        try {
            return StringUtils.isBlank(intStr) ? null : Integer.valueOf(intStr.trim());
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    static Pattern HEADER_LINK_PATTERN = Pattern.compile("<(?<url>[^><;,]*)>[; ]*rel=\"(?<rel>[a-zA-Z0-9_:-]+)\"[, ]*");

    static Map<String, LinkUri> relToLinkUri(String headerLink) {
        Matcher matcher = HEADER_LINK_PATTERN.matcher(headerLink);
        return matcher.results().collect(toLinkedMap(
            mr -> mr.group("rel"),
            mr -> new LinkUri(mr.group("url"))
        ));
    }

    @Getter
    @JsonPropertyOrder(alphabetic = true)
    static class LinkUri {
        private final URI uri;
        private final Map<String, String> queryParams;
        LinkUri(String uriStr) {
            this(parseURI(uriStr));
        }
        LinkUri(URI uri) {
            this.uri = uri;
            if (uri == null || StringUtils.isBlank(uri.getQuery())) {
                this.queryParams = Collections.emptyMap();
                return;
            }
            this.queryParams = Arrays.stream(uri.getQuery().split("&"))
                .map(this::paramPairEntry)
                .collect(toLinkedMap());
        }
        Map.Entry<String, String> paramPairEntry(String paramPair) {
            if (StringUtils.isBlank(paramPair)) {
                return null;
            }
            String[] pairArr = paramPair.split("=");
            return nameValue(pairArr[0], pairArr[1]);
        }
        static URI parseURI(String urlStr) {
            try {
                return new URI(urlStr);
            } catch (URISyntaxException uriSyntaxEx) {
                log.error(String.format("cannot parse the link-URI '%s'", urlStr), uriSyntaxEx);
                return null;
            }
        }

        public Integer getPageNum() {
            return parseInteger(queryParams.get(PARAM_NAME__PAGE_NUM));
        }

        public Integer getPerPage() {
            return parseInteger(queryParams.get(PARAM_NAME__PER_PAGE));
        }
    }


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
                    .itemsListObj(itemsListObj)
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
