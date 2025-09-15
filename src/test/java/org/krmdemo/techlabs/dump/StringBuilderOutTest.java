package org.krmdemo.techlabs.dump;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test to verify the abilities of {@link StringBuilderOut}
 * and components of <a href="https://commons.apache.org/proper/commons-io/">Apache Commons IO</a>,
 * which are used in its implementation.
 */
public class StringBuilderOutTest {

    @Test
    void testStringWriter() throws IOException {
        StringWriter sw = new StringWriter();
        sw.append("1. the first line.").append(System.lineSeparator());
        sw.append("2. the second line.").append(System.lineSeparator());
        assertThat(sw.toString()).isEqualTo("""
            1. the first line.
            2. the second line.
            """);
        sw.flush();
        sw.close();
        sw.append("la-la-la");
        assertThat(sw.toString()).isEqualTo("""
            1. the first line.
            2. the second line.
            la-la-la""");
    }

    @Test
    void testStringBuilderWriter() throws IOException {
        @SuppressWarnings("resource") StringBuilderWriter sbw = new StringBuilderWriter();
        sbw.append("1. the first line.").append(System.lineSeparator());
        sbw.append("2. the second line.").append(System.lineSeparator());
        assertThat(sbw.toString()).isEqualTo("""
            1. the first line.
            2. the second line.
            """);
        sbw.getBuilder().setLength(0);
        sbw.append("3. the third line.").append(System.lineSeparator());
        sbw.append("4. the fourth line.").append(System.lineSeparator());
        assertThat(sbw.toString()).isEqualTo("""
            3. the third line.
            4. the fourth line.
            """);
    }

    @Test
    void testPrintWriter() {
        StringBuilderWriter sbw = new StringBuilderWriter();
        PrintWriter pw = new PrintWriter(sbw);
        int count = 0;
        pw.printf("%d. the first line.%n", ++count);
        pw.printf("%d. the second line.%n", ++count);
        assertThat(sbw.toString()).isEqualTo("""
            1. the first line.
            2. the second line.
            """);

        sbw.getBuilder().setLength(0);
        pw.println("la-la-la");
        assertThat(sbw.toString()).isEqualTo("""
            la-la-la
            """);

        sbw.getBuilder().setLength(0);
        pw.printf("%d. the third line.%n", ++count);
        pw.printf("%d. the fourth line.%n", ++count);
        assertThat(sbw.toString())
            .startsWith("3.")
            .contains("the third");

        System.out.println();
    }

    @Test
    void testStringBuilderOut() {
        @SuppressWarnings("resource") StringBuilderOut sbOut = StringBuilderOut.create();
        assertThat(sbOut.linesCount()).isEqualTo(0);
        assertThat(sbOut.isEmpty()).isTrue();

        int count = 0;
        sbOut.printf("%d. the first line.%n", ++count);
        sbOut.printf("%d. the second line.%n", ++count);
        assertThat(sbOut.toString()).isEqualTo("""
            1. the first line.
            2. the second line.
            """);
        assertThat(sbOut.linesCount()).isEqualTo(2);
        sbOut.println();
        sbOut.println();
        assertThat(sbOut.linesCount()).isEqualTo(4);
        assertThat(sbOut.isEmpty()).isFalse();

        sbOut.clear();
        assertThat(sbOut.linesCount()).isEqualTo(0);
        assertThat(sbOut.isEmpty()).isTrue();
        sbOut.println("la-la-la");
        sbOut.printf("%d. the first line.%n", ++count);
        sbOut.printf("%d. the second line.%n", ++count);
        assertThat(sbOut.linesCount()).isEqualTo(3);
        assertThat(sbOut.isEmpty()).isFalse();
        assertThat(sbOut.toString()).isEqualTo("""
            la-la-la
            3. the first line.
            4. the second line.
            """);
    }
}
