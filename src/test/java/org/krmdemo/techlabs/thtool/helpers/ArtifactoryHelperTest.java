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
    private static final CommitGroupMinor minorGroup = MockRevCommitUtils.minorGroup(latestInternal);
    private static final CommitGroupMajor majorGroup = MockRevCommitUtils.majorGroup(latestInternal);

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
        assertThat(ah.badgeGHPkgLongUrl(minorGroup)).isEqualTo(ah.badgeGHPkgLongUrl(latestInternal));
        assertThat(ah.badgeGHPkgLongUrl(majorGroup)).isEqualTo(ah.badgeGHPkgLongUrl(latestInternal));
        assertThat(ah.badgeGHPkgShortUrl(latestInternal)).isEqualTo("""
            https://img.shields.io/badge/GH--Packages-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6""");
        assertThat(ah.badgeGHPkgShortUrl(minorGroup)).isEqualTo(ah.badgeGHPkgShortUrl(latestInternal));
        assertThat(ah.badgeGHPkgShortUrl(majorGroup)).isEqualTo(ah.badgeGHPkgShortUrl(latestInternal));
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
        assertThat(ah.badgeGHPkgLongMD(latestInternal)).isEqualTo("""
            [![GitHub-Packages long](https://img.shields.io/badge/io.github.krm--demo.core--utils-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004)""");
        assertThat(ah.badgeGHPkgLongMD(minorGroup)).isEqualTo(ah.badgeGHPkgLongMD(latestInternal));
        assertThat(ah.badgeGHPkgLongMD(majorGroup)).isEqualTo(ah.badgeGHPkgLongMD(latestInternal));
        assertThat(ah.badgeGHPkgShortMD(latestInternal)).isEqualTo("""
            [![GitHub-Packages short](https://img.shields.io/badge/GH--Packages-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004 "GH-Package 'io.github.krm-demo.core-utils':21.23.004")""");
        assertThat(ah.badgeGHPkgShortMD(minorGroup)).isEqualTo(ah.badgeGHPkgShortMD(latestInternal));
        assertThat(ah.badgeGHPkgShortMD(majorGroup)).isEqualTo(ah.badgeGHPkgShortMD(latestInternal));
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
        assertThat(ah.badgeGHPkgLongHtml(minorGroup)).isEqualTo(ah.badgeGHPkgLongHtml(latestInternal));
        assertThat(ah.badgeGHPkgLongHtml(majorGroup)).isEqualTo(ah.badgeGHPkgLongHtml(latestInternal));
        assertThat(ah.badgeGHPkgShortHtml(latestInternal)).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004" title="GH-Package 'io.github.krm-demo.core-utils':21.23.004">
              <img alt="a short badge to GH-Package" src="https://img.shields.io/badge/GH--Packages-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
        assertThat(ah.badgeGHPkgShortHtml(minorGroup)).isEqualTo(ah.badgeGHPkgShortHtml(latestInternal));
        assertThat(ah.badgeGHPkgShortHtml(majorGroup)).isEqualTo(ah.badgeGHPkgShortHtml(latestInternal));
    }
}
