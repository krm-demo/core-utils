package org.krmdemo.techlabs.thtool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.core.dump.StringBuilderOut;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.loadFileContent;

/**
 * A functional-test for {@link ThymeleafTool} that executes it as the command-line application from the same JVM
 */
public class ThymeleafToolTest {

    @Test
    void testEval_SimpleExpression() {
        int exitCode = ThymeleafTool.executeMain("eval", "2 + 2");
        assertThat(exitCode).isZero();
        assertThat(sbErr).isEmpty();
        assertThat(sbOut.toString()).isEqualTo("4");
    }

    @Test
    void testProcess_Readme() {
        int exitCode = ThymeleafTool.executeMain(
            "--var-file",
            "mavenProps=./target/classes/META-INF/maven/maven-project.properties",
            "process",
            ".github/th-templates/ROOT-Readme.md.th"
        );
        assertThat(exitCode).isZero();
        assertThat(sbErr).isEmpty();
        assertThat(sbOut).isNotBlank();

        assertThat(sbOut).contains("core-utils");
        assertThat(sbOut).contains("utility-classes to simplify working with core-java API");
        assertThat(sbOut).containsAnyOf("on-main-push.yml/badge.svg?event=push");
    }

    @Test
    void testProcessDir_DryRun() {
        int exitCode = ThymeleafTool.executeMain(
            "--var-file",
            "mavenProps=./target/classes/META-INF/maven/maven-project.properties",
            "process-dir",
            "--input-dir",
            ".github/th-test-process-dir/input-dir"
        );
        assertThat(exitCode).isZero();
        assertThat(sbErr).isEmpty();
        assertThat(sbOut).isNotBlank();
        //stdOut.println(sbOut);
    }

    @Test
    void testProcessDir_ProcessAll() {
        int exitCode = ThymeleafTool.executeMain(
            "--var-file",
            "mavenProps=./target/classes/META-INF/maven/maven-project.properties",
            "process-dir",
            "--input-dir",
            ".github/th-test-process-dir/input-dir",
            "--output-dir",
            "./target/th-test-process-dir/process-all"
        );
        assertThat(exitCode).isZero();
        assertThat(sbErr).isEmpty();
        assertThat(sbOut).isNotBlank();

        //stdOut.println(sbOut);
        assertThat(loadFileContent("./target/th-test-process-dir/process-all/root-one.html"))
            .contains("mh.projectCatalogName = core-utils-21");
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
