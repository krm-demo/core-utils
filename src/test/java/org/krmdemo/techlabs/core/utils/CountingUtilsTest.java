package org.krmdemo.techlabs.core.utils;

import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.krmdemo.techlabs.core.dump.PrintUtils;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SequencedMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.krmdemo.techlabs.core.utils.CoreStringUtils.splitWords;
import static org.krmdemo.techlabs.core.utils.CountingUtils.countingLinkedMap;
import static org.krmdemo.techlabs.core.utils.CountingUtils.countingMap;
import static org.krmdemo.techlabs.core.utils.CountingUtils.countingMapLong;
import static org.krmdemo.techlabs.core.utils.CountingUtils.countingSortedMap;

/**
 * A unit-test to verify the utility-class {@link CountingUtils}
 * <hr/>
 * This test loads the file outside the test classpath - so be careful when copy/paste it.
 */
public class CountingUtilsTest {

    String LICENSE_TEXT = CoreFileUtils.loadFileAsText("./LICENSE");

    @Test
    void testCountingMap() {
        Map<String, Integer> cntMapInt = countingMap(splitWords(LICENSE_TEXT));
        Map<String, Long> cntMapLong = countingMapLong(splitWords(LICENSE_TEXT));
        assertThat(cntMapInt).hasSize(117);
        assertThat(cntMapLong).hasSize(117);
        assertThat(DumpUtils.dumpAsYamlTxt(cntMapInt))
            .isEqualTo(DumpUtils.dumpAsYamlTxt(cntMapInt));
    }

    @Test
    void testCountingSortedMap() {
        NavigableMap<String, Integer> cntMapSorted = countingSortedMap(splitWords(LICENSE_TEXT));
        assertThat(cntMapSorted.headMap("la-la-la")).hasSize(89);
        assertThat(cntMapSorted.tailMap("la-la-la")).hasSize(28);
        List<String> firstFiveWords = cntMapSorted.keySet().stream().limit(5).toList();
        assertThat(firstFiveWords).containsExactly("2025", "A", "ACTION", "AN", "AND");
    }

    @Test
    void testCountingLinkedMap() {
        SequencedMap<String, Integer> cntMapLinked = countingLinkedMap(splitWords(LICENSE_TEXT));
        assertThat(cntMapLinked).hasSize(117);
        List<String> firstFiveWords = cntMapLinked.keySet().stream().limit(5).toList();
        assertThat(firstFiveWords).containsExactly("MIT", "License", "Copyright", "c", "2025");
    }

    @Test
    void testCreationIsProhibited() {
        UnsupportedOperationException uoEx = assertThrows(UnsupportedOperationException.class,
            () -> CorePropsUtils.newInstance(CountingUtils.class)
        );
        assertThat(uoEx.getMessage()).isEqualTo(
            "Cannot instantiate utility-class org.krmdemo.techlabs.core.utils.CountingUtils");
    }
}
