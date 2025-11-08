package org.krmdemo.techlabs.core.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SequencedMap;
import java.util.TreeMap;
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

    /**
     * @param valuesStream a stream of elements of type {@code <V>}
     * @return a counting-map of type {@code Map<V,Long>}
     * @param <V> the type of elements
     */
    public static <V> Map<V, Long> countingMapLong(Stream<V> valuesStream) {
        return valuesStream.collect(groupingBy(identity(), counting()));
    }

    /**
     * @param valuesStream a stream of elements of type {@code <V>}
     * @return a counting-map of type {@code Map<V,Integer>}
     * @param <V> the type of elements
     */
    public static <V> Map<V, Integer> countingMap(Stream<V> valuesStream) {
        return valuesStream.collect(groupingBy(identity(), summingInt(v -> 1)));
    }

    /**
     * @param valuesStream a stream of elements of type {@code <V>}
     * @return a <b>sorted</b> counting-map of type {@code Map<V,Integer>}
     * @param <V> the type of elements (expected to implement {@link Comparable})
     */
    public static <V> NavigableMap<V, Integer> countingSortedMap(Stream<V> valuesStream) {
        return valuesStream.collect(groupingBy(identity(), TreeMap::new, summingInt(v -> 1)));
    }

    /**
     * Getting the counting-map, where the order of elements corresponds to the first occurrence of value.
     *
     * @param valuesStream a stream of elements of type {@code <V>}
     * @return a <b>sorted</b> counting-map of type {@code Map<V,Integer>}
     * @param <V> the type of elements (expected to implement {@link Comparable})
     */
    public static <V> SequencedMap<V, Integer> countingLinkedMap(Stream<V> valuesStream) {
        return valuesStream.collect(groupingBy(identity(), LinkedHashMap::new, summingInt(v -> 1)));
    }

    // ------------------------------------------------------------------------------------------------------

    private CountingUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
