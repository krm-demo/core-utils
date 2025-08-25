package org.krmdemo.techlabs.stream;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.keyValue;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.linkedMap;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.linkedSet;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.nameValue;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedMap;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedSet;

public class TechlabsStreamUtilsTest {

    @Test
    void testLinkedSortedSet() {
        assertThat(sortedSet("one", "two", "three", "four", "five"))
            .isInstanceOf(NavigableSet.class)
            .containsExactly("five", "four", "one", "three", "two");
        assertThat(linkedSet("one", "two", "three", "four", "five"))
            .isInstanceOf(SequencedSet.class)
            .containsExactly("one", "two", "three", "four", "five");

        int[] intsArr = IntStream.range(0, 10).map(i -> (i * 10) % 7).toArray();
        assertThat(intsArr).containsExactly(0, 3, 6, 2, 5, 1, 4, 0, 3, 6);
        assertThat(sortedSet(Arrays.stream(intsArr).boxed()))
            .hasSize(7)
            .isInstanceOf(NavigableSet.class)
            .containsExactly(0, 1, 2, 3, 4, 5, 6);
        assertThat(linkedSet(Arrays.stream(intsArr).boxed()))
            .hasSize(7)
            .isInstanceOf(SequencedSet.class)
            .containsExactly(0, 3, 6, 2, 5, 1, 4);
    }

    @Test
    void testSortedMap() {
        NavigableMap<String, Integer> numbersMap = sortedMap(
            nameValue("one", 1),
            nameValue("two", 2),
            nameValue("three", 3),
            nameValue("four", 4),
            nameValue("five", 5)
        );
        assertThat(numbersMap.keySet()).containsExactly("five", "four", "one", "three", "two");
        assertThat(numbersMap.values()).containsExactly(5, 4, 1, 3, 2);

        List<Map.Entry<Integer, String>> numberWords = List.of(
            keyValue(1, "one"),
            keyValue(2, "two"),
            keyValue(2, "couple"),
            keyValue(2, "pair"),
            keyValue(3, "three")
        );
        // the default merging function is MergeFunction.OVERWRITE, which repeats the behavior of "Map.put(...)"
        NavigableMap<Integer, String> numberWords_OVERWRITE = sortedMap(numberWords.stream());
        assertThat(numberWords_OVERWRITE)
            .hasSize(3)
            .containsValues("one", "pair", "three");  // <-- the last value of '2' is in result map - 'pair'

        NavigableMap<Integer, String> numberWords_IGNORE = sortedMap(MergeFunction.IGNORE, numberWords.stream());
        assertThat(numberWords_IGNORE)
            .hasSize(3)
            .containsValues("one", "two", "three");  // <-- the first value of '2' is in result map - 'two'

        assertThatIllegalStateException().isThrownBy(() ->
            sortedMap(MergeFunction.THROW, numberWords.stream())
        ).withMessage("attempt to merge values 'two' and 'couple', which is NOT allowed");
    }

    @Test
    void testLinkedMap() {
        SequencedMap<String, Integer> numbersMap = linkedMap(
            nameValue("one", 1),
            nameValue("two", 2),
            nameValue("three", 3),
            nameValue("four", 4),
            nameValue("five", 5)
        );
        assertThat(numbersMap.keySet()).containsExactly("one", "two", "three", "four", "five");
        assertThat(numbersMap.values()).containsExactly(1, 2, 3, 4, 5);

        List<Map.Entry<Integer, String>> numberWords = List.of(
            keyValue(1, "one"),
            keyValue(2, "two"),
            keyValue(2, "couple"),
            keyValue(2, "pair"),
            keyValue(3, "three")
        );
        // the default merging function is MergeFunction.OVERWRITE, which repeats the behavior of "Map.put(...)"
        SequencedMap<Integer, String> numberWords_OVERWRITE = linkedMap(numberWords.stream());
        assertThat(numberWords_OVERWRITE)
            .hasSize(3)
            .containsValues("one", "pair", "three");  // <-- the last value of '2' is in result map - 'pair'

        SequencedMap<Integer, String> numberWords_IGNORE = linkedMap(MergeFunction.IGNORE, numberWords.stream());
        assertThat(numberWords_IGNORE)
            .hasSize(3)
            .containsValues("one", "two", "three");  // <-- the first value of '2' is in result map - 'two'

        assertThatIllegalStateException().isThrownBy(() ->
            sortedMap(MergeFunction.THROW, numberWords.stream())
        ).withMessage("attempt to merge values 'two' and 'couple', which is NOT allowed");
    }
}
