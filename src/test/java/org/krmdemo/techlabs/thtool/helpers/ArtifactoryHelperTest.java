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
    private static final String latestInternal = "21.23.004";

    @Test
    void testGHPkgUrl() {
        assertThat(ah.getGHPkgUrl())
            .isEqualTo("https://github.com/krm-demo/core-utils/packages/2631343");
        assertThat(ah.ghPkgUrl(latestInternal))
            .isEqualTo("https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004");
    }

    @Test
    void testBadgeGHPkgUrl() {
//        System.out.println("badgeGHPkgLongUrl --> " + ah.getBadgeGHPkgLongUrl());
//        System.out.println("badgeGHPkgShortUrl --> " + ah.getBadgeGHPkgShortUrl());
        assertThat(ah.getBadgeGHPkgLongUrl()).isEqualTo("""
            https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6""");
        assertThat(ah.getBadgeGHPkgShortUrl()).isEqualTo("""
            https://img.shields.io/badge/GH--Packages-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6""");
    }

    @Test
    void testBadgeGHPkgVersionUrl() {
//        System.out.printf("badgeGHPkgLongUrl(%s) --> %s;%n", latestInternal, ah.badgeGHPkgLongUrl(latestInternal));
//        System.out.printf("badgeGHPkgShortUrl(%s) --> %s;%n", latestInternal, ah.badgeGHPkgShortUrl(latestInternal));
        assertThat(ah.badgeGHPkgLongUrl(latestInternal)).isEqualTo("""
            https://img.shields.io/badge/io.github.krm--demo.core--utils-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6""");
        assertThat(ah.badgeGHPkgShortUrl(latestInternal)).isEqualTo("""
            https://img.shields.io/badge/GH--Packages-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6""");
    }

    @Test
    void testGetBadgeGHPkgMD() {
        assertThat(ah.getBadgeGHPkgLongMD()).isEqualTo("""
            [![GitHub-Packages long](https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343)""");
        assertThat(ah.getBadgeGHPkgShortMD()).isEqualTo("""
            [![GitHub-Packages short](https://img.shields.io/badge/GH--Packages-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343 "GH-Package 'io.github.krm-demo.core-utils'")""");
    }

    @Test
    void testBadgeGHPkgVersionMD() {
        assertThat(ah.badgeGHPkgLongMD(latestInternal)).startsWith("""
            [![GitHub-Packages long](https://img.shields.io/badge/io.github.krm--demo.core--utils""");
        assertThat(ah.badgeGHPkgShortMD(latestInternal)).startsWith("""
            [![GitHub-Packages short](https://img.shields.io/badge/GH--Packages-""");
    }

    @Test
    void testGetBadgeGHPkgHtml() {
        assertThat(ah.getBadgeGHPkgLongHtml()).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343">
              <img alt="a long badge to GH-Package" src="https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
        assertThat(ah.getBadgeGHPkgShortHtml()).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343" title="GH-Package 'io.github.krm-demo.core-utils'">
              <img alt="a short badge to GH-Package" src="https://img.shields.io/badge/GH--Packages-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
    }

    @Test
    void testGetBadgeGHPkgVersionHtml() {
        assertThat(ah.badgeGHPkgLongHtml(latestInternal)).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004">
              <img alt="a long badge to GH-Package" src="https://img.shields.io/badge/io.github.krm--demo.core--utils-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
        assertThat(ah.badgeGHPkgShortHtml(latestInternal)).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004" title="GH-Package 'io.github.krm-demo.core-utils':21.23.004">
              <img alt="a short badge to GH-Package" src="https://img.shields.io/badge/GH--Packages-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
    }
}
