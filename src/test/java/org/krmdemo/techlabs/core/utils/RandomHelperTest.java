package org.krmdemo.techlabs.core.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.intsList;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedSet;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.reversed;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.reversedArr;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedSet;
import static org.krmdemo.techlabs.core.utils.RandomHelper.constantIntArr;
import static org.krmdemo.techlabs.core.utils.RandomHelper.zeroIntArr;

/**
 * A unit-test to verify the utility-class {@link RandomHelper}
 */
public class RandomHelperTest {

    final RandomHelper rnd = new RandomHelper();
    final RandomHelper rnd123 = new RandomHelper(123);

    @Test
    void testReverseInts() {
        int[] intsArr = rnd.randomRangeArr(5, 35);
        int[] reversedArr = reversedArr(intsArr);
        assertThat(intsList(intsArr)).isEqualTo(intsList(reversed(reversedArr)));
    }

    @Test
    void testShuffleInts() {
        final int LOW = 15;
        final int HIGH = 117;
        List<Integer> intsList = intsList(rnd.randomRangeClosedArr(LOW, HIGH));
        assertThat(Collections.min(intsList)).isEqualTo(LOW);
        assertThat(Collections.max(intsList)).isEqualTo(HIGH);
        assertThat(sortedSet(intsList.stream()).size()).isEqualTo(HIGH - LOW + 1);
        assertThat(linkedSet(intsList.stream()).size()).isEqualTo(HIGH - LOW + 1);
    }

    @Test
    void testRandomStrings() {
        assertThat(rnd123.randomUpperCase(10)).isEqualTo("EOMTTHYHVN");
        assertThat(rnd123.randomLowerCase(10)).isEqualTo("lwuznrcbaq");
        assertThat(rnd.randomUpperCase(1234))
            .doesNotContainAnyWhitespaces()
            .isUpperCase();
        assertThat(rnd.randomLowerCase(567))
            .doesNotContainAnyWhitespaces()
            .isLowerCase();
        assertThat(rnd.randomLowerCase(0)).isEmpty();
        assertThat(rnd.randomUpperCase(0)).isEmpty();
        assertThatIllegalArgumentException().isThrownBy(() -> rnd.randomLowerCase(-10));
        assertThatIllegalArgumentException().isThrownBy(() -> rnd.randomUpperCase(-125));
    }

    @Test
    void testRandomArrays() {
        assertThat(rnd123.randomIncreasingIntArr(15, 1, 100))
            .containsExactly(9, 12, 21, 32, 33, 41, 44, 54, 64, 67, 76, 78, 82, 87, 99);
        assertThat(rnd123.randomDecreasingIntArr(15, 1, 100))
            .containsExactly(86, 72, 70, 64, 60, 55, 40, 39, 38, 26, 23, 18, 15, 9, 2);
        assertThat(rnd123.randomSortedIntArr(15, 1, 30))
            .containsExactly(2, 6, 8, 11, 13, 14, 18, 20, 22, 23, 23, 24, 24, 28, 29);
        assertThat(rnd123.randomSortedReversedIntArr(15, 1, 30))
            .containsExactly(27, 24, 21, 21, 20, 20, 18, 18, 16, 14, 13, 12, 9, 2, 2);
    }

    @Test
    void testConstantArrays() {
        assertThat(constantIntArr(10, 20))
            .containsExactly(20, 20, 20, 20, 20, 20, 20, 20, 20, 20);
        assertThat(Arrays.stream(zeroIntArr(153)).sum()).isZero();
        assertThat(Arrays.stream(zeroIntArr(432)).sum()).isZero();
    }
}
