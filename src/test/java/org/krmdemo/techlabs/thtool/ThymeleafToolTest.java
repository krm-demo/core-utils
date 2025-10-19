package org.krmdemo.techlabs.thtool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.techlabs.core.dump.StringBuilderOut;

import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.loadFileContent;

/**
 * A functional-test for {@link ThymeleafTool} that executes it as the command-line application from the same JVM
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ThymeleafToolTest {

    @Test
    void testEval_SimpleExpression() {
        int exitCode = ThymeleafTool.executeMain("eval", "2 + 2");
        assertThat(exitCode).isZero();
        assertThat(sbErr).isEmpty();
        assertThat(sbOut.toString()).isEqualTo("4");
    }

    @Test
    void testProcessDir_TestSite_DryRun() {
        int exitCode = ThymeleafTool.executeMain(
            "--var-file",
            "mavenProps=./target/classes/META-INF/maven/maven-project.properties",
            "process-dir",
            "--input-dir",
            ".github/th-test-site/original"
        );
//        stdOut.println(sbOut);
//        stdErr.println(sbErr);
        assertThat(exitCode).isZero();
        assertThat(sbErr).isEmpty();
        assertThat(sbOut).isNotBlank();
    }

    @Test
    void testProcessDir_TestSite() {
        int exitCode = ThymeleafTool.executeMain(
            "--var-file",
            "mavenProps=./target/classes/META-INF/maven/maven-project.properties",
            "process-dir",
            "--input-dir",
            ".github/th-test-site/original",
            "--output-dir",
            ".github/th-test-site/processed",
            "--clean-output"
        );
//        stdOut.println(sbOut);
//        stdErr.println(sbErr);
        assertThat(exitCode).isZero();
        assertThat(sbErr).isEmpty();
        assertThat(sbOut).isNotBlank();

        assertThat(loadFileContent(".github/th-test-site/processed/root-one.html"))
            .contains("[&#8203;[${ mh.projectCatalogName }]&#8203;] = core-utils-21");
    }

    @Test
    void testProcess_Readme() {
        int exitCode = ThymeleafTool.executeMain(
            "--var-file",
            "mavenProps=./target/classes/META-INF/maven/maven-project.properties",
            "process",
            ".github/th-templates/ROOT-Readme.md.th"
        );
//        stdErr.println(sbErr);
//        stdErr.println(sbOut);
        assertThat(exitCode).isZero();
        assertThat(sbErr).isEmpty();
        assertThat(sbOut).isNotBlank();

        assertThat(sbOut).contains("core-utils");
        assertThat(sbOut).contains("utility-classes to simplify working with core-java API");
        assertThat(sbOut).containsAnyOf("on-main-push.yml/badge.svg?event=push");

        assertThat(sbOut).contains("[![Latest-Public]");
        assertThat(sbOut).contains("[![Release-Catalog]" +
            "(https://img.shields.io/badge/Release_Catalog-4D7A97?logo=github&logoColor=f8981d&labelColor=4D7A97)]" +
            "(https://krm-demo.github.io/core-utils/)");
    }

    @Test
    void testProcess_ReleaseCatalog() {
        int exitCode = ThymeleafTool.executeMain(
            "--var-file",
            "mavenProps=./target/classes/META-INF/maven/maven-project.properties",
            "process",
            "--output",
            ".github/th-test-release-catalog/index.html",
            ".github/th-templates/GH-PAGES--Release-Catalog.html.th"
        );
//        stdErr.println(sbErr);
//        stdErr.println(sbOut);
        assertThat(exitCode).isZero();
        assertThat(sbErr).isEmpty();
        assertThat(sbOut).isEmpty();

        assertThat(loadFileContent(".github/th-test-release-catalog/index.html"))
            .contains("core-utils (Release Catalog)")
            .contains("https://github.com/krm-demo/core-utils/commit/");
    }

    // --------------------------------------------------------------------------------------------

    final static StringBuilderOut sbOut = StringBuilderOut.create();
    final static StringBuilderOut sbErr = StringBuilderOut.create();

    final static PrintStream stdOut = System.out;
    final static PrintStream stdErr = System.err;

    @BeforeEach
    void beforeEach() {
        sbOut.clear();
        sbErr.clear();
        System.setOut(sbOut);
        System.setErr(sbErr);
    }

    @AfterEach
    void afterEach() {
        System.setOut(stdOut);
        System.setErr(stdErr);
    }
}
