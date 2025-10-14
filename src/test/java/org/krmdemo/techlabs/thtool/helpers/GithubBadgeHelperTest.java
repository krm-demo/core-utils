package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR__AS_FILE;

/**
 * A unit-test to <b>{@code th-tool}</b>-helper {@link GithubBadgeHelper}.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class GithubBadgeHelperTest {

    private static final ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();

    @BeforeAll
    static void beforeAll() {
        ttCtx.processDirectory(DEFAULT_VARS_DIR__AS_FILE);
    }

    @Test
    void testBadgePassing() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assertThat(gbh.getBadgeBuildPassing()).isEqualTo("""
            [![on-main-push](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml/badge.svg?event=push)](https://github.com/krm-demo/core-utils/actions/workflows/on-main-push.yml)""");
    }

    @Test
    void testBadgeReleaseCatalog() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assertThat(gbh.getBadgeUrlReleaseCatalog()).isEqualTo(
            "https://img.shields.io/badge/Release_Catalog-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97");
        assertThat(gbh.getBadgeReleaseCatalog()).isEqualTo("""
            [![Release-Catalog](https://img.shields.io/badge/Release_Catalog-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97)](https://krm-demo.github.io/core-utils/)""");
    }

    @Test
    void testBadgeLatestPublicJavaDoc() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assumeThat(gbh.isLatestPublicAvailable()).isTrue(); // <-- in some initial cases it's not available
        assertThat(gbh.getBadgeUrlLatestPublicJavaDoc()).matches(
            "https://img.shields.io/badge/core--utils-21\\.\\d\\d-blue\\?logo=github&logoColor=f8981d&labelColor=4D7A97");
        assertThat(gbh.getBadgeLatestPublicJavaDocMD()).matches("""
            \\[!\\[Latest-Public]\\(https://img.shields.io/badge/core--utils-21\\.\\d\\d-blue\\?logo=github&logoColor=f8981d&labelColor=4D7A97\\)]\\(https://krm-demo.github.io/core-utils/core-utils-21\\.\\d\\d\\)""");
        assertThat(gbh.getBadgeLatestPublicJavaDocHTML()).matches("""
            (?s).*https://img.shields.io/badge/core--utils-21\\.\\d\\d-blue\\?logo=github&logoColor=f8981d&labelColor=4D7A97.*""");
    }

    @Test
    void testBadgeLatestInternalJavaDoc() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assumeThat(gbh.isLatestInternalAvailable()).isTrue(); // <-- in some cases it's not available (PUBLIC-release)
        assertThat(gbh.getBadgeUrlLatestInternalJavaDoc()).matches(
            "https://img.shields.io/badge/core--utils-21\\.\\d\\d\\.\\d\\d\\d-blue\\?logo=github&logoColor=f8981d&labelColor=4D7A97");
        assertThat(gbh.getBadgeLatestInternalJavaDocMD()).matches("""
            \\[!\\[Latest-Internal]\\(https://img.shields.io/badge/core--utils-21\\.\\d\\d.\\d\\d\\d-blue\\?logo=github&logoColor=f8981d&labelColor=4D7A97\\)]\\(https://krm-demo.github.io/core-utils/core-utils-21\\.\\d\\d\\.\\d\\d\\d\\)""");
        assertThat(gbh.getBadgeLatestInternalJavaDocHTML()).matches("""
            (?s).*https://img.shields.io/badge/core--utils-21\\.\\d\\d.\\d\\d\\d-blue\\?logo=github&logoColor=f8981d&labelColor=4D7A97.*""");
    }

    @Test
    void testBadgeSnapshotJavaDoc() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assumeThat(gbh.isMavenSnapshot()).isTrue(); // <-- in some cases it's not available (PUBLIC-release and INTERNAL-release)
        assertThat(gbh.getBadgeUrlSnapshotJavaDoc()).matches(
            "https://img.shields.io/badge/core--utils-21\\.\\d\\d\\.\\d\\d\\d--SNAPSHOT-blue\\?logo=github&logoColor=f8981d&labelColor=4D7A97");
        assertThat(gbh.getBadgeSnapshotJavaDocMD()).matches("""
            \\[!\\[Snapshot-Version]\\(https://img.shields.io/badge/core--utils-21\\.\\d\\d.\\d\\d\\d--SNAPSHOT-blue\\?logo=github&logoColor=f8981d&labelColor=4D7A97\\)]\\(https://krm-demo.github.io/core-utils/core-utils-21\\.\\d\\d\\.\\d\\d\\d-SNAPSHOT\\)""");
        assertThat(gbh.getBadgeSnapshotJavaDocHTML()).matches("""
            (?s).*https://img.shields.io/badge/core--utils-21\\.\\d\\d.\\d\\d\\d--SNAPSHOT-blue\\?logo=github&logoColor=f8981d&labelColor=4D7A97.*""");
    }

    @Test
    void testBadgeLatestPublicGitHub() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assumeThat(gbh.isLatestPublicAvailable()).isTrue(); // <-- in some initial cases it's not available
        assertThat(gbh.getBadgeUrlLatestPublicGitHub()).matches(
            "https://img.shields.io/badge/core--utils-21\\.\\d\\d-blue\\?logo=github&logoColor=white&labelColor=black");
        assertThat(gbh.getBadgeLatestPublicGitHubMD()).matches("""
            \\[!\\[Latest-Public]\\(https://img.shields.io/badge/core--utils-21\\.\\d\\d-blue\\?logo=github&logoColor=white&labelColor=black\\)]\\(https://github.com/krm-demo/core-utils/tree/21\\.\\d\\d\\)""");
        assertThat(gbh.getBadgeLatestPublicGitHubHTML()).matches("""
            (?s).*https://img.shields.io/badge/core--utils-21\\.\\d\\d-blue\\?logo=github&logoColor=white&labelColor=black.*""");
    }

    @Test
    void testBadgeLatestInternalGitHub() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assumeThat(gbh.isLatestInternalAvailable()).isTrue(); // <-- in some cases it's not available (PUBLIC-release)
        assertThat(gbh.getBadgeUrlLatestInternalGitHub()).matches(
            "https://img.shields.io/badge/core--utils-21\\.\\d\\d\\.\\d\\d\\d-blue\\?logo=github&logoColor=white&labelColor=black");
        assertThat(gbh.getBadgeLatestInternalGitHubMD()).matches("""
            \\[!\\[Latest-Internal]\\(https://img.shields.io/badge/core--utils-21\\.\\d\\d.\\d\\d\\d-blue\\?logo=github&logoColor=white&labelColor=black\\)]\\(https://github.com/krm-demo/core-utils/tree/21\\.\\d\\d\\.\\d\\d\\d\\)""");
        assertThat(gbh.getBadgeLatestInternalGitHubHTML()).matches("""
            (?s).*https://img.shields.io/badge/core--utils-21\\.\\d\\d.\\d\\d\\d-blue\\?logo=github&logoColor=white&labelColor=black.*""");
    }

    @Test
    void testBadgeSnapshotGitHub() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assumeThat(gbh.isMavenSnapshot()).isTrue(); // <-- in some cases it's not available (PUBLIC-release and INTERNAL-release)
        assertThat(gbh.getBadgeUrlSnapshotGitHub()).matches(
            "https://img.shields.io/badge/core--utils-21\\.\\d\\d\\.\\d\\d\\d--SNAPSHOT-blue\\?logo=github&logoColor=white&labelColor=black");
        assertThat(gbh.getBadgeSnapshotGitHubMD()).matches("""
            \\[!\\[Snapshot-Version]\\(https://img.shields.io/badge/core--utils-21\\.\\d\\d.\\d\\d\\d--SNAPSHOT-blue\\?logo=github&logoColor=white&labelColor=black\\)]\\(https://github.com/krm-demo/core-utils\\)""");
        assertThat(gbh.getBadgeSnapshotGitHubHTML()).matches("""
            (?s).*https://img.shields.io/badge/core--utils-21\\.\\d\\d.\\d\\d\\d--SNAPSHOT-blue\\?logo=github&logoColor=white&labelColor=black.*""");
    }
}
