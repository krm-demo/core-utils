package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test for <b>{@code th-tool}</b>-helper {@link JavaDocHelper}.
 */
public class JavaDocHelperTest {

    private static final ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();

    @Test
    void testNavBarRight() {
        JavaDocHelper jdh = JavaDocHelper.fromCtxLazy(ttCtx);
        assertThat(jdh.getNavBarRight()).isEqualTo("""
            <a href="https://krm-demo.github.io/core-utils/" class="release-catalog-badge-link">
              <img alt="a badge to 'Release Catalog'" src="https://img.shields.io/badge/Release_Catalog-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97" class="release-catalog-badge"/>
            </a>""");
    }
}
