package org.krmdemo.techlabs.core.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

/**
 * Utility-Class to build a counting-map from the {@link Stream} of any objects
 * that have properly implemented {@link Object#equals(Object)} and {@link Object#hashCode()}
 * (and {@link Comparable#compareTo(Object) for sorted counting-maps}).
 */
public class CountingUtils {

    public static <V> Map<V, Long> countingMapLong(Stream<V> valuesStream) {
        return valuesStream.collect(groupingBy(identity(), counting()));
    }
    public static <V> Map<V, Integer> countingMap(Stream<V> valuesStream) {
        return valuesStream.collect(groupingBy(identity(), summingInt(v -> 1)));
    }
    public static <V> NavigableMap<V, Integer> countingSortedMap(Stream<V> valuesStream) {
        return valuesStream.collect(groupingBy(identity(), TreeMap::new, summingInt(v -> 1)));
    }
    public static <V> LinkedHashMap<V, Integer> countingLinkedMap(Stream<V> valuesStream) {
        return valuesStream.collect(groupingBy(identity(), LinkedHashMap::new, summingInt(v -> 1)));
    }

    // ------------------------------------------------------------------------------------------------------

    private CountingUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
