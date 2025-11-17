package org.krmdemo.techlabs.ghapi;

import feign.Feign;
import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.Request;
import feign.RequestLine;
import feign.Response;
import feign.Util;
import feign.jackson.JacksonDecoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.core.utils.CorePropsUtils;
import org.krmdemo.techlabs.core.utils.JacksonUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toSortedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;

/**
 * This unit-test is just a demo to make REST-requests to GitHub-API using different approaches.
 * <hr/>
 * The same things is performed from command-line via standard {@code curl}-utility: <pre>{@code
 * ...> ./.gfithub/scripts/curl--gh-list-packages.sh
 * }
 * ... or exectly the same could be archived using dedicated {@code gh}-utility: <pre>{@code
 * ...> ./.gfithub/scripts/gh-api--list-packages.sh
 * }
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class GithubHttpTest {

    final static String URL_GITHUB_API = "https://api.github.com";
    final static String URL_LIST_PACKAGES = URL_GITHUB_API + "/users/krm-demo/packages";
    final static String GITHUB_AUTH_TOKEN = StringUtils.reverse("6zYck0lz4EQpVDWqkQ2un0lVZLXC4V9wiE1L_phg");
    final static int HTTP_TIMEOUT_MS = 5_000;  // <-- 5 seconds for timeout
    final static Duration HTTP_TIMEOUT = Duration.ofMillis(HTTP_TIMEOUT_MS);

    @Test
    @DisplayName("1. Old-School approach with HttpURLConnection")
    void testListPackages_OldSchool(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());
        Map<String, String> paramsMap = mavenTypeParamsMap();
        URI uri = URI.create(URL_LIST_PACKAGES + urlParams(paramsMap));
        try {
            HttpURLConnection httpConn = (HttpURLConnection)uri.toURL().openConnection();
            httpConn.setRequestMethod("GET");
            headersMapStr().forEach(httpConn::setRequestProperty);  // <-- very nice way to provide headers !!!
            httpConn.setConnectTimeout(HTTP_TIMEOUT_MS);
            httpConn.setReadTimeout(HTTP_TIMEOUT_MS);
            // remote invocation is performed after following statement:
            int httpStatus = httpConn.getResponseCode();
            assertThat(httpStatus).isEqualTo(200);
            Map<String, List<String>> responseHeadersMap = lowerCaseNames(httpConn.getHeaderFields());
            System.out.println("responseHeadersMap.size() --> " + responseHeadersMap.size());
            System.out.println("responseHeadersMap.keySet() --> " + responseHeadersMap.keySet());
            printHeader(responseHeadersMap, "content-type");
            printHeader(responseHeadersMap, "content-length");
            printHeader(responseHeadersMap, "date");
            String responseBody = IOUtils.toString(httpConn.getInputStream(), Charset.defaultCharset());
            verifyResponseBody(responseBody);
            httpConn.disconnect();
        } catch (IOException ioEx) {
            throw new IllegalStateException("cannot connect to URI via HttpURLConnection - " + uri, ioEx);
        }
    }

    @Test
    @DisplayName("2. JDK-11 approach with HttpClient")
    void testListPackages_HttpClient(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());
        Map<String, String> paramsMap = mavenTypeParamsMap();
        URI uri = URI.create(URL_LIST_PACKAGES + urlParams(paramsMap));
        try (HttpClient client = HttpClient.newBuilder().build()) {
            HttpRequest.Builder requestBuilder =
                HttpRequest.newBuilder()
                    .timeout(HTTP_TIMEOUT)
                    .uri(uri).GET();
            headersMapStr().forEach(requestBuilder::header);    // <-- very nice way to provide headers !!!
            HttpRequest request = requestBuilder.build();
            // remote invocation is performed after following statement:
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            // HTTP-status is also present in following map by key ":status":
            Map<String, List<String>> responseHeadersMap = response.headers().map();
            System.out.println("responseHeadersMap.size() --> " + responseHeadersMap.size());
            printHeader(responseHeadersMap, "content-type");
            printHeader(responseHeadersMap, "content-length");
            printHeader(responseHeadersMap, "date");
            verifyResponseBody(response.body());
        } catch (InterruptedException | IOException ex) {
            throw new IllegalStateException("cannot connect to URI via HttpClient - " + uri, ex);
        }
    }

    @Test
    @DisplayName("3. Open-Feign approach (using @HeaderMap and @QueryMap)")
    void testListPackages_OpenFeign_HeaderMaps(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());

        interface PackagesApi {
            /**
             * Getting the list of packages for the current user.
             *
             * @param headerMap all HTTP-headers that must be provided by client-side
             * @param queryMap a query-map that will be converted into URL-query string by Open-Feign
             * @return the list of packages for the current user<hr/>
             *         - once we are using Jackson-Decoder we can expect the JSON-array;<br/>
             *         - to access HTTP-headers of response it's necessary to return the type "feign.Response";
             */
            @RequestLine("GET /users/krm-demo/packages")
            List<Object> listAll(@HeaderMap Map<String, Object> headerMap, @QueryMap Map<String, Object> queryMap);
        }

        PackagesApi githubPackages = Feign.builder()
            .options(new Request.Options(HTTP_TIMEOUT, HTTP_TIMEOUT, true))
            .decoder(new JacksonDecoder())
            .target(PackagesApi.class, URL_GITHUB_API);

        List<Object> responseList = githubPackages.listAll(headersMap(), mavenTypeQueryMap());
        verifyResponseList(responseList);
    }

    @Test
    @DisplayName("4. Open-Feign approach (using @Headers on interface)")
    void testListPackages_OpenFeign_HeadersAnnotation(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());

        @Headers({
            "Authorization: token {github-token}",
            "Accept: application/vnd.github+json",
            "X-GitHub-Api-Version: 2022-11-28",
        })
        interface PackagesApi {
            /**
             * Getting the list of packages for the current user.
             *
             * @param githubToken this token will substitute the place-holder in the header-annotation
             * @param queryMap a query-map that will be converted into URL-query string by Open-Feign
             * @return the list of packages for the current user<hr/>
             *         - once we are using Jackson-Decoder we can expect the JSON-array;<br/>
             *         - to access HTTP-headers of response it's necessary to return the type "feign.Response";
             */
            @RequestLine("GET /users/krm-demo/packages")
            List<Object> listAll(@Param("github-token") String githubToken, @QueryMap Map<String, Object> queryMap);
        }

        PackagesApi githubPackages = Feign.builder()
            .options(new Request.Options(HTTP_TIMEOUT, HTTP_TIMEOUT, true))
            .decoder(new JacksonDecoder())
            .target(PackagesApi.class, URL_GITHUB_API);

        List<Object> responseList = githubPackages.listAll(GITHUB_AUTH_TOKEN, mavenTypeQueryMap());
        verifyResponseList(responseList);
    }

    @Test
    @DisplayName("5. Open-Feign approach (using RequestInterceptor)")
    void testListPackages_OpenFeign_RequestInterceptor(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());

        interface PackagesApi {
            /**
             * Getting the list of packages for the current user.
             *
             * @return HTTP-response as {@link Response} object to access the body and response-headers
             */
            @RequestLine("GET /users/krm-demo/packages")
            Response listAll();
        }

        PackagesApi githubPackages = Feign.builder()
            .options(new Request.Options(HTTP_TIMEOUT, HTTP_TIMEOUT, true))
            .requestInterceptor(requestTemplate -> {
                headersMapStr().forEach(requestTemplate::header);
                mavenTypeParamsMap().forEach(requestTemplate::query);
            }).target(PackagesApi.class, URL_GITHUB_API);

        try (feign.Response response = githubPackages.listAll()) {
            System.out.println("responseHeadersMap.size() --> " + response.headers().size());
            printHeader(response.headers(), "content-type");
            printHeader(response.headers(), "content-length");
            printHeader(response.headers(), "date");
            String responseBody = Util.toString(response.body().asReader(Util.UTF_8));
            verifyResponseBody(responseBody);
        } catch (Exception ioEx) {
            throw new IllegalStateException("exceoption when access the HTTP-response", ioEx);
        }
    }

    // ------------------------------------------------------------------------------------

    static <V> NavigableMap<String, V> lowerCaseNames(Map<String,V> originalMap) {
        return originalMap.entrySet().stream()
            .filter(e -> StringUtils.isNotBlank(e.getKey()))
            .collect(toSortedMap(
                e -> e.getKey().toLowerCase(),
                Map.Entry::getValue
            ));
    }

    static void printHeader(Map<String, ? extends Collection<String>> responseHeadersMap, String headerName) {
        System.out.printf("responseHeaders['%s'] = '%s';%n",
            headerName, firstHeader(responseHeadersMap, headerName));
    }

    static String firstHeader(Map<String, ? extends Collection<String>> responseHeadersMap, String headerName) {
        Collection<String> valuesColl = responseHeadersMap.get(headerName);
        return valuesColl == null ? null : valuesColl.iterator().next();
    }

    static Map<String, Object> headersMap() {
        return Collections.unmodifiableMap(headersMapStr());
    }

    static Map<String, String> headersMapStr() {
        return linkedMap(
            nameValue("Authorization", "token " + GITHUB_AUTH_TOKEN),
            nameValue("Accept", "application/vnd.github+json"),
            nameValue("X-GitHub-Api-Version", "2022-11-28")
        );
    }

    static Map<String, Object> mavenTypeQueryMap() {
        return Collections.unmodifiableMap(mavenTypeParamsMap());
    }

    static Map<String, String> mavenTypeParamsMap() {
        return linkedMap(nameValue("package_type", "maven"));
    }

    private void verifyResponseBody(String responseBody) {
        verifyResponseList(JacksonUtils.jsonArrFromString(responseBody));
    }

    private void verifyResponseList(List<Object> responseList) {
        List<String> ghPkgNames = responseList.stream()
            .map(obj -> CorePropsUtils.propValueStr(obj, "name"))
            .toList();
        assertThat(ghPkgNames).contains("io.github.krm-demo.core-utils");
        //System.out.println(DumpUtils.dumpAsJsonTxt(responseList));
        System.out.println(DumpUtils.dumpAsJsonTxt(ghPkgNames));
    }

    // ------------------------------------------------------------------------------------
    //  TODO: think about move it to a separate utility-class, whih work with URL-params:
    // ------------------------------------------------------------------------------------

    public static String urlParams(Map<String, String> paramsMap) {
        return urlParams(paramsMap.entrySet().stream());
    }

    public static String urlParams(Map.Entry<String, String>... paramsArr) {
        return urlParams(Arrays.stream(paramsArr));
    }

    public static String urlParams(Stream<Map.Entry<String, String>> params) {
        return "?" + params.map(GithubHttpTest::urlEncode)
            .collect(Collectors.joining("&"));
    }

    public static String urlEncode(Map.Entry<String, String> paramPair) {
        return urlEncode(paramPair.getKey()) + "=" + urlEncode(paramPair.getValue());
    }

    public static String urlEncode(String str) {
        return URLEncoder.encode(str, Charset.defaultCharset());
    }
}
