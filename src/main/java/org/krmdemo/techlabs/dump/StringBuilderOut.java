package org.krmdemo.techlabs.dump;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.io.output.WriterOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.stream.Stream;

/**
 * A {@link PrintWriter} over {@link StringBuilderWriter} that could simplify
 * the substitution of {@link System#out} and {@link System#err} in unit-tests,
 * and it's also used by other classes of the current package.
 */
public class StringBuilderOut extends PrintStream implements CharSequence {
    private final StringBuilderWriter sbw;

    StringBuilderOut(StringBuilderWriter sbw) {
        super(outputStream(sbw));
        this.sbw = sbw;
    }

    static OutputStream outputStream(Writer originWriter) {
        try {
            return WriterOutputStream.builder()
                .setWriter(originWriter)
                .setWriteImmediately(true)
                .get();
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(
                "could not create a wrapper over originWriter - " + originWriter,
                ioEx);
        }
    }

    public static StringBuilderOut create() {
        return new StringBuilderOut(new StringBuilderWriter());
    }

    public StringBuilder stringBuilder() {
        return sbw.getBuilder();
    }

    public void clear() {
        stringBuilder().setLength(0);
    }

    public int length() {
        return stringBuilder().length();
    }

    @Override
    public String toString() {
        return stringBuilder().toString();
    }

    public Stream<String> lines() {
        return toString().lines();
    }

    public int linesCount() {
        return Math.toIntExact(lines().count());
    }

    @Override
    public char charAt(int index) {
        return stringBuilder().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return stringBuilder().subSequence(start, end);
    }
}
