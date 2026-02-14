package org.krmdemo.techlabs.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.keyValue;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedMap;
import static org.krmdemo.techlabs.core.utils.CoreStringUtils.countingCharsMap;
import static org.krmdemo.techlabs.core.utils.CoreStringUtils.hasNoNewLineAtTheEnd;
import static org.krmdemo.techlabs.core.utils.CoreStringUtils.joiningReversed;
import static org.krmdemo.techlabs.core.utils.CoreStringUtils.requiredNonBlank;
import static org.krmdemo.techlabs.core.utils.CoreStringUtils.splitWordsList;

/**
 * A unit-test to verify the utility-class {@link CoreStringUtils}
 */
public class CoreStringUtilsTest {

    @Test
    void testRequiredNonBlank() {
        assertThat(requiredNonBlank("abc")).isEqualTo("abc");
        assertThat(requiredNonBlank(".   ")).isEqualTo(".   ");
        assertThat(requiredNonBlank("\n\t...")).isEqualTo("\n\t...");
        assertThatIllegalArgumentException().isThrownBy(() -> requiredNonBlank(null));
        assertThatIllegalArgumentException().isThrownBy(() -> requiredNonBlank(""));
        assertThatIllegalArgumentException().isThrownBy(() -> requiredNonBlank("\n  \r\t"));

        assertThatIllegalArgumentException().isThrownBy(
            () -> requiredNonBlank(null, "some direct error message")
        ).withMessage("some direct error message");

        assertThatIllegalArgumentException().isThrownBy(
            () -> requiredNonBlank("", () -> "some error message, that is supplied only if blank")
        ).withMessage("some error message, that is supplied only if blank");

        assertThat(requiredNonBlank("xyz", // if no error - the supplier will not be invoked
            () -> { throw new IllegalStateException("never thrown"); })
        ).isEqualTo("xyz");
    }

    @Test
    void testCountingCharsMap() {
        assertThat(countingCharsMap("")).isEmpty();
        assertThat(sortedMap(countingCharsMap("la-la-la")))
            .isEqualTo(sortedMap(
                keyValue('l', 3),
                keyValue('a', 3),
                keyValue('-', 2)
            ));
        assertThat(sortedMap(countingCharsMap("Hello World")))
            .isEqualTo(sortedMap(
                keyValue(' ', 1),
                keyValue('H', 1),
                keyValue('W', 1),
                keyValue('d', 1),
                keyValue('e', 1),
                keyValue('l', 3),
                keyValue('o', 2),
                keyValue('r', 1)
            ));
    }

    @Test
    void testSplitWords() {
        assertThat(splitWordsList("")).isEmpty();
        assertThat(splitWordsList("A unit-test to verify the utility-class {@link CoreStringUtils}"))
            .isEqualTo(List.of("A", "unit", "test", "to", "verify", "the",  "utility", "class", "link", "CoreStringUtils"));
    }

    @Test
    void testJoinReversed() {
        assertThat("" + Stream.empty().collect(joiningReversed())).isEmpty();
        assertThat("" + Stream.of("single-element").collect(joiningReversed()))
            .isEqualTo("single-element");
        assertThat("" + Stream.of("1", "2", "3", "4").collect(joiningReversed()))
            .isEqualTo("4321");
        assertThat("" + Stream.of("1", "2", "3", "4").collect(joiningReversed(".")))
            .isEqualTo("4.3.2.1");
        assertThat("" + Stream.of("1", "2", "3", "4").collect(joiningReversed(", ", "( ", " )")))
            .isEqualTo("( 4, 3, 2, 1 )");
    }

    @Test
    void testJoinReversed_Parallel() {
        String forwardJoined = IntStream.range('a', 'z')
            .mapToObj(ch -> "" + (char)ch)
            .parallel()
            .collect(joining());
        String reverseJoined = "" + IntStream.range('a', 'z')
            .mapToObj(ch -> "" + (char)ch)
            .parallel()
            .collect(joiningReversed());
        assertThat(reverseJoined).isEqualTo(StringUtils.reverse(forwardJoined));
    }

    @Test
    void testHasNoNewLine() {
        assertThat(hasNoNewLineAtTheEnd(null)).isFalse();
        assertThat(hasNoNewLineAtTheEnd("")).isFalse();
        assertThat(hasNoNewLineAtTheEnd(" ")).isTrue();
        assertThat(hasNoNewLineAtTheEnd(" \t  \t")).isTrue();
        assertThat(hasNoNewLineAtTheEnd("la-la-la")).isTrue();
        assertThat(hasNoNewLineAtTheEnd("la-la-la    ")).isTrue();
        assertThat(hasNoNewLineAtTheEnd("la-la-la   \n")).isFalse();
        assertThat(hasNoNewLineAtTheEnd("la-la-la   \r\n")).isFalse();
        assertThat(hasNoNewLineAtTheEnd("la-la-la   \r")).isTrue();
        assertThat(hasNoNewLineAtTheEnd(String.format("la-la-la   %n"))).isFalse();
    }

    @Test
    void testCreationIsProhibited() {
        UnsupportedOperationException uoEx = assertThrows(UnsupportedOperationException.class,
            () -> CorePropsUtils.newInstance(CoreStringUtils.class)
        );
        assertThat(uoEx.getMessage()).isEqualTo(
            "Cannot instantiate utility-class org.krmdemo.techlabs.core.utils.CoreStringUtils");
    }
}
