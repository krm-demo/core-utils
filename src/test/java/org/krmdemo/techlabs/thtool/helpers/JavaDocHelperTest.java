package org.krmdemo.techlabs.thtool.helpers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR__AS_FILE;

/**
 * A unit-test for <b>{@code th-tool}</b>-helper {@link JavaDocHelper}.
 * <hr/>
 * This unit-test is working <b>only after generating JavaDoc-report</b> - otherwise the test-cases will be skipped.
 */
@Tag("integration-test")
public class JavaDocHelperTest {

    private static final ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();

    private static final Path PATH_DIR__JAVADOC_REPORT = Path.of(
        "target", "reports", "apidocs");

    @BeforeAll
    static void beforeAll() {
        ttCtx.processDirectory(DEFAULT_VARS_DIR__AS_FILE);
    }

    @Test
    void testOverviewDocTitle() {
        assumeTrue(Files.exists(PATH_DIR__JAVADOC_REPORT),
            String.format("input directory '%s' to process the JavaDoc-report does not exist",
                PATH_DIR__JAVADOC_REPORT));
        JavaDocHelper jdh = JavaDocHelper.fromCtxLazy(ttCtx);
        assertThat(jdh.getOverviewDocTitle()).contains("core-utils");
    }

    @Test
    void testNavBarRight_Contains_ReleaseCatalog() {
        assumeTrue(Files.exists(PATH_DIR__JAVADOC_REPORT),
            String.format("input directory '%s' to process the JavaDoc-report does not exist",
                PATH_DIR__JAVADOC_REPORT));
        ttCtx.getThToolHelper().setInputDir(PATH_DIR__JAVADOC_REPORT.toFile());
        ttCtx.getThToolHelper().setInputFile(PATH_DIR__JAVADOC_REPORT.resolve(
            Path.of("org/krmdemo/techlabs/core/datetime/LinkedDateTimeTripletMap.html")
        ).toFile());
        JavaDocHelper jdh = JavaDocHelper.fromCtxLazy(ttCtx);
        assertThat(jdh.getNavBarRight()).startsWith("""
            <div id="div-release-catalog-badge" class="nav-bar-right-first">
            <a href="https://krm-demo.github.io/core-utils" class="release-catalog-badge-link">
              <img alt="a badge to 'Release Catalog'" src="https://img.shields.io/badge/Release_Catalog-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97" class="release-catalog-badge"/>
            </a>
            </div>
            <div id="div-github-source-badge" class="nav-bar-right-second">
            <a href="https://github.com/krm-demo/core-utils""");
    }

    @Test
    void testNavBarRight_JavaSource_RegularClass() {
        assumeTrue(Files.exists(PATH_DIR__JAVADOC_REPORT),
            String.format("input directory '%s' to process the JavaDoc-report does not exist",
                PATH_DIR__JAVADOC_REPORT));
        ttCtx.getThToolHelper().setInputDir(PATH_DIR__JAVADOC_REPORT.toFile());
        ttCtx.getThToolHelper().setInputFile(PATH_DIR__JAVADOC_REPORT.resolve(
            Path.of("org/krmdemo/techlabs/core/datetime/LinkedDateTimeTripletMap.html")
        ).toFile());
        JavaDocHelper jdh = JavaDocHelper.fromCtxLazy(ttCtx);
        assertThat(jdh.getGitHubSourcePath()).isEqualTo(
            "src/main/java/org/krmdemo/techlabs/core/datetime/LinkedDateTimeTripletMap.java");
    }

    @Test
    void testNavBarRight_JavaSource_InnerClass() {
        assumeTrue(Files.exists(PATH_DIR__JAVADOC_REPORT),
            String.format("input directory '%s' to process the JavaDoc-report does not exist",
                PATH_DIR__JAVADOC_REPORT));
        ttCtx.getThToolHelper().setInputDir(PATH_DIR__JAVADOC_REPORT.toFile());
        ttCtx.getThToolHelper().setInputFile(PATH_DIR__JAVADOC_REPORT.resolve(
            Path.of("org/krmdemo/techlabs/core/datetime/LinkedDateTimeTripletMap.LinkedTriplet.html")
        ).toFile());
        JavaDocHelper jdh = JavaDocHelper.fromCtxLazy(ttCtx);
        assertThat(jdh.getGitHubSourcePath()).isEqualTo(
            "src/main/java/org/krmdemo/techlabs/core/datetime/LinkedDateTimeTripletMap.java");
    }

    @Test
    void testNavBarRight_ClassInUse() {
        assumeTrue(Files.exists(PATH_DIR__JAVADOC_REPORT),
            String.format("input directory '%s' to process the JavaDoc-report does not exist",
                PATH_DIR__JAVADOC_REPORT));
        ttCtx.getThToolHelper().setInputDir(PATH_DIR__JAVADOC_REPORT.toFile());
        ttCtx.getThToolHelper().setInputFile(PATH_DIR__JAVADOC_REPORT.resolve(
            Path.of("org/krmdemo/techlabs/core/datetime/class-use/CoreDateTimeUtils.html")
        ).toFile());
        JavaDocHelper jdh = JavaDocHelper.fromCtxLazy(ttCtx);
        assertThat(jdh.getGitHubSourcePath()).isEqualTo(
            "src/main/java/org/krmdemo/techlabs/core/datetime/CoreDateTimeUtils.java");
    }

    @Test
    void testNavBarRight_PackageInfo_NotExisting() {
        assumeTrue(Files.exists(PATH_DIR__JAVADOC_REPORT),
            String.format("input directory '%s' to process the JavaDoc-report does not exist",
                PATH_DIR__JAVADOC_REPORT));
        ttCtx.getThToolHelper().setInputDir(PATH_DIR__JAVADOC_REPORT.toFile());
        ttCtx.getThToolHelper().setInputFile(PATH_DIR__JAVADOC_REPORT.resolve(
            Path.of("org/krmdemo/techlabs/core/datetime/package-summary.html")
        ).toFile());
        JavaDocHelper jdh = JavaDocHelper.fromCtxLazy(ttCtx);
        assertThat(jdh.getGitHubSourcePath()).isEqualTo(
            "src/main/java/org/krmdemo/techlabs/core/datetime");
    }

    @Test
    void testNavBarRight_PackageInfo_Existing() {
        assumeTrue(Files.exists(PATH_DIR__JAVADOC_REPORT),
            String.format("input directory '%s' to process the JavaDoc-report does not exist",
                PATH_DIR__JAVADOC_REPORT));
        ttCtx.getThToolHelper().setInputDir(PATH_DIR__JAVADOC_REPORT.toFile());
        ttCtx.getThToolHelper().setInputFile(PATH_DIR__JAVADOC_REPORT.resolve(
            Path.of("org/krmdemo/techlabs/core/package-tree.html")
        ).toFile());
        JavaDocHelper jdh = JavaDocHelper.fromCtxLazy(ttCtx);
        assertThat(jdh.getGitHubSourcePath()).isEqualTo(
            "src/main/java/org/krmdemo/techlabs/core/package-info.java");
    }
}
