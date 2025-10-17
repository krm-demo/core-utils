package org.krmdemo.techlabs.thtool.helpers;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR__AS_FILE;
import static org.mockito.Mockito.when;

/**
 * A unit-test for <b>{@code th-tool}</b>-helper {@link GithubBadgeHelper}.
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

    @Test
    void testBadgeJavaDoc_MajorGroup() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assertThat(gbh.badgeUrlJavaDoc(majorGroup("12.34"))).isEqualTo(
            "https://img.shields.io/badge/core--utils-12.34-blue?logo=github&logoColor=f8981d&labelColor=4D7A97");
        assertThat(gbh.badgeJavaDocHTML(majorGroup("21.15"))).isEqualTo("""
            <a href="https://krm-demo.github.io/core-utils/core-utils-21.15">
              <img alt="a badge to the GitHub-project 'core-utils' of git-tag '21.15'" src="https://img.shields.io/badge/core--utils-21.15-blue?logo=github&logoColor=f8981d&labelColor=4D7A97" />
            </a>""");
    }

    @Test
    void testBadgeJavaDoc_MinorGroup() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assertThat(gbh.badgeUrlJavaDoc(minorGroup("12.34.567"))).isEqualTo(
            "https://img.shields.io/badge/core--utils-12.34.567-blue?logo=github&logoColor=f8981d&labelColor=4D7A97");
        assertThat(gbh.badgeJavaDocHTML(minorGroup("21.15.001"))).isEqualTo("""
            <a href="https://krm-demo.github.io/core-utils/core-utils-21.15.001">
              <img alt="a badge to the GitHub-project 'core-utils' of git-tag '21.15.001'" src="https://img.shields.io/badge/core--utils-21.15.001-blue?logo=github&logoColor=f8981d&labelColor=4D7A97" />
            </a>""");
    }

    @Test
    void testBadgeGitHub_MajorGroup() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assertThat(gbh.badgeUrlGitHub(majorGroup("12.34"))).isEqualTo(
            "https://img.shields.io/badge/core--utils-12.34-blue?logo=github&logoColor=white&labelColor=black");
        assertThat(gbh.badgeGitHubHTML(majorGroup("21.15"))).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/tree/21.15">
              <img alt="a badge to the GitHub-project 'core-utils' of git-tag '21.15'" src="https://img.shields.io/badge/core--utils-21.15-blue?logo=github&logoColor=white&labelColor=black" />
            </a>""");
    }

    @Test
    void testBadgeGitHub_MinorGroup() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assertThat(gbh.badgeUrlGitHub(minorGroup("12.34.567"))).isEqualTo(
            "https://img.shields.io/badge/core--utils-12.34.567-blue?logo=github&logoColor=white&labelColor=black");
        assertThat(gbh.badgeGitHubHTML(minorGroup("21.15.001"))).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/tree/21.15.001">
              <img alt="a badge to the GitHub-project 'core-utils' of git-tag '21.15.001'" src="https://img.shields.io/badge/core--utils-21.15.001-blue?logo=github&logoColor=white&labelColor=black" />
            </a>""");
    }

    @Test
    void testBadgeCommit() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        CommitInfo commitInfo = new CommitInfo(mockRevCommit("32b8955806a5e53e5f4065f738555d1aefeacf0b"));
        assertThat(gbh.badgeCommit(commitInfo)).isEqualTo("""
            <a href="https://github.com/krm-demo/core-utils/commit/32b8955806a5e53e5f4065f738555d1aefeacf0b">
              <svg aria-hidden="true" focusable="false"
                   viewBox="0 0 16 16" width="16" height="16"
                   stroke="#59636e" overflow="visible" style="vertical-align:text-bottom">
                <path d="M11.93 8.5a4.002 4.002 0 0 1-7.86 0H.75a.75.75 0 0 1 0-1.5h3.32a4.002 4.002 0 0 1 7.86 0h3.32a.75.75 0 0 1 0 1.5Zm-1.43-.75a2.5 2.5 0 1 0-5 0 2.5 2.5 0 0 0 5 0Z"></path>
              </svg>
              <code>32b8955</code>
            </a>""");
    }

    // --------------------------------------------------------------------------------------------

    @Test
    void testCommitGroup_toString() {
        System.out.println("---- ---- minorGroup(...).toString() test: ---- ----");
        System.out.println(minorGroup("12.34"));
        System.out.println(minorGroup("12.34.567"));
        assertThat(minorGroup("12.34").versionTag() + "").isEqualTo("12.34");
        assertThat(minorGroup("12.34.567").versionTag() + "").isEqualTo("12.34.567");
        System.out.println("---- ---- majorGroup(...).toString() test: ---- ----");
        System.out.println(majorGroup("12.34"));
        System.out.println(majorGroup("56.78"));
        assertThat(majorGroup("12.34").versionTag() + "").isEqualTo("12.34");
        assertThat(majorGroup("56.78").versionTag() + "").isEqualTo("56.78");
        System.out.println("---- ---- -------------------------------- ---- ----");
    }

    private static CommitGroupMajor majorGroup(String versionStr) {
        return new CommitGroupMajor(() -> minorGroup(versionStr));
    }

    private static CommitGroupMinor minorGroup(String versionStr) {
        return new CommitGroupMinor() {
            @Override
            public VersionTag versionTag() {
                return VersionTag.parse(versionStr);
            }
        };
    }

    // --------------------------------------------------------------------------------------------

    @SuppressWarnings("SameParameterValue")
    private static RevCommit mockRevCommit(String commitID) {
        return mockRevCommit(commitID,
            String.format("some test-message for mock-commid #%s",  commitID.substring(0, 7)));
    }

    private static RevCommit mockRevCommit(String commitID, String message) {
        ObjectId mockObjectId = Mockito.mock(ObjectId.class);
        when(mockObjectId.getName()).thenReturn(commitID);

        PersonIdent authorIdent = Mockito.mock(PersonIdent.class);
        when(authorIdent.getName()).thenReturn("test-author");
        when(authorIdent.getEmailAddress()).thenReturn("test.author@junit5.com");
        PersonIdent commiterIdent = Mockito.mock(PersonIdent.class);
        when(commiterIdent.getName()).thenReturn("test-commiter");
        when(commiterIdent.getEmailAddress()).thenReturn("test.commiter@junit5.com");

        RevCommit mockRevCommit = Mockito.mock(RevCommit.class);
        when(mockRevCommit.getAuthorIdent()).thenReturn(authorIdent);
        when(mockRevCommit.getCommitterIdent()).thenReturn(commiterIdent);
        when(mockRevCommit.getShortMessage()).thenReturn(message);
        when(mockRevCommit.getFullMessage()).thenReturn(message);
        when(mockRevCommit.getId()).thenReturn(mockObjectId);
        return mockRevCommit;
    }
}
