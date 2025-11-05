package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.techlabs.core.utils.CoreFileUtils;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.krmdemo.techlabs.core.dump.DumpUtils.dumpAsYamlTxt;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR__AS_FILE;
import static org.krmdemo.techlabs.thtool.helpers.MockRevCommitUtils.majorGroup;
import static org.krmdemo.techlabs.thtool.helpers.MockRevCommitUtils.minorGroup;
import static org.krmdemo.techlabs.thtool.helpers.MockRevCommitUtils.mockRevCommit;

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
        assertThat(gbh.getBadgeReleaseCatalogMD()).isEqualTo("""
            [![Release-Catalog](https://img.shields.io/badge/Release_Catalog-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97)](https://krm-demo.github.io/core-utils)""");
        assertThat(gbh.getBadgeReleaseCatalogHTML()).isEqualTo("""
            <a href="https://krm-demo.github.io/core-utils" class="release-catalog-badge-link">
              <img alt="a badge to 'Release Catalog'" src="https://img.shields.io/badge/Release_Catalog-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97" class="release-catalog-badge"/>
            </a>""");
    }

    @Test
    void testBadgeHomeJavaDocHTML() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        assertThat(gbh.getBadgeHomeJavaDocHTML()).isNotBlank();
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
            \\[!\\[Snapshot-Version]\\(https://img.shields.io/badge/core--utils-21\\.\\d\\d.\\d\\d\\d--SNAPSHOT-blue\\?logo=github&logoColor=white&labelColor=black\\)]\\(https://github.com/krm-demo/core-utils/tree/main\\)""");
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
    void testCustomLogoBadge_GIF() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        String badgeUrlTxt = gbh.badgeUrlShiedsIO("left-part", "right-part", "blue",
            Path.of(".github/images/jacoco/jacoco-reports.gif").toFile(),
            "f8981d", // <-- this color corresponds "--selected-background-color" CSS-variable at JavDoc-site
            "4D7A97" // <-- this color corresponds "--navbar-background-color" CSS-variable at JavDoc-site
        );
        assertThat(badgeUrlTxt)
            .isNotBlank()
            .startsWith("https://img.shields.io/badge/left--part-right--part-blue?logo=data%3Aimage%2Fgif%3Bbase64%2CR0lGODlhEAA")
            .endsWith("LWnCykoKSopvScnDEUKBgkJCMYJBwcKTM1FQQA7&logoColor=f8981d&labelColor=4D7A97")
            .hasSize(725);
    }

    @Test
    void testCustomLogoBadge_PNG() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        String badgeUrlTxt = gbh.badgeUrlShiedsIO("left-part", "right-part", "gray",
            Path.of(".github/images/opentest4j/opentest4j-logo--48.png").toFile(),
            "f8981d", // <-- this color corresponds "--selected-background-color" CSS-variable at JavDoc-site
            "4D7A97" // <-- this color corresponds "--navbar-background-color" CSS-variable at JavDoc-site
        );
        assertThat(badgeUrlTxt)
            .isNotBlank()
            .startsWith("https://img.shields.io/badge/left--part-right--part-gray?logo=data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAA")
            .endsWith("AAD%2F%2FxfuVaicHZfQAAAAAElFTkSuQmCC&logoColor=f8981d&labelColor=4D7A97")
            .hasSize(3555);
    }

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

    // --------------------------------------------------------------------------------------------

    /**
     * JVM entry-point to play with different badges (that have custom image-data) without garbage in JUnit-output
     *
     * @param args unused command-line arguments
     */
    public static void main(String... args) {
        System.out.println("----- data-images and badges for JaCoCo PNG-files: -----");
        File[] jacocoFilesArr = Stream.of(
            "jacoco-logo.png", // <-- Note! the badge-URL is not working because the size is too large
            "jacoco-logo--48.png", // <-- Note! the badge-URL is not working because the size is too large as well
            "jacoco-logo--32.png",
            "jacoco-logo--24.png",
            "jacoco-reports.gif"
        ).map(name -> Path.of(".github/images/jacoco", name).toFile()).toArray(File[]::new);
        System.out.println(dumpAsYamlTxt(jacocoFilesArr));
        dumpImageDataAndBadges(jacocoFilesArr);

        System.out.println("----- data-images and badges for 'Open-Test-Alliance'-logo PNG-files: -----");
        File[] opentest4jFilesArr = Stream.of(
            "opentest4j-logo--200.png",
            "opentest4j-logo--48.png",
            "opentest4j-logo--32.png",
            "opentest4j-logo--24.png"
        ).map(name -> Path.of(".github/images/opentest4j", name).toFile()).toArray(File[]::new);
        System.out.println(dumpAsYamlTxt(opentest4jFilesArr));
        dumpImageDataAndBadges(opentest4jFilesArr);

        System.out.println("----- data-images and badges for 'Maven-Central'-logo SVG-files: -----");
        dumpMavenCentralBadge();

        System.out.println("----- data-images and badges for 'MVN-Repository'-logo SVG-files: -----");
        dumpMvnRepoBadge_3_letters();
        dumpMvnRepoBadge_1_letter();
    }

    private static void dumpImageDataAndBadges(File... pathArr) {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        for (File imageFile : pathArr) {
            String imageData64 = CoreFileUtils.imageFileData64(imageFile);
            System.out.println(imageFile + "(bas64) --> '" + imageData64 + "'");
            System.out.println(imageFile + "(.url.) --> '" + URLEncoder.encode(imageData64, StandardCharsets.UTF_8) + "'");
            String badgeUrlTxt = gbh.badgeUrlShiedsIO("left-part", "right-part",
                "B0E0E6", // <-- this color is called "PowderBlue" at https://htmlcolorcodes.com/color-names/
                imageFile,
                "f8981d", // <-- this color corresponds "--selected-background-color" CSS-variable at JavDoc-site
                "4D7A97" // <-- this color corresponds "--navbar-background-color" CSS-variable at JavDoc-site
            );
            System.out.println(imageFile + "(badge) --> '" + badgeUrlTxt + "'");
            System.out.println(imageFile + "(badge) --> badgeUrlTxt.size() = " + badgeUrlTxt.length());
            System.out.println("----------------------");
        }
    }

    private static void dumpMavenCentralBadge() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        File imageFile = Path.of(".github/images/maven-central-logo-short.svg").toFile();
        String imageData64 = CoreFileUtils.imageFileData64(imageFile);
        System.out.println(imageFile + "(bas64) --> '" + imageData64 + "'");
        System.out.println(imageFile + "(.url.) --> '" + URLEncoder.encode(imageData64, StandardCharsets.UTF_8) + "'");
        String badgeUrlTxt = gbh.badgeUrlShiedsIO("maven-central", "21.23",
            "blue",
            imageFile,
            "2A4566", // <-- this color corresponds background of "maven-central" site
            "2A4566" // <-- this color corresponds background of "maven-central" site
        );
        System.out.println(imageFile + "(badge) --> '" + badgeUrlTxt + "'");
        System.out.println(imageFile + "(badge) --> badgeUrlTxt.size() = " + badgeUrlTxt.length());
        System.out.println("----------------------");
    }

    private static void dumpMvnRepoBadge_3_letters() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        File imageFile = Path.of(".github/images/mvnrepo-3-letters.svg").toFile();
        String imageData64 = CoreFileUtils.imageFileData64(imageFile);
        System.out.println(imageFile + "(bas64) --> '" + imageData64 + "'");
        System.out.println(imageFile + "(.url.) --> '" + URLEncoder.encode(imageData64, StandardCharsets.UTF_8) + "'");
        String badgeUrlTxt = gbh.badgeUrlShiedsIO("mvn-repo", "21.23",
            "blue",
            imageFile,
            "white", // <-- this color corresponds background of "maven-central" site
            "white" // <-- this color corresponds background of "maven-central" site
        );
        System.out.println(imageFile + "(badge) --> '" + badgeUrlTxt + "'");
        System.out.println(imageFile + "(badge) --> badgeUrlTxt.size() = " + badgeUrlTxt.length());
        System.out.println("----------------------");
    }

    private static void dumpMvnRepoBadge_1_letter() {
        GithubBadgeHelper gbh = GithubBadgeHelper.fromCtxLazy(ttCtx);
        File imageFile = Path.of(".github/images/mvnrepo-1-letter.svg").toFile();
        String imageData64 = CoreFileUtils.imageFileData64(imageFile);
        System.out.println(imageFile + "(bas64) --> '" + imageData64 + "'");
        System.out.println(imageFile + "(.url.) --> '" + URLEncoder.encode(imageData64, StandardCharsets.UTF_8) + "'");
        String badgeUrlTxt = gbh.badgeUrlShiedsIO("mvn-repo", "21.23",
            "blue",
            imageFile,
            "2A4566", // <-- this color corresponds background of "maven-central" site
            "2A4566" // <-- this color corresponds background of "maven-central" site
        );
        System.out.println(imageFile + "(badge) --> '" + badgeUrlTxt + "'");
        System.out.println(imageFile + "(badge) --> badgeUrlTxt.size() = " + badgeUrlTxt.length());
        System.out.println("----------------------");
    }
}

