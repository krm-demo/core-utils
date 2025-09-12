package org.krmdemo.techlabs.dump;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;

/**
 *
 */
public class StdOutCaptureTest {

    final static StringBuilderOut sbOut = StringBuilderOut.create();
    final static StringBuilderOut sbErr = StringBuilderOut.create();

    final static PrintStream stdOut = System.out;
    final static PrintStream stdErr = System.err;

    @BeforeAll
    static void beforeAll() {
        sbOut.clear();
        sbErr.clear();
//        System.setOut(sbOut);
//        System.setErr(sbErr);
    }

    @AfterAll
    static void afterAll() {
        System.setOut(stdOut);
        System.setErr(stdErr);
        if (!sbErr.isEmpty()) {
            System.err.println("---------------");
            System.err.println(sbErr);
            System.err.println("---------------");
        }
        if (!sbOut.isEmpty()) {
            System.out.println(" - - - - - - - ");
            System.out.println(sbOut);
            System.out.println(" - - - - - - - ");
        }
    }

    @Test
    void testStdOut() {
        System.out.println("Hello !!!");
        System.err.println("Good Bye !!!");
    }
}
