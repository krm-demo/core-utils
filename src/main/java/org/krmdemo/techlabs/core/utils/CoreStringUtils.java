package org.krmdemo.techlabs.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.core.utils.CountingUtils.countingMap;

/**
 * Utility-class to work with strings, words and characters
 * (some extension to JDK and {@link org.apache.commons.lang3.StringUtils})
 */
public class CoreStringUtils {

    /**
     * Quite the same as JDK's utility-method {@link Objects#requireNonNull(Object, String)},
     * but in addition to valid non-{@code null} reference the passed string requires to be non-blank
     * (contains at least one non-whitespace character)
     *
     * @param str a string (or any type that extends {@link CharSequence}) that requires to be non-blank
     * @return the input string {@code str} if it's non-blank
     * @throws IllegalArgumentException if the input string {@code str} is blank
     * @see StringUtils#isBlank(CharSequence)
     */
    public static <S extends CharSequence> S requiredNonBlank(S str) {
        return requiredNonBlank(str, "input string must NOT be blank");
    }

    /**
     * Quite the same as JDK's utility-method {@link Objects#requireNonNull(Object)},
     * but in addition to valid non-{@code null} reference the passed string requires to be non-blank
     * (contains at least one non-whitespace character)
     *
     * @param str a string (or any type that extends {@link CharSequence}) that requires to be non-blank
     * @return the input string {@code str} if it's non-blank
     * @throws IllegalArgumentException if the input string {@code str} is blank
     * @see StringUtils#isBlank(CharSequence)
     */
    public static <S extends CharSequence> S requiredNonBlank(S str, String errorMessage) {
        return requiredNonBlank(str, () -> errorMessage);
    }

    /**
     * Quite the same as JDK's utility-method {@link Objects#requireNonNull(Object, Supplier)},
     * but in addition to valid non-{@code null} reference the passed string requires to be non-blank
     * (contains at least one non-whitespace character)
     *
     * @param str a string (or any type that extends {@link CharSequence}) that requires to be non-blank
     * @param errMsgSupplier a supplier for error message (to postpone the calculation if it's non-trivial)
     * @return the input string {@code str} if it's non-blank
     * @throws IllegalArgumentException if the input string {@code str} is blank
     * @param <S> any type that extends {@link CharSequence}
     * @see StringUtils#isBlank(CharSequence)
     */
    public static <S extends CharSequence> S requiredNonBlank(S str, Supplier<String> errMsgSupplier) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException(errMsgSupplier.get());
        }
        return str;
    }

    /**
     * Just a short-cut of JDK-invocation: <pre>{@code
     *     Collectors.joining(System.lineSeparator())
     * }</pre>
     * @return the {@link Collector} that join the stream of strings with {@link System#lineSeparator()}
     */
    public static Collector<CharSequence, ?, String> multiLine() {
        return Collectors.joining(System.lineSeparator());
    }

    /**
     * @param str a string (eventually {@link CharSequence} to get the stream of chars
     * @return the stream of chars as {@link Stream Stream&lt;Character&gt;}
     */
    public static Stream<Character> streamChars(CharSequence str) {
        return str.chars().mapToObj(ch -> (char)ch);
    }

    /**
     * Count the characters in passed string
     *
     * @param str a string to count the characters (as {@link CharSequence}
     * @return a counting-map of characters in passed {@code str}
     */
    public static Map<Character, Integer> countingCharsMap(CharSequence str) {
        return countingMap(streamChars(str));
    }

    /**
     * Split the passed sentence on alphanumeric words
     *
     * @param sentence a string to sp[lit
     * @return a {@link Stream Stream&lt;String&gt;} of alphanumeric words
     */
    public static Stream<String> splitWords(String sentence) {
        return Arrays.stream(sentence.split("\\W"))
            .map(String::trim)
            .filter(word -> !word.isEmpty());
    }

    /**
     * Split the passed sentence on alphanumeric words
     *
     * @param sentence a string to sp[lit
     * @return a {@link List List&lt;String&gt;} of alphanumeric words
     */
    public static List<String> splitWordsList(String sentence) {
        return splitWords(sentence).collect(Collectors.toList());
    }

    /**
     * The same as {@link Collectors#joining(CharSequence)} with empty delimiter,
     * but the elements of string are concatenated in <b>reversed order</b>.
     * <hr/>
     * <u><i>Note:</i></u> Not use for large streams, because  {@link StringBuilder#insert(int, Object)}
     * is much slower than {@link StringBuilder#append(Object)} when the length of buffer is very big.
     *
     * @return the result as {@link CharSequence} (no extra copy of the result!)
     * @param <T> the type of input elements to the reduction operation
     */
    public static <T> Collector<T, ?, CharSequence> joiningReversed() {
        return joiningReversed("");
    }

    /**
     * The same as {@link Collectors#joining(CharSequence)},
     * but the elements of string are concatenated in <b>reversed order</b>.
     * <hr/>
     * <u><i>Note:</i></u> Not use for large streams, because  {@link StringBuilder#insert(int, Object)}
     * is much slower than {@link StringBuilder#append(Object)} when the length of buffer is very big.
     *
     * @return the result as {@link CharSequence} (no extra copy of the result!)
     * @param <T> the type of input elements to the reduction operation
     */
    public static <T> Collector<T, ?, CharSequence> joiningReversed(String delimiter) {
        return joiningReversed(delimiter, "", "");
    }

    /**
     * The same as {@link Collectors#joining(CharSequence, CharSequence, CharSequence)},
     * but the elements of string are concatenated in <b>reversed order</b>.
     * <hr/>
     * <u><i>Note:</i></u> Not use for large streams, because  {@link StringBuilder#insert(int, Object)}
     * is much slower than {@link StringBuilder#append(Object)} when the length of buffer is very big.
     *
     * @return the result as {@link CharSequence} (no extra copy of the result!)
     * @param <T> the type of input elements to the reduction operation
     */
    public static <T> Collector<T, ?, CharSequence> joiningReversed(String delimiter, String prefix, String suffix) {
        return Collector.of(
            StringBuilder::new,
            (sb, value) -> {
                if (!sb.isEmpty() && !delimiter.isEmpty()) {
                    sb.insert(0, delimiter);
                }
                sb.insert(0, value);
            },
            (left, right) -> { // the combiner is required for parallel streams
                right.append(delimiter).append(left); return right;
            },
            sb -> sb.insert(0, prefix).append(suffix)
        );
    }

    /**
     * This method could be used to check whether the multi-line output ends with {@link System#lineSeparator() new-line}-symbol(s),
     * because in most cases it's quite impossible to realize that fact visually (unless using different background).
     * <hr/>
     * TODO: introduce a method to detect extra white-spaces before new-line like {@code git-diff} is doing
     *
     * @param str the string to check
     * @return {@code true} if the passed line is not empty, and it does not end with {@link System#lineSeparator() new-line}-symbol(s)
     */
    public static boolean hasNoNewLineAtTheEnd(String str) {
        return StringUtils.isNotEmpty(str) && !str.endsWith(System.lineSeparator());
    }

    // --------------------------------------------------------------------------------------------

    private CoreStringUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
