package org.krmdemo.techlabs.stream;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.stream.CoreCollectors.toLinkedMap;
import static org.krmdemo.techlabs.stream.CoreCollectors.toLinkedSet;
import static org.krmdemo.techlabs.stream.CoreCollectors.toSortedMap;
import static org.krmdemo.techlabs.stream.CoreCollectors.toSortedSet;

/**
 * Utility-class that provides methods to transform the stream or var-args-arrays of values into linked or sorted sets
 * and stream or var-args-arrays of {@link Map.Entry entries} into linked or sorted map.
 * An enumeration {@link MergeFunction} could be used to handle the entries with the same {@link Map.Entry#getKey() key}.
 */
public class CoreStreamUtils {

    /**
     * @param valuesArr var-args-arrays of values
     * @return the sorted set of those values as {@link NavigableSet}
     * @param <T> the type of value
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T extends Comparable<T>> NavigableSet<T> sortedSet(T... valuesArr) {
        return sortedSet(Arrays.stream(valuesArr));
    }

    /**
     * @param values stream of values
     * @return the sorted set of those values as {@link NavigableSet}
     * @param <T> the type of value
     */
    public static <T extends Comparable<T>> NavigableSet<T> sortedSet(Stream<T> values) {
        return values.collect(toSortedSet());
    }

    /**
     * @param valuesArr var-args-arrays of values
     * @return the linked set of those values as {@link SequencedSet}
     * @param <T> the type of value
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> SequencedSet<T> linkedSet(T... valuesArr) {
        return linkedSet(Arrays.stream(valuesArr));
    }

    /**
     * @param values stream of values
     * @return the linked set of those values as {@link SequencedSet}
     * @param <T> the type of value
     */
    public static <T> SequencedSet<T> linkedSet(Stream<T> values) {
        return values.collect(toLinkedSet());
    }

    /**
     * A factory-method to produce an instance of {@link Map.Entry} from {@code key} and {@code value}.
     *
     * @param key a key of entry to create
     * @param value a value of entry to create
     * @return an instance of {@link Map.Entry} as {@link AbstractMap.SimpleEntry}
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static <K, V> Map.Entry<K, V> keyValue(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    /**
     * The same as {@link #keyValue(Object, Object) keyValue(K key, V value)},
     * but when the type of {@code key} is {@link String} (we can call such {@code key} as {@code name} in this case).
     *
     * @param name a name of entry to create
     * @param value a value of entry to create
     * @return an instance of {@link Map.Entry} as {@link AbstractMap.SimpleEntry}
     * @param <V> the type of value
     */
    public static <V> Map.Entry<String, V> nameValue(String name, V value) {
        return keyValue(name, value);
    }

    /**
     * Transform the var-args-arrays of entries into the sorted map,
     * where the entries with the same {@link Map.Entry#getKey() key} are merged
     * with the default merge-function {@link MergeFunction#OVERWRITE}.
     * <hr/>
     * In order to avoid {@link NullPointerException} all invalid entries are filtered out,
     * where the filter is based on using {@link #entryIsValid(Map.Entry)} function.
     *
     * @param entriesArr var-args-arrays of entries
     * @return the sorted map, which is collected from those entries, as {@link NavigableMap},
     * @param <K> the type of key
     * @param <V> the type of value
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K extends Comparable<K>, V> NavigableMap<K, V>
    sortedMap(Map.Entry<K,V>... entriesArr) {
        return sortedMap(Arrays.stream(entriesArr).filter(CoreStreamUtils::entryIsValid));
    }

    /**
     * Transform the var-args-arrays of entries into the sorted map,
     * where the entries with the same {@link Map.Entry#getKey() key} are merged
     * with the passed {@code mergeFunction}.
     * <hr/>
     * In order to avoid {@link NullPointerException} all invalid entries are filtered out,
     * where the filter is based on using {@link #entryIsValid(Map.Entry)} function.
     *
     * @param mergeFunction the merge-function to handle the entries with the same {@link Map.Entry#getKey() key}
     * @param entriesArr var-args-arrays of entries
     * @return the sorted map, which is collected from those entries, as {@link NavigableMap},
     * @param <K> the type of key
     * @param <V> the type of value
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K extends Comparable<K>, V> NavigableMap<K, V>
    sortedMap(MergeFunction mergeFunction, Map.Entry<K,V>... entriesArr) {
        return sortedMap(mergeFunction, Arrays.stream(entriesArr));
    }

    /**
     * Transform the var-args-arrays of entries into the sorted map,
     * where the entries with the same {@link Map.Entry#getKey() key} are merged
     * with the default merge-function {@link MergeFunction#OVERWRITE}.
     * <hr/>
     * In order to avoid {@link NullPointerException} all invalid entries are filtered out,
     * where the filter is based on using {@link #entryIsValid(Map.Entry)} function.
     *
     * @param entries stream of entries
     * @return the sorted map, which is collected from those entries, as {@link NavigableMap},
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static <K extends Comparable<K>, V> NavigableMap<K, V>
    sortedMap(Stream<Map.Entry<K,V>> entries) {
        return entries.collect(toSortedMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Transform the var-args-arrays of entries into the sorted map,
     * where the entries with the same {@link Map.Entry#getKey() key} are merged
     * with the passed {@code mergeFunction}.
     * <hr/>
     * In order to avoid {@link NullPointerException} all invalid entries are filtered out,
     * where the filter is based on using {@link #entryIsValid(Map.Entry)} function.
     *
     * @param mergeFunction the merge-function to handle the entries with the same {@link Map.Entry#getKey() key}
     * @param entries stream of entries
     * @return the sorted map, which is collected from those entries, as {@link NavigableMap},
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static <K extends Comparable<K>, V> NavigableMap<K, V>
    sortedMap(MergeFunction mergeFunction, Stream<Map.Entry<K,V>> entries) {
        return entries.collect(toSortedMap(Map.Entry::getKey, Map.Entry::getValue, mergeFunction));
    }

    /**
     * Transform the var-args-arrays of entries into the linked map,
     * where the entries with the same {@link Map.Entry#getKey() key} are merged
     * with the default merge-function {@link MergeFunction#OVERWRITE}.
     * <hr/>
     * In order to avoid {@link NullPointerException} all invalid entries are filtered out,
     * where the filter is based on using {@link #entryIsValid(Map.Entry)} function.
     *
     * @param entriesArr var-args-arrays of entries
     * @return the linked map, which is collected from {@code entries}, as {@link SequencedMap},
     * @param <K> the type of key
     * @param <V> the type of value
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> SequencedMap<K, V>
    linkedMap(Map.Entry<K,V>... entriesArr) {
        return linkedMap(Arrays.stream(entriesArr).filter(CoreStreamUtils::entryIsValid));
    }

    /**
     * Transform the var-args-arrays of entries into the linked map,
     * where the entries with the same {@link Map.Entry#getKey() key} are merged
     * with the passed {@code mergeFunction}.
     * <hr/>
     * In order to avoid {@link NullPointerException} all invalid entries are filtered out,
     * where the filter is based on using {@link #entryIsValid(Map.Entry)} function.
     *
     * @param mergeFunction the merge-function to handle the entries with the same {@link Map.Entry#getKey() key}
     * @param entriesArr var-args-arrays of entries
     * @return the linked map, which is collected from {@code entries}, as {@link SequencedMap},
     * @param <K> the type of key
     * @param <V> the type of value
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> SequencedMap<K, V>
    linkedMap(MergeFunction mergeFunction, Map.Entry<K,V>... entriesArr) {
        return linkedMap(mergeFunction, Arrays.stream(entriesArr));
    }

    /**
     * Transform the stream of entries into the linked map,
     * where the entries with the same {@link Map.Entry#getKey() key} are merged
     * with the default merge-function {@link MergeFunction#OVERWRITE}.
     * <hr/>
     * In order to avoid {@link NullPointerException} all invalid entries are filtered out,
     * where the filter is based on using {@link #entryIsValid(Map.Entry)} function.
     *
     * @param entries stream of entries
     * @return the linked map, which is collected from {@code entries}, as {@link SequencedMap},
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static <K, V> SequencedMap<K, V>
    linkedMap(Stream<Map.Entry<K,V>> entries) {
        return entries.collect(toLinkedMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Transform the var-args-arrays of entries into the linked map,
     * where the entries with the same {@link Map.Entry#getKey() key} are merged
     * with the passed {@code mergeFunction}.
     * <hr/>
     * In order to avoid {@link NullPointerException} all invalid entries are filtered out,
     * where the filter is based on using {@link #entryIsValid(Map.Entry)} function.
     *
     * @param mergeFunction the merge-function to handle the entries with the same {@link Map.Entry#getKey() key}
     * @param entries stream of entries
     * @return the linked map, which is collected from {@code entries}, as {@link SequencedMap},
     * @param <K> the type of key
     * @param <V> the type of value
     */
    public static <K, V> SequencedMap<K, V>
    linkedMap(MergeFunction mergeFunction, Stream<Map.Entry<K,V>> entries) {
        return entries.collect(toLinkedMap(Map.Entry::getKey, Map.Entry::getValue, mergeFunction));
    }

    /**
     * In order to avoid {@link NullPointerException} in JDK's collectors
     * all entries that are {@code null} or whose key or value is {@code null} should be filtered out.
     *
     * @param entry an entry to filter
     * @return {@code true} if the entry is valid and otherwise - {@code false}
     */
    public static boolean entryIsValid(Map.Entry<?,?> entry) {
        return entry != null && entry.getKey() != null && entry.getValue() != null;
    }

    // --------------------------------------------------------------------------------------------

    private CoreStreamUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
