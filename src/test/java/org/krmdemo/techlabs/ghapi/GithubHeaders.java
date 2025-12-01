package org.krmdemo.techlabs.ghapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import feign.Response;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.krmdemo.techlabs.core.utils.CoreCollectors.toLinkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;

/**
 * Utility-class to parse HTTP-headers of GitHib-API results
 *
 * @see <a href="https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api?apiVersion=2022-11-28">
 *     (GitHub docs) Rate limits for the REST API
 * </a>
 */
@Slf4j
public class GithubHeaders {

    /**
     * HTTP-Header name for paging links
     */
    public final static String HEADER_NAME__LINK = "link";

    //  x-ratelimit-limit=[5000],
    //  x-ratelimit-remaining=[4943],
    //  x-ratelimit-reset=[1764555236],
    //  x-ratelimit-resource=[core],
    //  x-ratelimit-used=[57],

    /**
     * HTTP-Header name for GitHub-RateLimit that contains the maximum number of requests per hour
     *
     * @see <a href="https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api?apiVersion=2022-11-28#primary-rate-limit-for-authenticated-users">
     *     Primary rate limit for authenticated users
     * </a>
     */
    public final static String HEADER_NAME__RATE_LIMIT_PER_HOUR = "x-ratelimit-limit";

    /**
     * HTTP-Header name for GitHub-RateLimit that contains the number of remaining requests to make within the limit
     */
    public final static String HEADER_NAME__RATE_LIMIT_REMAINING = "x-ratelimit-remaining";

    /**
     * HTTP-Header name for GitHub-RateLimit that contains the number of requests that were already done within the limit
     */
    public final static String HEADER_NAME__RATE_LIMIT_USED = "x-ratelimit-used";

    /**
     * HTTP-Query parameter that corresponds to the {@code '1'}-based sequence number of the page to fetch
     */
    public final static String PARAM_NAME__PAGE_NUM = "page";

    /**
     * HTTP-Query parameter that corresponds to the number of items in each page
     */
    public final static String PARAM_NAME__PER_PAGE = "per_page";

    static Pattern HEADER_LINK_PATTERN = Pattern.compile("<(?<url>[^><;,]*)>[; ]*rel=\"(?<rel>[a-zA-Z0-9_:-]+)\"[, ]*");

    /**
     * Parse the HTTP-header {@value #HEADER_NAME__LINK} and return the map of paging links
     *
     * @param response feign-response as {@link Response} to access the headers of HTTP-Response
     * @return the linked-map, where the {@link Map.Entry#getKey() key} is the kind of link ({@code "next"}, {@code "last"}, ...),
     *         and the {@link Map.Entry#getValue() value} is a link as {@link LinkUri}
     */
    public static Map<String, LinkUri> relToLinkUri(Response response) {
        String headerLink = extractString(response.headers(), HEADER_NAME__LINK);
        return StringUtils.isBlank(headerLink) ? Collections.emptyMap() : relToLinkUri(headerLink);
    }

    /**
     * Parse the value of HTTP-header {@value #HEADER_NAME__LINK} and return the map of paging links
     *
     * @param headerLink the value of HTTP-header {@value #HEADER_NAME__LINK}
     * @return the linked-map, where the {@link Map.Entry#getKey() key} is the kind of link ({@code "next"}, {@code "last"}, ...),
     *         and the {@link Map.Entry#getValue() value} is a link as {@link LinkUri}
     */
    public static Map<String, LinkUri> relToLinkUri(String headerLink) {
        Matcher matcher = HEADER_LINK_PATTERN.matcher(headerLink);
        return matcher.results().collect(toLinkedMap(
            mr -> mr.group("rel"),
            mr -> new LinkUri(mr.group("url"))
        ));
    }

    /**
     * Extracting the string-value either from query-map or headers-map
     *
     * @param nameToColl the map, where the {@link Map.Entry#getKey() key} is the name of header or query-parameter
     *                   and the {@link Map.Entry#getValue() value} is the {@link Collection&lt;String&gt;}
     * @param name the name of header or parameter to extract
     * @return the first value in corresponding collection as {@link String}, or {@code null} if nothing was found
     */
    public static String extractString(Map<String, Collection<String>> nameToColl, String name) {
        Collection<String> coll = nameToColl.get(name);
        if (coll == null || coll.isEmpty()) {
            return null;
        } else {
            return coll.iterator().next();
        }
    }

    /**
     * Extracting the integer-value either from query-map or headers-map
     *
     * @param nameToColl the map, where the {@link Map.Entry#getKey() key} is the name of header or query-parameter
     *                   and the {@link Map.Entry#getValue() value} is the {@link Collection&lt;String&gt;}
     * @param name the name of header or parameter to extract
     * @return the first integer-value in corresponding collection as {@link Integer},
     *         or {@code null} if nothing was found or it was impossible to parse it as an {@link Integer}
     */
    public static Integer extractInteger(Map<String, Collection<String>> nameToColl, String name) {
        return parseInteger(extractString(nameToColl, name));
    }

    /**
     * Safe-parsing the {@link String} for an {@link Integer} and returning {@code null} in case of failure
     *
     * @param intStr string to parse for an integer
     * @return parsed {@link Integer} or {@code null} in case of failure
     */
    public static Integer parseInteger(String intStr) {
        try {
            return StringUtils.isBlank(intStr) ? null : Integer.valueOf(intStr.trim());
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    /**
     * This class encapsulates the link-URI and used as a {@link Map.Entry#getValue() value}
     * in the map, which is returned by {@link #relToLinkUri(Response)} or {@link #relToLinkUri(String)}
     */
    @Getter
    @JsonPropertyOrder(alphabetic = true)
    public static class LinkUri {
        @JsonIgnore private final URI uri;
        @JsonIgnore private final Map<String, String> queryParams;
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

    /**
     * Extracting the value of HTTP-header {@value HEADER_NAME__RATE_LIMIT_PER_HOUR}
     *
     * @param response feign-response as {@link Response} to access the headers of HTTP-Response
     * @return the value of HTTP-header {@value HEADER_NAME__RATE_LIMIT_PER_HOUR}
     */
    public static Integer extractRateLimitPerHour(Response response) {
        return extractInteger(response.headers(), HEADER_NAME__RATE_LIMIT_PER_HOUR);
    }

    /**
     * Extracting the value of HTTP-header {@value HEADER_NAME__RATE_LIMIT_REMAINING}
     *
     * @param response feign-response as {@link Response} to access the headers of HTTP-Response
     * @return the value of HTTP-header {@value HEADER_NAME__RATE_LIMIT_REMAINING}
     */
    public static Integer extractRateLimitRemaining(Response response) {
        return extractInteger(response.headers(), HEADER_NAME__RATE_LIMIT_REMAINING);
    }

    /**
     * Extracting the value of HTTP-header {@value HEADER_NAME__RATE_LIMIT_USED}
     *
     * @param response feign-response as {@link Response} to access the headers of HTTP-Response
     * @return the value of HTTP-header {@value HEADER_NAME__RATE_LIMIT_USED}
     */
    public static Integer extractRateLimitUsed(Response response) {
        return extractInteger(response.headers(), HEADER_NAME__RATE_LIMIT_USED);
    }
}
