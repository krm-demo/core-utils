package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test for <b>{@code th-tool}</b>-helper {@link ArtifactoryHelper}.
 */
public class ArtifactoryHelperTest {

    private static final ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();
    private static final ArtifactoryHelper ah = ArtifactoryHelper.fromCtxLazy(ttCtx);

    @Test
    void testBadgeGHPkgUrl() {
//        System.out.println("badgeGHPkgLongUrl --> " + ah.getBadgeGHPkgLongUrl());
//        System.out.println("badgeGHPkgShortUrl --> " + ah.getBadgeGHPkgShortUrl());
        assertThat(ah.getBadgeGHPkgLongUrl()).isEqualTo("""
            https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=white&labelColor=black""");
        assertThat(ah.getBadgeGHPkgShortUrl()).isEqualTo("""
            https://img.shields.io/badge/GH--Packages-b0e0e6?logo=github&logoColor=white&labelColor=black""");
    }

    @Test
    void testBadgeGHPkgMD() {
        assertThat(ah.getBadgeGHPkgLongMD()).isEqualTo("""
            [![GitHub-Packages long](https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=white&labelColor=black)](https://github.com/krm-demo/core-utils/packages/2631343)""");
        assertThat(ah.getBadgeGHPkgShortMD()).isEqualTo("""
            [![GitHub-Packages short](https://img.shields.io/badge/GH--Packages-b0e0e6?logo=github&logoColor=white&labelColor=black)](https://github.com/krm-demo/core-utils/packages/2631343 "GH-Package 'io.github.krm-demo.core-utils'")""");
    }
}
