package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;
import org.krmdemo.techlabs.thtool.badges.BadgeProvider;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.thtool.helpers.MockUtils.mockMavenHelperInternal;
import static org.krmdemo.techlabs.thtool.helpers.MockUtils.mockMavenHelperPublic;
import static org.krmdemo.techlabs.thtool.helpers.MockUtils.mockMavenHelperSnapshot;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

/**
 * A unit-test for <b>{@code th-tool}</b>-helper {@link ArtifactoryHelper}.
 */
public class ArtifactoryHelperTest {

    private static final ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();
    private static final ArtifactoryHelper ah = ArtifactoryHelper.fromCtxLazy(ttCtx);

    private static final String latestPublic = "21.23";
    private static final String latestInternal = "21.23.004";

    private static final CommitGroupMajor majorGroup = MockUtils.majorGroup(latestPublic);
    private static final CommitGroupMinor minorGroup = MockUtils.minorGroup(latestInternal);

    @Test
    void testGHPkgUrl() {
        assertThat(ah.getGhPkgLong().getRoot().getTargetUrl())
            .isEqualTo("https://github.com/krm-demo/core-utils/packages/2631343");
        assertThat(ah.getGhPkgShort().getRoot().getTargetUrl())
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
        assertThat(ah.badgeGHPkgLongUrl("")).isEqualTo("""
            https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6""");
        assertThat(ah.badgeGHPkgShortUrl("")).isEqualTo("""
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
        assertThat(ah.getGhPkgLong().getRoot().getBadgeMD()).isEqualTo("""
            [![GitHub-Packages long](https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343 "GH-Package 'io.github.krm-demo.core-utils'")""");
        assertThat(ah.getGhPkgShort().getRoot().getBadgeMD()).isEqualTo("""
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
        assertThat(ah.getGhPkgLong().getRoot().getBadgeHtml()).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/packages/2631343" title="GH-Package 'io.github.krm-demo.core-utils'">
              <img alt="GitHub-Packages long" src="https://img.shields.io/badge/io.github.krm--demo.core--utils-b0e0e6?logo=github&logoColor=black&labelColor=b0e0e6" />
            </a>""");
        assertThat(ah.getGhPkgShort().getRoot().getBadgeHtml()).isEqualTo("""
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
        assertThat(ah.getGhPkgLong().of(minorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgLong().badgeHtml(minorGroup));
        assertThat(ah.getGhPkgShort().of(minorGroup).getBadgeHtml()).isEqualTo(ah.getGhPkgShort().badgeHtml(minorGroup));
    }

    @Test
    void testCurrentGHPkg() {
        // instances should be mocked before the static mocking of the class
        MavenHelper mhPublic = mockMavenHelperPublic();
        MavenHelper mhInternal = mockMavenHelperInternal();
        MavenHelper mhSnapshot = mockMavenHelperSnapshot();
        // a good example how to mock the static factory-method "MavenHelper.fromCtx":
        try (MockedStatic<MavenHelper> mavenHelperFactory = mockStatic(MavenHelper.class)) {
            mavenHelperFactory.when(() -> MavenHelper.fromCtx(any())).thenReturn(mhPublic);
            assertThat(ah.getCurrentGHPkg()).isSameAs(BadgeProvider.EMPTY);
            mavenHelperFactory.when(() -> MavenHelper.fromCtx(any())).thenReturn(mhSnapshot);
            assertThat(ah.getCurrentGHPkg()).isSameAs(BadgeProvider.EMPTY);
            mavenHelperFactory.when(() -> MavenHelper.fromCtx(any())).thenReturn(mhInternal);
            BadgeProvider ghPkg = ah.getCurrentGHPkg();
            assertThat(ghPkg).isNotSameAs(BadgeProvider.EMPTY);
            assertThat(ghPkg.getTargetUrl()).isEqualTo("https://github.com/krm-demo/core-utils/packages/2631343?version=21.xx.yyy");
            assertThat(ghPkg.getBadgeMD()).isEqualTo("""
                [![GitHub-Packages short](https://img.shields.io/badge/GH--Packages-21.xx.yyy-blue?logo=github&logoColor=black&labelColor=b0e0e6)](https://github.com/krm-demo/core-utils/packages/2631343?version=21.xx.yyy "GH-Package 'some.maven.project.group.mock-project-artifact':21.xx.yyy")""");
            assertThat(ghPkg.getBadgeHtml()).isEqualTo("""
                <a href="https://github.com/krm-demo/core-utils/packages/2631343?version=21.xx.yyy" title="GH-Package 'some.maven.project.group.mock-project-artifact':21.xx.yyy">
                  <img alt="GitHub-Packages short" src="https://img.shields.io/badge/GH--Packages-21.xx.yyy-blue?logo=github&logoColor=black&labelColor=b0e0e6" />
                </a>""");
        }
    }

    @Test
    void testMavenCentralUrl() {
        assertThat(ah.getMavenCentral().getRoot().getTargetUrl())
            .isEqualTo("https://central.sonatype.com");
        assertThat(ah.getMavenCentral().of(latestPublic).getTargetUrl())
            .isEqualTo("https://central.sonatype.com/artifact/io.github.krm-demo/core-utils/21.23");
        assertThat(ah.getMavenCentral().of(latestPublic).getTargetUrl())
            .isEqualTo(ah.getMavenCentral().of(majorGroup).getTargetUrl());
    }

    @Test
    void testMvnRepositoryUrl() {
        assertThat(ah.getMvnRepository().getRoot().getTargetUrl())
            .isEqualTo("https://mvnrepository.com");
        assertThat(ah.getMvnRepository().of(latestPublic).getTargetUrl())
            .isEqualTo("https://mvnrepository.com/artifact/io.github.krm-demo/core-utils/21.23");
        assertThat(ah.getMvnRepository().of(latestPublic).getTargetUrl())
            .isEqualTo(ah.getMvnRepository().of(majorGroup).getTargetUrl());
    }

    @Test
    void testMavenBadgesMD() {
        assertThat(ah.getMavenCentral().getRoot().getBadgeMD()).isEqualTo("""
            [![Maven-Central site](https://img.shields.io/badge/maven--central-415d7c?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyBpZD0iTGF5ZXJfMSIgZGF0YS1uYW1lPSJMYXllciAxIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDQwIDQ0Ij4KICA8ZGVmcz4KICAgIDxzdHlsZT4KICAgICAgLmNscy0xIHsKICAgICAgICBmaWxsOiAjZjE4OTAwOwogICAgICB9CiAgICAgIC5jbHMtMSwgLmNscy0yLCAuY2xzLTMgewogICAgICAgIHN0cm9rZS13aWR0aDogMTA7CiAgICAgIH0KICAgICAgLmNscy0yIHsKICAgICAgICBmaWxsOiAjZWY4NzAwOwogICAgICB9CiAgICA8L3N0eWxlPgogIDwvZGVmcz4KICA8cG9seWdvbiBjbGFzcz0iY2xzLTIiIHBvaW50cz0iMTkuMiA0My45IC4yIDMzIDAgMTEuMSAxOC45IDAgMzggMTAuOCAzOCAxMi41IDM2LjIgMTIuNSAzNiAxMi44IDM1LjUgMTIuNSAzNS4zIDEyLjUgMzUuMyAxMi40IDE4LjkgMy4xIDIuNyAxMi42IDIuOCAzMS41IDE5LjIgNDAuOCAzNS41IDMxLjIgMzUuNSAzMS4xIDM1LjcgMzEuMSAzNi4xIDMwLjggMzYuMyAzMS4xIDM4LjEgMzEuMSAzOC4xIDMyLjggMTkuMiA0My45Ii8%2BCiAgPGcgY2xhc3M9ImNscy0xIiA%2BCiAgICA8cGF0aCBkPSJNMjAuOCwxNS4xIGMyLjgsMCw0LjQsMS4yLDUuMywyLjRsLTIuMywyLjFjLS42LS45LTEuNi0xLjQtMi44LTEuNC0yLjEsMC0zLjYsMS42LTMuNiwzLjlzMS41LDMuOSwzLjYsMy45LDIuMi0uNiwyLjgtMS40bDIuMywyLjFjLS45LDEuMi0yLjYsMi40LTUuMywyLjQtNC4xLDAtNy4xLTIuOS03LjEtNy4xczMtNyw3LjEtN1oiLz4KICA8L2c%2BCjwvc3ZnPg%3D%3D&logoColor=415d7c&labelColor=415d7c)](https://central.sonatype.com "Maven-Central site")""");
        assertThat(ah.getMvnRepository().getRoot().getBadgeMD()).isEqualTo("""
            [![MVN-Repository site](https://img.shields.io/badge/mvn--repo-eee?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMzVweCIgaGVpZ2h0PSIzNXB4IiB2aWV3Qm94PSIwIDUgMzAgMjUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI%2BCiAgPGcgc3R5bGU9ImRpc3BsYXk6aW5saW5lIj4KICAgIDxwYXRoIHN0eWxlPSJkaXNwbGF5OmlubGluZTtmaWxsOiMyNzZiYzA7ZmlsbC1vcGFjaXR5OjE7c3Ryb2tlLXdpZHRoOi4xOTM4MjQiIGQ9Ik0wIDBoNjN2MzVIMHoiIC8%2BCiAgICA8ZwogICAgICBzdHlsZT0iZm9udC13ZWlnaHQ6NzAwO2ZvbnQtc2l6ZToyNC42MDY5cHg7Zm9udC1mYW1pbHk6J0dpbGwgU2Fucyc7LWlua3NjYXBlLWZvbnQtc3BlY2lmaWNhdGlvbjonR2lsbCBTYW5zLCBCb2xkJztmaWxsOiMwNGE7c3Ryb2tlLXdpZHRoOi4zMTgxOTMiPgogICAgICA8cGF0aAogICAgICAgIGQ9Ik0yNy44MjMgMjMuMDU1SDIyLjh2LTkuNjQ4bC00LjUzIDUuNTg3aC0uMzk2bC00LjU0MS01LjU4N3Y5LjY0OEg4LjQ1NFY2LjQwMmg0LjU1NGw1LjExOSA2LjI0OCA1LjE0Mi02LjI0OGg0LjU1NHoiCiAgICAgICAgc3R5bGU9ImZpbGw6I2ZmZiIgdHJhbnNmb3JtPSJzY2FsZSguODMxNTIgMS4yMDI2MikiIC8%2BCiAgICA8L2c%2BCiAgPC9nPgo8L3N2Zz4%3D&logoColor=eee&labelColor=eee)](https://mvnrepository.com "MVN-Repository site")""");
        assertThat(ah.getMavenCentral().getExampleXYZ().getBadgeMD()).isEqualTo("""
            [![Maven-Central site](https://img.shields.io/badge/maven--central-xx.yy.zzz-blue?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyBpZD0iTGF5ZXJfMSIgZGF0YS1uYW1lPSJMYXllciAxIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDQwIDQ0Ij4KICA8ZGVmcz4KICAgIDxzdHlsZT4KICAgICAgLmNscy0xIHsKICAgICAgICBmaWxsOiAjZjE4OTAwOwogICAgICB9CiAgICAgIC5jbHMtMSwgLmNscy0yLCAuY2xzLTMgewogICAgICAgIHN0cm9rZS13aWR0aDogMTA7CiAgICAgIH0KICAgICAgLmNscy0yIHsKICAgICAgICBmaWxsOiAjZWY4NzAwOwogICAgICB9CiAgICA8L3N0eWxlPgogIDwvZGVmcz4KICA8cG9seWdvbiBjbGFzcz0iY2xzLTIiIHBvaW50cz0iMTkuMiA0My45IC4yIDMzIDAgMTEuMSAxOC45IDAgMzggMTAuOCAzOCAxMi41IDM2LjIgMTIuNSAzNiAxMi44IDM1LjUgMTIuNSAzNS4zIDEyLjUgMzUuMyAxMi40IDE4LjkgMy4xIDIuNyAxMi42IDIuOCAzMS41IDE5LjIgNDAuOCAzNS41IDMxLjIgMzUuNSAzMS4xIDM1LjcgMzEuMSAzNi4xIDMwLjggMzYuMyAzMS4xIDM4LjEgMzEuMSAzOC4xIDMyLjggMTkuMiA0My45Ii8%2BCiAgPGcgY2xhc3M9ImNscy0xIiA%2BCiAgICA8cGF0aCBkPSJNMjAuOCwxNS4xIGMyLjgsMCw0LjQsMS4yLDUuMywyLjRsLTIuMywyLjFjLS42LS45LTEuNi0xLjQtMi44LTEuNC0yLjEsMC0zLjYsMS42LTMuNiwzLjlzMS41LDMuOSwzLjYsMy45LDIuMi0uNiwyLjgtMS40bDIuMywyLjFjLS45LDEuMi0yLjYsMi40LTUuMywyLjQtNC4xLDAtNy4xLTIuOS03LjEtNy4xczMtNyw3LjEtN1oiLz4KICA8L2c%2BCjwvc3ZnPg%3D%3D&logoColor=415d7c&labelColor=415d7c)](https://central.sonatype.com/artifact/io.github.krm-demo/core-utils/xx.yy.zzz "Maven-Central site for 'io.github.krm-demo:core-utils:xx.yy.zzz'")""");
        assertThat(ah.getMvnRepository().getExampleXYZ().getBadgeMD()).isEqualTo("""
            [![MVN-Repository site](https://img.shields.io/badge/mvn--repo-xx.yy.zzz-blue?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMzVweCIgaGVpZ2h0PSIzNXB4IiB2aWV3Qm94PSIwIDUgMzAgMjUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI%2BCiAgPGcgc3R5bGU9ImRpc3BsYXk6aW5saW5lIj4KICAgIDxwYXRoIHN0eWxlPSJkaXNwbGF5OmlubGluZTtmaWxsOiMyNzZiYzA7ZmlsbC1vcGFjaXR5OjE7c3Ryb2tlLXdpZHRoOi4xOTM4MjQiIGQ9Ik0wIDBoNjN2MzVIMHoiIC8%2BCiAgICA8ZwogICAgICBzdHlsZT0iZm9udC13ZWlnaHQ6NzAwO2ZvbnQtc2l6ZToyNC42MDY5cHg7Zm9udC1mYW1pbHk6J0dpbGwgU2Fucyc7LWlua3NjYXBlLWZvbnQtc3BlY2lmaWNhdGlvbjonR2lsbCBTYW5zLCBCb2xkJztmaWxsOiMwNGE7c3Ryb2tlLXdpZHRoOi4zMTgxOTMiPgogICAgICA8cGF0aAogICAgICAgIGQ9Ik0yNy44MjMgMjMuMDU1SDIyLjh2LTkuNjQ4bC00LjUzIDUuNTg3aC0uMzk2bC00LjU0MS01LjU4N3Y5LjY0OEg4LjQ1NFY2LjQwMmg0LjU1NGw1LjExOSA2LjI0OCA1LjE0Mi02LjI0OGg0LjU1NHoiCiAgICAgICAgc3R5bGU9ImZpbGw6I2ZmZiIgdHJhbnNmb3JtPSJzY2FsZSguODMxNTIgMS4yMDI2MikiIC8%2BCiAgICA8L2c%2BCiAgPC9nPgo8L3N2Zz4%3D&logoColor=eee&labelColor=eee)](https://mvnrepository.com/artifact/io.github.krm-demo/core-utils/xx.yy.zzz "MVN-Repository site for 'io.github.krm-demo:core-utils:xx.yy.zzz'")""");
    }

    @Test
    void testCurrentMavenCentral() {
        // instances should be mocked before the static mocking of the class
        MavenHelper mhPublic = mockMavenHelperPublic();
        MavenHelper mhInternal = mockMavenHelperInternal();
        MavenHelper mhSnapshot = mockMavenHelperSnapshot();
        // a good example how to mock the static factory-method "MavenHelper.fromCtx":
        try (MockedStatic<MavenHelper> mavenHelperFactory = mockStatic(MavenHelper.class)) {
            mavenHelperFactory.when(() -> MavenHelper.fromCtx(any())).thenReturn(mhInternal);
            assertThat(ah.getCurrentMavenCentral()).isSameAs(BadgeProvider.EMPTY);
            mavenHelperFactory.when(() -> MavenHelper.fromCtx(any())).thenReturn(mhSnapshot);
            assertThat(ah.getCurrentMavenCentral()).isSameAs(BadgeProvider.EMPTY);
            mavenHelperFactory.when(() -> MavenHelper.fromCtx(any())).thenReturn(mhPublic);
            BadgeProvider mavenCentral = ah.getCurrentMavenCentral();
            assertThat(mavenCentral).isNotSameAs(BadgeProvider.EMPTY);
            assertThat(mavenCentral.getTargetUrl()).isEqualTo("https://central.sonatype.com/artifact/mock.maven.project.group/mock-project-artifact/21.xx");
            assertThat(mavenCentral.getBadgeMD()).isEqualTo("""
                [![Maven-Central site](https://img.shields.io/badge/maven--central-21.xx-blue?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyBpZD0iTGF5ZXJfMSIgZGF0YS1uYW1lPSJMYXllciAxIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDQwIDQ0Ij4KICA8ZGVmcz4KICAgIDxzdHlsZT4KICAgICAgLmNscy0xIHsKICAgICAgICBmaWxsOiAjZjE4OTAwOwogICAgICB9CiAgICAgIC5jbHMtMSwgLmNscy0yLCAuY2xzLTMgewogICAgICAgIHN0cm9rZS13aWR0aDogMTA7CiAgICAgIH0KICAgICAgLmNscy0yIHsKICAgICAgICBmaWxsOiAjZWY4NzAwOwogICAgICB9CiAgICA8L3N0eWxlPgogIDwvZGVmcz4KICA8cG9seWdvbiBjbGFzcz0iY2xzLTIiIHBvaW50cz0iMTkuMiA0My45IC4yIDMzIDAgMTEuMSAxOC45IDAgMzggMTAuOCAzOCAxMi41IDM2LjIgMTIuNSAzNiAxMi44IDM1LjUgMTIuNSAzNS4zIDEyLjUgMzUuMyAxMi40IDE4LjkgMy4xIDIuNyAxMi42IDIuOCAzMS41IDE5LjIgNDAuOCAzNS41IDMxLjIgMzUuNSAzMS4xIDM1LjcgMzEuMSAzNi4xIDMwLjggMzYuMyAzMS4xIDM4LjEgMzEuMSAzOC4xIDMyLjggMTkuMiA0My45Ii8%2BCiAgPGcgY2xhc3M9ImNscy0xIiA%2BCiAgICA8cGF0aCBkPSJNMjAuOCwxNS4xIGMyLjgsMCw0LjQsMS4yLDUuMywyLjRsLTIuMywyLjFjLS42LS45LTEuNi0xLjQtMi44LTEuNC0yLjEsMC0zLjYsMS42LTMuNiwzLjlzMS41LDMuOSwzLjYsMy45LDIuMi0uNiwyLjgtMS40bDIuMywyLjFjLS45LDEuMi0yLjYsMi40LTUuMywyLjQtNC4xLDAtNy4xLTIuOS03LjEtNy4xczMtNyw3LjEtN1oiLz4KICA8L2c%2BCjwvc3ZnPg%3D%3D&logoColor=415d7c&labelColor=415d7c)](https://central.sonatype.com/artifact/mock.maven.project.group/mock-project-artifact/21.xx "Maven-Central site for 'mock.maven.project.group:mock-project-artifact:21.xx'")""");
            assertThat(mavenCentral.getBadgeHtml()).isEqualTo("""
                <a href="https://central.sonatype.com/artifact/mock.maven.project.group/mock-project-artifact/21.xx" title="Maven-Central site for 'mock.maven.project.group:mock-project-artifact:21.xx'">
                  <img alt="Maven-Central site" src="https://img.shields.io/badge/maven--central-21.xx-blue?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyBpZD0iTGF5ZXJfMSIgZGF0YS1uYW1lPSJMYXllciAxIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDQwIDQ0Ij4KICA8ZGVmcz4KICAgIDxzdHlsZT4KICAgICAgLmNscy0xIHsKICAgICAgICBmaWxsOiAjZjE4OTAwOwogICAgICB9CiAgICAgIC5jbHMtMSwgLmNscy0yLCAuY2xzLTMgewogICAgICAgIHN0cm9rZS13aWR0aDogMTA7CiAgICAgIH0KICAgICAgLmNscy0yIHsKICAgICAgICBmaWxsOiAjZWY4NzAwOwogICAgICB9CiAgICA8L3N0eWxlPgogIDwvZGVmcz4KICA8cG9seWdvbiBjbGFzcz0iY2xzLTIiIHBvaW50cz0iMTkuMiA0My45IC4yIDMzIDAgMTEuMSAxOC45IDAgMzggMTAuOCAzOCAxMi41IDM2LjIgMTIuNSAzNiAxMi44IDM1LjUgMTIuNSAzNS4zIDEyLjUgMzUuMyAxMi40IDE4LjkgMy4xIDIuNyAxMi42IDIuOCAzMS41IDE5LjIgNDAuOCAzNS41IDMxLjIgMzUuNSAzMS4xIDM1LjcgMzEuMSAzNi4xIDMwLjggMzYuMyAzMS4xIDM4LjEgMzEuMSAzOC4xIDMyLjggMTkuMiA0My45Ii8%2BCiAgPGcgY2xhc3M9ImNscy0xIiA%2BCiAgICA8cGF0aCBkPSJNMjAuOCwxNS4xIGMyLjgsMCw0LjQsMS4yLDUuMywyLjRsLTIuMywyLjFjLS42LS45LTEuNi0xLjQtMi44LTEuNC0yLjEsMC0zLjYsMS42LTMuNiwzLjlzMS41LDMuOSwzLjYsMy45LDIuMi0uNiwyLjgtMS40bDIuMywyLjFjLS45LDEuMi0yLjYsMi40LTUuMywyLjQtNC4xLDAtNy4xLTIuOS03LjEtNy4xczMtNyw3LjEtN1oiLz4KICA8L2c%2BCjwvc3ZnPg%3D%3D&logoColor=415d7c&labelColor=415d7c" />
                </a>""");
        }
    }

    @Test
    void testCurrentMvnRepository() {
        // instances should be mocked before the static mocking of the class
        MavenHelper mhPublic = mockMavenHelperPublic();
        MavenHelper mhInternal = mockMavenHelperInternal();
        MavenHelper mhSnapshot = mockMavenHelperSnapshot();
        // a good example how to mock the static factory-method "MavenHelper.fromCtx":
        try (MockedStatic<MavenHelper> mavenHelperFactory = mockStatic(MavenHelper.class)) {
            mavenHelperFactory.when(() -> MavenHelper.fromCtx(any())).thenReturn(mhInternal);
            assertThat(ah.getCurrentMvnRepository()).isSameAs(BadgeProvider.EMPTY);
            mavenHelperFactory.when(() -> MavenHelper.fromCtx(any())).thenReturn(mhSnapshot);
            assertThat(ah.getCurrentMvnRepository()).isSameAs(BadgeProvider.EMPTY);
            mavenHelperFactory.when(() -> MavenHelper.fromCtx(any())).thenReturn(mhPublic);
            BadgeProvider mvnRepo = ah.getCurrentMvnRepository();
            assertThat(mvnRepo).isNotSameAs(BadgeProvider.EMPTY);
            assertThat(mvnRepo.getTargetUrl()).isEqualTo("https://mvnrepository.com/artifact/mock.maven.project.group/mock-project-artifact/21.xx");
            assertThat(mvnRepo.getBadgeMD()).isEqualTo("""
                [![MVN-Repository site](https://img.shields.io/badge/mvn--repo-21.xx-blue?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMzVweCIgaGVpZ2h0PSIzNXB4IiB2aWV3Qm94PSIwIDUgMzAgMjUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI%2BCiAgPGcgc3R5bGU9ImRpc3BsYXk6aW5saW5lIj4KICAgIDxwYXRoIHN0eWxlPSJkaXNwbGF5OmlubGluZTtmaWxsOiMyNzZiYzA7ZmlsbC1vcGFjaXR5OjE7c3Ryb2tlLXdpZHRoOi4xOTM4MjQiIGQ9Ik0wIDBoNjN2MzVIMHoiIC8%2BCiAgICA8ZwogICAgICBzdHlsZT0iZm9udC13ZWlnaHQ6NzAwO2ZvbnQtc2l6ZToyNC42MDY5cHg7Zm9udC1mYW1pbHk6J0dpbGwgU2Fucyc7LWlua3NjYXBlLWZvbnQtc3BlY2lmaWNhdGlvbjonR2lsbCBTYW5zLCBCb2xkJztmaWxsOiMwNGE7c3Ryb2tlLXdpZHRoOi4zMTgxOTMiPgogICAgICA8cGF0aAogICAgICAgIGQ9Ik0yNy44MjMgMjMuMDU1SDIyLjh2LTkuNjQ4bC00LjUzIDUuNTg3aC0uMzk2bC00LjU0MS01LjU4N3Y5LjY0OEg4LjQ1NFY2LjQwMmg0LjU1NGw1LjExOSA2LjI0OCA1LjE0Mi02LjI0OGg0LjU1NHoiCiAgICAgICAgc3R5bGU9ImZpbGw6I2ZmZiIgdHJhbnNmb3JtPSJzY2FsZSguODMxNTIgMS4yMDI2MikiIC8%2BCiAgICA8L2c%2BCiAgPC9nPgo8L3N2Zz4%3D&logoColor=eee&labelColor=eee)](https://mvnrepository.com/artifact/mock.maven.project.group/mock-project-artifact/21.xx "MVN-Repository site for 'mock.maven.project.group:mock-project-artifact:21.xx'")""");
            assertThat(mvnRepo.getBadgeHtml()).isEqualTo("""
                <a href="https://mvnrepository.com/artifact/mock.maven.project.group/mock-project-artifact/21.xx" title="MVN-Repository site for 'mock.maven.project.group:mock-project-artifact:21.xx'">
                  <img alt="MVN-Repository site" src="https://img.shields.io/badge/mvn--repo-21.xx-blue?logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMzVweCIgaGVpZ2h0PSIzNXB4IiB2aWV3Qm94PSIwIDUgMzAgMjUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI%2BCiAgPGcgc3R5bGU9ImRpc3BsYXk6aW5saW5lIj4KICAgIDxwYXRoIHN0eWxlPSJkaXNwbGF5OmlubGluZTtmaWxsOiMyNzZiYzA7ZmlsbC1vcGFjaXR5OjE7c3Ryb2tlLXdpZHRoOi4xOTM4MjQiIGQ9Ik0wIDBoNjN2MzVIMHoiIC8%2BCiAgICA8ZwogICAgICBzdHlsZT0iZm9udC13ZWlnaHQ6NzAwO2ZvbnQtc2l6ZToyNC42MDY5cHg7Zm9udC1mYW1pbHk6J0dpbGwgU2Fucyc7LWlua3NjYXBlLWZvbnQtc3BlY2lmaWNhdGlvbjonR2lsbCBTYW5zLCBCb2xkJztmaWxsOiMwNGE7c3Ryb2tlLXdpZHRoOi4zMTgxOTMiPgogICAgICA8cGF0aAogICAgICAgIGQ9Ik0yNy44MjMgMjMuMDU1SDIyLjh2LTkuNjQ4bC00LjUzIDUuNTg3aC0uMzk2bC00LjU0MS01LjU4N3Y5LjY0OEg4LjQ1NFY2LjQwMmg0LjU1NGw1LjExOSA2LjI0OCA1LjE0Mi02LjI0OGg0LjU1NHoiCiAgICAgICAgc3R5bGU9ImZpbGw6I2ZmZiIgdHJhbnNmb3JtPSJzY2FsZSguODMxNTIgMS4yMDI2MikiIC8%2BCiAgICA8L2c%2BCiAgPC9nPgo8L3N2Zz4%3D&logoColor=eee&labelColor=eee" />
                </a>""");
        }
    }
}
