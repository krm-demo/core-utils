package org.krmdemo.techlabs.thtool.helpers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.thtool.ThymeleafToolCtx;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR__AS_FILE;

/**
 * A unit-test for <b>{@code th-tool}</b>-helper {@link JavaDocHelper}.
 */
public class JavaDocHelperTest {

    private static final ThymeleafToolCtx ttCtx = new ThymeleafToolCtx();

    private static final Path PATH_DIR__JAVADOC_REPORT = Path.of(
        "target", "reports", "apidocs");

    @BeforeAll
    static void beforeAll() {
        ttCtx.processDirectory(DEFAULT_VARS_DIR__AS_FILE);
    }

    @Test
    void testNavBarRight_JavaSource() {
        assumeTrue(Files.exists(PATH_DIR__JAVADOC_REPORT),
            String.format("input directory '%s' to process the JavaDoc-report does not exist",
                PATH_DIR__JAVADOC_REPORT));
        ttCtx.getThToolHelper().setInputDir(PATH_DIR__JAVADOC_REPORT.toFile());
        ttCtx.getThToolHelper().setInputFile(PATH_DIR__JAVADOC_REPORT.resolve(
            Path.of("org", "krmdemo", "techlabs", "core", "datetime", "LinkedDateTimeTripletMap.html")
        ).toFile());
        JavaDocHelper jdh = JavaDocHelper.fromCtxLazy(ttCtx);
        assertThat(jdh.getNavBarRight()).isEqualTo("""
            <a href="https://krm-demo.github.io/core-utils/" class="release-catalog-badge-link">
              <img alt="a badge to 'Release Catalog'" src="https://img.shields.io/badge/Release_Catalog-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97" class="release-catalog-badge"/>
            </a>
            <a href="https://github.com/krm-demo/core-utils/tree/main/src/main/java/org/krmdemo/techlabs/core/datetime"
               class="github-project-source-badge-link">
              <img alt="a badge to GitHub-project source 'src/main/java/org/krmdemo/techlabs/core/datetime' for version '21.18.001-SNAPSHOT'"
                   title="go to GitHub-project source 'src/main/java/org/krmdemo/techlabs/core/datetime' for version '21.18.001-SNAPSHOT'"
                   class="github-project-source-badge"
                   src="https://img.shields.io/badge/core--utils-21.18.001--SNAPSHOT-4D7A97?logo=github&logoColor=white&labelColor=black" />
            </a>""");
    }
}
