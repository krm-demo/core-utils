package org.krmdemo.techlabs.ghapi;

import javassist.tools.Dump;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedSet;

public class PagingResultTest {

    final String headerLink_01 = """
        <https://api.github.com/user/packages/maven/io.github.krm-demo.core-utils/versions?page=2&per_page=40&package_type=maven>; rel="next", \
        <https://api.github.com/user/packages/maven/io.github.krm-demo.core-utils/versions?page=2&per_page=40&package_type=maven>; rel="last"]""";

    final String headerLink_02 = """
        <https://api.github.com/user/packages/maven/io.github.krm-demo.core-utils/versions?page=21&per_page=50&package_type=maven>; rel="next", \
        <https://api.github.com/user/packages/maven/io.github.krm-demo.core-utils/versions?per_page=xxx&package_type=maven>; rel="la-la-la", \
        <https://api.github.com/user/packages/maven/io.github.krm-demo.core-utils/versions?page=123&per_page=60&package_type=maven>; rel="last"]""";

    @Test
    void testRelToUri_01() {
        Map<String, PagingResult.LinkUri> relToLinkUri = PagingResult.relToLinkUri(headerLink_01);
        assertThat(DumpUtils.dumpAsJsonTxt(relToLinkUri)).isEqualTo("""
            {
              "next": {
                "pageNum": "2",
                "perPage": "40"
              },
              "last": {
                "pageNum": "2",
                "perPage": "40"
              }
            }""");
    }

    @Test
    void testRelToUri_02() {
        Map<String, PagingResult.LinkUri> relToLinkUri = PagingResult.relToLinkUri(headerLink_02);
        assertThat(DumpUtils.dumpAsJsonTxt(relToLinkUri)).isEqualTo("""
            {
              "next": {
                "pageNum": "21",
                "perPage": "50"
              },
              "la-la-la": {
                "pageNum": null,
                "perPage": null
              },
              "last": {
                "pageNum": "123",
                "perPage": "60"
              }
            }""");
    }
}
