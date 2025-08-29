package org.krmdemo.techlabs.stream;

import org.apache.commons.text.StringEscapeUtils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.stream.TechlabsCollectors.toLinkedMap;
import static org.krmdemo.techlabs.stream.TechlabsCollectors.toLinkedSet;
import static org.krmdemo.techlabs.stream.TechlabsCollectors.toSortedMap;
import static org.krmdemo.techlabs.stream.TechlabsCollectors.toSortedSet;

/**
 * TODO: provide the comprehensive Java-Doc !!!
 */
public class TechlabsStreamUtils {

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T extends Comparable<T>> NavigableSet<T> sortedSet(T... valuesArr) {
        return sortedSet(Arrays.stream(valuesArr));
    }

    public static <T extends Comparable<T>> NavigableSet<T> sortedSet(Stream<T> values) {
        return values.collect(toSortedSet());
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> SequencedSet<T> linkedSet(T... valuesArr) {
        return linkedSet(Arrays.stream(valuesArr));
    }

    public static <T> SequencedSet<T> linkedSet(Stream<T> values) {
        return values.collect(toLinkedSet());
    }

    public static <K, V> Map.Entry<K, V> keyValue(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public static <V> Map.Entry<String, V> nameValue(String name, V value) {
        return keyValue(name, value);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K extends Comparable<K>, V> NavigableMap<K, V>
    sortedMap(Map.Entry<K,V>... entriesArr) {
        return sortedMap(Arrays.stream(entriesArr));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K extends Comparable<K>, V> NavigableMap<K, V>
    sortedMap(MergeFunction mergeFunction, Map.Entry<K,V>... entriesArr) {
        return sortedMap(mergeFunction, Arrays.stream(entriesArr));
    }

    public static <K extends Comparable<K>, V> NavigableMap<K, V>
    sortedMap(MergeFunction mergeFunction, Stream<Map.Entry<K,V>> entries) {
        return entries.collect(toSortedMap(Map.Entry::getKey, Map.Entry::getValue, mergeFunction));
    }

    public static <K extends Comparable<K>, V> NavigableMap<K, V>
    sortedMap(Stream<Map.Entry<K,V>> entries) {
        return entries.collect(toSortedMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> SequencedMap<K, V>
    linkedMap(Map.Entry<K,V>... entriesArr) {
        return linkedMap(Arrays.stream(entriesArr));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> SequencedMap<K, V>
    linkedMap(MergeFunction mergeFunction, Map.Entry<K,V>... entriesArr) {
        return linkedMap(mergeFunction, Arrays.stream(entriesArr));
    }

    public static <K, V> SequencedMap<K, V>
    linkedMap(MergeFunction mergeFunction, Stream<Map.Entry<K,V>> entries) {
        return entries.collect(toLinkedMap(Map.Entry::getKey, Map.Entry::getValue, mergeFunction));
    }

    public static <K, V> SequencedMap<K, V>
    linkedMap(Stream<Map.Entry<K,V>> entries) {
        return entries.collect(toLinkedMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private TechlabsStreamUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
