package org.krmdemo.techlabs.core.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    public static Map<Character, Integer> countingCharsMap(CharSequence str) {
        return countingMap(streamChars(str));
    }

    public static Stream<String> splitWords(String sentence) {
        return Arrays.stream(sentence.split("\\W"))
            .map(String::trim)
            .filter(word -> !word.isEmpty());
    }

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
}
