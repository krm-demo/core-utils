package org.krmdemo.techlabs.thtool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.StringBuilderOut;

import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

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
