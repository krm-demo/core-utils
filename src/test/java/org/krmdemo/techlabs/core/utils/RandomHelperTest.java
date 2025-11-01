package org.krmdemo.techlabs.core.utils;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.intsList;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedSet;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.reversed;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.reversedArr;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedSet;

/**
 * A unit-test to verify the utility-class {@link RandomHelper}
 */
public class RandomHelperTest {

    final RandomHelper rnd = new RandomHelper(12335);

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
}
