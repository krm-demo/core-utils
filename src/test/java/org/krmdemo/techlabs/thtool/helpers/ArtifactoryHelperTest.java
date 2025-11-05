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
        // ------------ using currying interfaces: -----------------
        assertThat(ah.getGhPkgRootLong().getTargetUrl())
            .isEqualTo("https://github.com/krm-demo/core-utils/packages/2631343");
        assertThat(ah.getGhPkgRootShort().getTargetUrl())
            .isEqualTo("https://github.com/krm-demo/core-utils/packages/2631343");
        assertThat(ah.getGhPkgLong().of(latestInternal).getTargetUrl())
            .isEqualTo("https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004");
        assertThat(ah.getGhPkgShort().of(latestInternal).getTargetUrl())
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
        assertThat(ah.getGhPkgRootLong().getBadgeMD()).isEqualTo("""
            [![GitHub-Packages long](https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343 "GH-Package 'io.github.krm-demo.core-utils'")""");
        assertThat(ah.getGhPkgRootShort().getBadgeMD()).isEqualTo("""
            [![GitHub-Packages short](https://img.shields.io/badge/GH--Packages-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343 "GH-Package 'io.github.krm-demo.core-utils'")""");
    }

    @Test
    void testBadgeGHPkgVersionMD() {
        assertThat(ah.getGhPkgLong().badgeMD(latestInternal)).isEqualTo("""
            [![GitHub-Packages long](https://img.shields.io/badge/io.github.krm--demo.core--utils-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004 "GH-Package 'io.github.krm-demo.core-utils':21.23.004")""");
        assertThat(ah.getGhPkgShort().badgeMD(latestInternal)).isEqualTo("""
            [![GitHub-Packages short](https://img.shields.io/badge/GH--Packages-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004 "GH-Package 'io.github.krm-demo.core-utils':21.23.004")""");
        assertThat(ah.getGhPkgLong().of(latestInternal).getBadgeMD()).isEqualTo(ah.getGhPkgLong().badgeMD(latestInternal));
        assertThat(ah.getGhPkgShort().of(latestInternal).getBadgeMD()).isEqualTo(ah.getGhPkgShort().badgeMD(latestInternal));
    }

    @Test
    void testGetBadgeGHPkgHtml() {
        assertThat(ah.getGhPkgRootLong().getBadgeHtml()).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343" title="GH-Package 'io.github.krm-demo.core-utils'">
              <img alt="GitHub-Packages long" src="https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
        assertThat(ah.getGhPkgRootShort().getBadgeHtml()).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343" title="GH-Package 'io.github.krm-demo.core-utils'">
              <img alt="GitHub-Packages short" src="https://img.shields.io/badge/GH--Packages-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
    }

    @Test
    void testGetBadgeGHPkgVersionHtml() {
        assertThat(ah.getGhPkgLong().badgeHtml(latestInternal)).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004" title="GH-Package 'io.github.krm-demo.core-utils':21.23.004">
              <img alt="GitHub-Packages long" src="https://img.shields.io/badge/io.github.krm--demo.core--utils-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
        assertThat(ah.getGhPkgShort().badgeHtml(latestInternal)).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343?version=21.23.004" title="GH-Package 'io.github.krm-demo.core-utils':21.23.004">
              <img alt="GitHub-Packages short" src="https://img.shields.io/badge/GH--Packages-21.23.004-blue?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
        assertThat(ah.getGhPkgLong().of(latestInternal).getBadgeHtml()).isEqualTo(ah.getGhPkgLong().badgeHtml(latestInternal));
        assertThat(ah.getGhPkgShort().of(latestInternal).getBadgeHtml()).isEqualTo(ah.getGhPkgShort().badgeHtml(latestInternal));
        assertThat(ah.getGhPkgLong().of(minorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgLong().badgeHtml(latestInternal));
        assertThat(ah.getGhPkgShort().of(minorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgShort().badgeHtml(latestInternal));
        assertThat(ah.getGhPkgLong().of(majorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgLong().badgeHtml(latestInternal));
        assertThat(ah.getGhPkgShort().of(majorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgShort().badgeHtml(latestInternal));
        // looks like it's OK to get rid of following:
        assertThat(ah.getGhPkgLong().of(minorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgLong().badgeHtml(minorGroup));
        assertThat(ah.getGhPkgShort().of(minorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgShort().badgeHtml(minorGroup));
        assertThat(ah.getGhPkgLong().of(majorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgLong().badgeHtml(majorGroup));
        assertThat(ah.getGhPkgShort().of(majorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgShort().badgeHtml(majorGroup));
    }
}
