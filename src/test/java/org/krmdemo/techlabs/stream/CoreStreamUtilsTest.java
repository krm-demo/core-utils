package org.krmdemo.techlabs.stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.krmdemo.techlabs.dump.DumpUtils;
import org.krmdemo.techlabs.dump.PrintUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.keyValue;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.linkedSet;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.nameValue;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.sortedMap;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.sortedSet;

/**
 * A unit-test to verify the utility-class {@link CoreStreamUtils}
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CoreStreamUtilsTest {

    @Test @Order(1)
    @DisplayName("Test of linkedSet(...) and sortedSet(...)")
    void testLinkedSortedSet() {
        assertThat(linkedSet("one", "two", "three", "four", "five"))
            .isInstanceOf(SequencedSet.class)
            .containsExactly("one", "two", "three", "four", "five");
        System.out.println("linkedSet(...) --> " + DumpUtils.dumpAsJsonTxt(
            linkedSet("one", "two", "three", "four", "five")
        ));

        assertThat(sortedSet("one", "two", "three", "four", "five"))
            .isInstanceOf(NavigableSet.class)
            .containsExactly("five", "four", "one", "three", "two");
        System.out.println("sortedSet(...) --> " + DumpUtils.dumpAsJsonTxt(
            sortedSet("one", "two", "three", "four", "five")
        ));

        int[] intsArr = IntStream.range(0, 10).map(i -> (i * 10) % 7).toArray();
        assertThat(intsArr).containsExactly(0, 3, 6, 2, 5, 1, 4, 0, 3, 6);

        assertThat(linkedSet(Arrays.stream(intsArr).boxed()))
            .hasSize(7)
            .isInstanceOf(SequencedSet.class)
            .containsExactly(0, 3, 6, 2, 5, 1, 4);
        assertThat(sortedSet(Arrays.stream(intsArr).boxed()))
            .hasSize(7)
            .isInstanceOf(NavigableSet.class)
            .containsExactly(0, 1, 2, 3, 4, 5, 6);
    }

    @Test @Order(2)
    @DisplayName("Test of linkedMap(...)")
    void testLinkedMap() {
        SequencedMap<String, Integer> numbersMap = linkedMap(
            nameValue("one", 1),
            nameValue("two", 2),
            nameValue("three", 3),
            nameValue("four", 4),
            nameValue("five", 5)
        );
        System.out.println("linkedMap(...) --> " + DumpUtils.dumpAsJsonTxt(numbersMap));
        assertThat(numbersMap.keySet()).containsExactly("one", "two", "three", "four", "five");
        assertThat(numbersMap.values()).containsExactly(1, 2, 3, 4, 5);
    }

    @Test @Order(3)
    @DisplayName("Test of sortedMap(...)")
    void testSortedMap() {
        NavigableMap<String, Integer> numbersMap = sortedMap(
            nameValue("one", 1),
            nameValue("two", 2),
            nameValue("three", 3),
            nameValue("four", 4),
            nameValue("five", 5)
        );
        System.out.println("sortedMap(...) --> " + DumpUtils.dumpAsJsonTxt(numbersMap));
        assertThat(numbersMap.keySet()).containsExactly("five", "four", "one", "three", "two");
        assertThat(numbersMap.values()).containsExactly(5, 4, 1, 3, 2);
    }

    @Test @Order(4)
    @DisplayName("Test of linkedMap(MergeFunction, ...)")
    void testMergeLinkedMap(TestInfo testInfo) {
        List<Map.Entry<Integer, String>> numberWords = List.of(
            keyValue(1, "one"),
            keyValue(2, "two"),
            keyValue(2, "couple"),
            keyValue(2, "pair"),
            keyValue(3, "three")
        );
        System.out.printf("%s: numberWords --> %s%n",
            testInfo.getDisplayName(),
            DumpUtils.dumpAsJsonTxt(numberWords));

        // the default merging function is MergeFunction.OVERWRITE, which repeats the behavior of "Map.put(...)"
        SequencedMap<Integer, String> linkedMap_OVERWRITE = linkedMap(numberWords.stream());
        System.out.printf("%s: linkedMap_OVERWRITE --> %s%n",
            testInfo.getDisplayName(),
            DumpUtils.dumpAsJsonTxt(linkedMap_OVERWRITE));
        assertThat(linkedMap_OVERWRITE)
            .hasSize(3)
            .containsValues("one", "pair", "three");  // <-- the last value of '2' in the result map is 'pair'

        SequencedMap<Integer, String> linkedMap_IGNORE = linkedMap(MergeFunction.IGNORE, numberWords.stream());
        System.out.printf("%s: linkedMap_IGNORE --> %s%n",
            testInfo.getDisplayName(),
            DumpUtils.dumpAsJsonTxt(linkedMap_IGNORE));
        assertThat(linkedMap_IGNORE)
            .hasSize(3)
            .containsValues("one", "two", "three");  // <-- the first value of '2' in the result map is 'two'

        assertThatIllegalStateException().isThrownBy(() ->
            sortedMap(MergeFunction.THROW, numberWords.stream())
        ).withMessage("attempt to overwrite the value 'two' with the value 'couple', which is NOT allowed");
    }

    @Test @Order(5)
    @DisplayName("Test of sortedMap(MergeFunction, ...)")
    void testMergeSortedMap(TestInfo testInfo) {
        List<Map.Entry<Integer, String>> numberWords = List.of(
            keyValue(1, "one"),
            keyValue(2, "two"),
            keyValue(2, "couple"),
            keyValue(2, "pair"),
            keyValue(3, "three")
        );
        System.out.printf("%s: numberWords --> %s%n",
            testInfo.getDisplayName(),
            DumpUtils.dumpAsJsonTxt(numberWords));

        // the default merging function is MergeFunction.OVERWRITE, which repeats the behavior of "Map.put(...)"
        NavigableMap<Integer, String> sortedMap_OVERWRITE = sortedMap(numberWords.stream());
        System.out.printf("%s: sortedMap_OVERWRITE --> %s%n",
            testInfo.getDisplayName(),
            DumpUtils.dumpAsJsonTxt(sortedMap_OVERWRITE));
        assertThat(sortedMap_OVERWRITE)
            .hasSize(3)
            .containsValues("one", "pair", "three");  // <-- the last value of '2' in the source stream is 'pair'
        assertThat(sortedMap(numberWords.stream()))
            .isEqualTo(sortedMap(MergeFunction.OVERWRITE, numberWords.stream()));

        NavigableMap<Integer, String> sortedMap_IGNORE = sortedMap(MergeFunction.IGNORE, numberWords.stream());
        System.out.printf("%s: sortedMap_IGNORE --> %s",
            testInfo.getDisplayName(), DumpUtils.dumpAsJsonTxt(sortedMap_IGNORE));
        assertThat(sortedMap_IGNORE)
            .hasSize(3)
            .containsValues("one", "two", "three");  // <-- the first value of '2' in the source stream is 'two'

        assertThatIllegalStateException().isThrownBy(() ->
            sortedMap(MergeFunction.THROW, numberWords.stream())
        ).withMessage("attempt to overwrite the value 'two' with the value 'couple', which is NOT allowed");
    }
}
