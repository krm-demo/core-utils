package org.krmdemo.techlabs.github;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.core.dump.PrintUtils;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;

/**
 * This unit-test is just a demo to make REST-requests using standard API from JDK.
 * <hr/>
 * The same things is performed from command-line using the bash-script: <pre>{@code
 * ...> ./.gfithub/scripts/curl--gh-list-packages.sh
 * }
 */
public class GithubApiTest {

    final static String URL_LIST_PACKAGES = "https://api.github.com/users/krm-demo/packages";
    final static String GITHUB_AUTH_TOKEN = StringUtils.reverse("6zYck0lz4EQpVDWqkQ2un0lVZLXC4V9wiE1L_phg");
    final static int HTTP_TIMEOUT_MS = 5_000;  // <-- 5 seconds for timeout

    @Test
    @DisplayName("Java-11 approach with HttpClient")
    void testListPackages_HttpClient(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());
        Map<String, String> params = linkedMap(nameValue("package_type", "maven"));
        URI uri = URI.create(URL_LIST_PACKAGES + urlParams(params));
        try (HttpClient client = HttpClient.newBuilder().build()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "token " + GITHUB_AUTH_TOKEN)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .timeout(Duration.ofMillis(HTTP_TIMEOUT_MS))
                .GET()
                .build();
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
    @DisplayName("Old-School approach with HttpURLConnection")
    void testListPackages_OldSchool(TestInfo testInfo) {
        System.out.printf("------ %s: ------%n", testInfo.getDisplayName());
        Map<String, String> params = linkedMap(nameValue("package_type", "maven"));
        URI uri = URI.create(URL_LIST_PACKAGES + urlParams(params));
        try {
            HttpURLConnection httpConn = (HttpURLConnection)uri.toURL().openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Authorization", "token " + GITHUB_AUTH_TOKEN);
            httpConn.setRequestProperty("Accept", "application/vnd.github+json");
            httpConn.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
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

    private void verifyResponseBody(String responseBody) {
        List<Object> responseList = JacksonUtils.jsonArrFromString(responseBody);
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
