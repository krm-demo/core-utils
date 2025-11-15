package org.krmdemo.techlabs.ghapi;

import feign.Feign;
import feign.HeaderMap;
import feign.QueryMap;
import feign.Request;
import feign.RequestLine;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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
public class GithubApiTest {

    final static String URL_GITHUB_API = "https://api.github.com";
    final static String URL_LIST_PACKAGES = URL_GITHUB_API + "/users/krm-demo/packages";
    final static String GITHUB_AUTH_TOKEN = StringUtils.reverse("6zYck0lz4EQpVDWqkQ2un0lVZLXC4V9wiE1L_phg");
    final static int HTTP_TIMEOUT_MS = 5_000;  // <-- 5 seconds for timeout
    final static Duration HTTP_TIMEOUT = Duration.ofMillis(HTTP_TIMEOUT_MS);

    @Test
    @DisplayName("3. Open-Feign approach (using @HeaderMap and @QueryMap)")
    void testListPackages_OpenFeign(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());

        interface PackagesApi {
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
    @DisplayName("2. Java-11 approach with HttpClient")
    void testListPackages_HttpClient(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());
        Map<String, String> paramsMap = mavenTypeParamsMap();
        URI uri = URI.create(URL_LIST_PACKAGES + urlParams(paramsMap));
        try (HttpClient client = HttpClient.newBuilder().build()) {
            HttpRequest.Builder requestBuilder =
                HttpRequest.newBuilder()
                    .timeout(HTTP_TIMEOUT)
                    .uri(uri).GET();
            headersMapStr().forEach(requestBuilder::header);
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            Map<String, List<String>> responseHeadersMap = response.headers().map();
            System.out.println("responseHeadersMap.size() --> " + responseHeadersMap.size());
//            System.out.println("==========================================================");
//            PrintUtils.printAsJsonTxt(responseHeadersMap);
//            System.out.println();
//            System.out.println("==========================================================");
            verifyResponseBody(response.body());
        } catch (InterruptedException | IOException ex) {
            throw new IllegalStateException("cannot connect to URI via HttpClient - " + uri, ex);
        }
    }

    @Test
    @DisplayName("1. Old-School approach with HttpURLConnection")
    void testListPackages_OldSchool(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());
        Map<String, String> paramsMap = mavenTypeParamsMap();
        URI uri = URI.create(URL_LIST_PACKAGES + urlParams(paramsMap));
        try {
            HttpURLConnection httpConn = (HttpURLConnection)uri.toURL().openConnection();
            httpConn.setRequestMethod("GET");
            headersMapStr().forEach(httpConn::setRequestProperty);
            httpConn.setConnectTimeout(HTTP_TIMEOUT_MS);
            httpConn.setReadTimeout(HTTP_TIMEOUT_MS);
            int httpStatus = httpConn.getResponseCode();
            assertThat(httpStatus).isEqualTo(200);
            String responseBody = IOUtils.toString(httpConn.getInputStream(), Charset.defaultCharset());
            verifyResponseBody(responseBody);
            httpConn.disconnect();
        } catch (IOException ioEx) {
            throw new IllegalStateException("cannot connect to URI via HttpURLConnection - " + uri, ioEx);
        }
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
        return "?" + params.map(GithubApiTest::urlEncode)
            .collect(Collectors.joining("&"));
    }

    public static String urlEncode(Map.Entry<String, String> paramPair) {
        return urlEncode(paramPair.getKey()) + "=" + urlEncode(paramPair.getValue());
    }

    public static String urlEncode(String str) {
        return URLEncoder.encode(str, Charset.defaultCharset());
    }
}
