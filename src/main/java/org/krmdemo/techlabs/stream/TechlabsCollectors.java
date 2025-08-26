package org.krmdemo.techlabs.stream;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.propValue;

/**
 * TODO: provide the comprehensive Java-Doc !!!
 */
public class TechlabsCollectors {

    /**
     * The same as {@link Collectors#toCollection}, but produces {@link TreeSet}.
     *
     * @param <T> the type of the input elements
     * @return a {@code Collector} which collects all the input elements into a {@link TreeSet}.
     */
    public static <T extends Comparable<T>> Collector<T, ?, NavigableSet<T>> toSortedSet() {
        return Collectors.toCollection(TreeSet::new);
    }

    /**
     * The same as {@link Collectors#toCollection}, but produces {@link LinkedHashSet}.
     *
     * @param <T> the type of the input elements
     * @return a {@code Collector} which collects all the input elements into a {@link TreeSet}.
     */
    public static <T> Collector<T, ?, SequencedSet<T>> toLinkedSet() {
        return Collectors.toCollection(LinkedHashSet::new);
    }

    public static Function<Map.Entry<?,?>, Map.Entry<String, String>> toPropValue() {
        return entry -> propValue(entry.getKey(), entry.getValue());
    }

    public static <K, U>
    Collector<Map.Entry<K,U>, ?, NavigableMap<K,U>>
    toSortedMap() {
        return toSortedMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <K, V, U>
    Collector<Map.Entry<K, V>, ?, NavigableMap<K,U>>
    toSortedMap(Function<Map.Entry<K, V>, U> valueMapper) {
        return toSortedMap(Map.Entry::getKey, valueMapper);
    }

    /**
     * The same as {@link Collectors#toMap}, but produces {@link TreeMap} with overwriting the duplicates.
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper a <i>key-mapping</i> function to produce keys
     * @param valueMapper a <i>value-mapping</i> function to produce values
     * @return a @{link Collector} which collects elements into a @{link Map}
     * whose keys are the result of applying a {@code keyMapper} function to the input elements,
     * and whose values are the result of applying a {@code valueMapper} function to all input elements
     * equal to the key and overwriting the existing values with new ones
     * (according to {@link MergeFunction#OVERWRITE enum-value OVERWRITE}).
     */
    public static <T, K, U>
    Collector<T, ?, NavigableMap<K,U>>
    toSortedMap(Function<? super T, ? extends K> keyMapper,
                Function<? super T, ? extends U> valueMapper) {
        return toSortedMap(keyMapper, valueMapper, MergeFunction.OVERWRITE);
    }

    /**
     * The same as {@link #toSortedMap(Function, Function)}, but allows to handle duplicates with {@link MergeFunction}.
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction one of values that are represented by {@link MergeFunction}-enum
     * @return a @{link Collector} which collects elements into a @{link Map}
     * whose keys are the result of applying a {@code keyMapper} function to the input elements,
     * and whose values are the result of applying a {@code valueMapper} function to all input elements
     * equal to the key and combining them using the {@code mergeFunction}.
     */
    public static <T, K, U>
    Collector<T, ?, NavigableMap<K,U>>
    toSortedMap(Function<? super T, ? extends K> keyMapper,
                Function<? super T, ? extends U> valueMapper,
                MergeFunction mergeFunction) {
        return Collectors.toMap(keyMapper, valueMapper, mergeFunction.op(), TreeMap::new);
    }

    /**
     * The same as {@link Collectors#toMap}, but produces {@link LinkedHashMap} with overwriting the duplicates.
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper a <i>key-mapping</i> function to produce keys
     * @param valueMapper a <i>value-mapping</i> function to produce values
     * @return a @{link Collector} which collects elements into a @{link Map}
     * whose keys are the result of applying a {@code keyMapper} function to the input elements,
     * and whose values are the result of applying a {@code valueMapper} function to all input elements
     * equal to the key and overwriting the existing values with new ones
     * (according to {@link MergeFunction#OVERWRITE enum-value OVERWRITE}).
     */
    public static <T, K, U>
    Collector<T, ?, SequencedMap<K,U>>
    toLinkedMap(Function<? super T, ? extends K> keyMapper,
                Function<? super T, ? extends U> valueMapper) {
        return toLinkedMap(keyMapper, valueMapper, MergeFunction.OVERWRITE);
    }

    /**
     * The same as {@link #toLinkedMap(Function, Function)}, but allows to handle duplicates with {@link MergeFunction}.
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the key mapping function
     * @param <U> the output type of the value mapping function
     * @param keyMapper a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @param mergeFunction one of values that are represented by {@link MergeFunction}-enum
     * @return a @{link Collector} which collects elements into a @{link Map}
     * whose keys are the result of applying a {@code keyMapper} function to the input elements,
     * and whose values are the result of applying a {@code valueMapper} function to all input elements
     * equal to the key and combining them using the {@code mergeFunction}.
     */
    public static <T, K, U>
    Collector<T, ?, SequencedMap<K,U>>
    toLinkedMap(Function<? super T, ? extends K> keyMapper,
                Function<? super T, ? extends U> valueMapper,
                MergeFunction mergeFunction) {
        return Collectors.toMap(keyMapper, valueMapper, mergeFunction.op(), LinkedHashMap::new);
    }
}
