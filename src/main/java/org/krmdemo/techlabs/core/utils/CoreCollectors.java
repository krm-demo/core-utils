package org.krmdemo.techlabs.core.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * A factory of {@link Collector}s that allows to collect the stream of values into linked or sorted sets
 * and the stream of {@link Map.Entry entries} and any other types into the linked or the sorted map.
 * An enumeration {@link MergeFunction} could be used to handle the entries with the same {@link Map.Entry#getKey() key}.
 */
public class CoreCollectors {

    /**
     * The same as {@link Collectors#toList()}, but tp collect the items in <b>reverse order</b>.
     *
     * @return a {@code Collector} which collects all the input elements into an {@link ArrayList} in <b>reverse order</b>.
     * @param <T> the type of the elements in stream to collect
     */
    public static <T> Collector<T, ?, ArrayList<T>> toListReversed() {
        return Collector.of(
            ArrayList::new,
            ArrayList::addFirst,
            (left, right) -> {
                right.addAll(0, left);
                return right;
            }
        );
    }

    /**
     * The same as {@link Collectors#toCollection JDK's toCollection(...)}, but produces {@link LinkedList}.
     *
     * @return a {@code Collector} which collects all the input elements into a {@link LinkedList}.
     * @param <T> the type of the elements in stream to collect
     */
    public static <T> Collector<T, ?, List<T>> toLinkedList() {
        return Collectors.toCollection(LinkedList::new);
    }

    /**
     * The same as {@link #toLinkedList()}, but collecting the items in <b>reverse order</b>.
     *
     * @return a {@code Collector} which collects all the input elements into a {@link LinkedList} in <b>reverse order</b>.
     * @param <T> the type of the elements in stream to collect
     */
    public static <T> Collector<T, ?, LinkedList<T>> toLinkedListReversed() {
        return Collector.of(
            LinkedList::new,
            LinkedList::addFirst,
            (left, right) -> {
                right.addAll(0, left);
                return right;
            }
        );
    }

    /**
     * The same as {@link Collectors#toCollection JDK's toCollection(...)}, but produces {@link ArrayDeque}.
     *
     * @return a {@code Collector} which collects all the input elements into a {@link ArrayDeque}.
     * @param <T> the type of the elements in stream to collect
     */
    public static <T> Collector<T, ?, Deque<T>> toDeque() {
        return Collectors.toCollection(ArrayDeque::new);
    }

    /**
     * The same as {@link #toDeque()}, but collecting the items in <b>reverse order</b>.
     *
     * @return a {@code Collector} which collects all the input elements into a {@link ArrayDeque} in <b>reverse order</b>.
     * @param <T> the type of the elements in stream to collect
     */
    public static <T> Collector<T, ?, Deque<T>> toDequeReversed() {
        return Collector.of(
            ArrayDeque::new,
            Deque::addFirst,
            (left, right) -> {
                right.addAll(left);
                return right;
            }
        );
    }

    /**
     * The same as {@link Collectors#toCollection JDK's toCollection(...)}, but produces {@link LinkedHashSet}.
     *
     * @return a {@code Collector} which collects all the input elements into a {@link LinkedHashSet}.
     * @param <T> the type of the elements in stream to collect
     */
    public static <T> Collector<T, ?, SequencedSet<T>> toLinkedSet() {
        return Collectors.toCollection(LinkedHashSet::new);
    }

    /**
     * The same as {@link Collectors#toCollection JDK's toCollection(...)}, but produces {@link TreeSet}.
     *
     * @return a {@link Collector}, which collects all the input elements into a {@link TreeSet}
     * @param <T> the type of the elements in stream to collect (must implement {@link Comparable})
     */
    public static <T extends Comparable<T>> Collector<T, ?, NavigableSet<T>> toSortedSet() {
        return Collectors.toCollection(TreeSet::new);
    }

    /**
     * The same as {@link #toSortedSet() toSortedSet()}, but with {@code comparator} as parameter
     *
     * @param comparator a function to compare the elements
     * @return  a {@link Collector}, which collects all the input elements into a {@link TreeSet}
     * @param <T> the type of the elements in stream to collect (not necessary to implement {@link Comparable})
     */
    public static <T> Collector<T, ?, NavigableSet<T>>
    toSortedSet(Comparator<T> comparator) {
        return Collectors.toCollection(() -> new TreeSet<>(Objects.requireNonNull(comparator)));
    }

    /**
     * The same as {@link Collectors#toMap JDK's toMap(...)}, but the elements of collecting stream are required to be
     * of type {@link Map.Entry}, and the produced sorted-map accepts them one by one like
     * if the method {@link Map#putAll} would be invoked (the latest entries overwrite the existing ones).
     *
     * @return a {@link Collector}, which collects all the input elements into a {@link TreeMap}
     * @param <K> the type of {@link Map.Entry#getKey() key} for {@link Map.Entry entries} in input stream
     * @param <U> the type of {@link Map.Entry#getValue() value} for {@link Map.Entry entries} in input stream
     */
    public static <K, U>
    Collector<Map.Entry<K,U>, ?, NavigableMap<K,U>>
    toSortedMap() {
        return toSortedMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    /**
     * The same as {@link #toSortedMap() toSortedMap()}, but the parameter {@code valueMapper} allows to transform
     * <b>the whole input entry</b> of type {@link Map.Entry Map.Entry&lt;K,V&gt;}
     * into <b>the value of output entry</b> of type {@link Map.Entry Map.Entry&lt;K,U&gt;},
     * and the {@link Map.Entry#getKey() key} of input entry remains the same in output one.
     * <hr/>
     * <i>the brief summary is:</i><ul>
     *     <li>{@code valueMapper} function accepts {@link Map.Entry Map.Entry&lt;K,V&gt;} and returns {@code <U>}</li>
     * </ul>
     *
     * @param valueMapper the function to transform the {@link Map.Entry#getValue() value}
     * @return a {@link Collector} that which collects all the input elements into a {@link TreeMap}
     * @param <K> the type of {@link Map.Entry#getKey() key} for {@link Map.Entry entries} in input and output streams
     * @param <V> the type of {@link Map.Entry#getValue() value} for {@link Map.Entry entries} in output stream
     * @param <U> the type of {@link Map.Entry#getValue() value} for {@link Map.Entry entries} in input stream
     */
    public static <K, V, U>
    Collector<Map.Entry<K, V>, ?, NavigableMap<K,U>>
    toSortedMap(Function<Map.Entry<K, V>, U> valueMapper) {
        return toSortedMap(Map.Entry::getKey, valueMapper);
    }

    /**
     * The same as {@link Collectors#toMap JDK's toMap(...)}, but produces {@link TreeMap} with overwriting the duplicates.
     * <hr/>
     * <i>the brief summary is:</i><ul>
     *     <li>{@code keyMapper} function accepts {@code <T>} and returns the type of entry-key {@code <K>}</li>
     *     <li>{@code valueMapper} function accepts {@code <T>} and returns the type of entry-value {@code <U>}</li>
     * </ul>
     *
     * @param keyMapper a <i>key-mapping</i> function to produce keys
     * @param valueMapper a <i>value-mapping</i> function to produce values
     * @return a {@link Collector} that collects elements into a {@link NavigableMap},
     * whose keys are the result of applying a {@code keyMapper} function to the input elements,
     * and whose values are the result of applying a {@code valueMapper} function to all input elements
     * equal to the key and overwriting the existing values with new ones
     * (according to {@link MergeFunction#OVERWRITE enum-value OVERWRITE}).
     * @param <T> the type of the input elements
     * @param <K> the output type of the <i>key-mapping</i> function
     * @param <U> the output type of the <i>value-mapping</i> function
     */
    public static <T, K, U>
    Collector<T, ?, NavigableMap<K,U>>
    toSortedMap(Function<? super T, ? extends K> keyMapper,
                Function<? super T, ? extends U> valueMapper) {
        return toSortedMap(keyMapper, valueMapper, MergeFunction.OVERWRITE);
    }

    /**
     * The same as {@link #toSortedMap(Function, Function) toSortedMap(keyMapper,valueMapper)},
     * but allows to handle duplicates with {@link MergeFunction mergeFunction} parameter.
     * <hr/>
     * <i>the brief summary is:</i><ul>
     *     <li>{@code keyMapper} function accepts {@code <T>} and returns the type of entry-key {@code <K>}</li>
     *     <li>{@code valueMapper} function accepts {@code <T>} and returns the type of entry-value {@code <U>}</li>
     * </ul>
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the <i>key-mapping</i> function
     * @param <U> the output type of the <i>value-mapping</i> function
     * @param keyMapper a <i>key-mapping</i> function to produce keys
     * @param valueMapper a <i>value-mapping</i> function to produce values
     * @param mergeFunction one of values that are represented by {@link MergeFunction}-enum
     * @return a {@link Collector}, that collects elements into a {@link TreeMap},
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
     * The same as {@link Collectors#toMap JDK's toMap(...)}, but produces {@link LinkedHashMap} with overwriting the duplicates.
     * <hr/>
     * <i>the brief summary is:</i><ul>
     *     <li>{@code keyMapper} function accepts {@code <T>} and returns the type of entry-key {@code <K>}</li>
     *     <li>{@code valueMapper} function accepts {@code <T>} and returns the type of entry-value {@code <U>}</li>
     * </ul>
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the <i>key-mapping</i>  function
     * @param <U> the output type of the <i>value-mapping</i> function
     * @param keyMapper a <i>key-mapping</i> function to produce keys
     * @param valueMapper a <i>value-mapping</i> function to produce values
     * @return a {@link Collector} that collects elements into a {@link LinkedHashMap},
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
     * The same as {@link #toLinkedMap(Function, Function) toLinkedMap(keyMapper,valueMapper)},
     * but allows to handle duplicates with {@link MergeFunction mergeFunction} parameter.
     * <hr/>
     * <i>the brief summary is:</i><ul>
     *     <li>{@code keyMapper} function accepts {@code <T>} and returns the type of entry-key {@code <K>}</li>
     *     <li>{@code valueMapper} function accepts {@code <T>} and returns the type of entry-value {@code <U>}</li>
     * </ul>
     *
     * @param <T> the type of the input elements
     * @param <K> the output type of the <i>key-mapping</i>  function
     * @param <U> the output type of the <i>value-mapping</i> function
     * @param keyMapper a <i>key-mapping</i> function to produce keys
     * @param valueMapper a <i>value-mapping</i> function to produce values
     * @param mergeFunction one of values that are represented by {@link MergeFunction}-enum
     * @return a {@link Collector} that collects elements into a {@link LinkedHashMap},
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

    // --------------------------------------------------------------------------------------------

    private CoreCollectors() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
