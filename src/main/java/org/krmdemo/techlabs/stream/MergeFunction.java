package org.krmdemo.techlabs.stream;

import java.util.Map;
import java.util.function.BinaryOperator;

/**
 * This enumeration is used to handle the entries with the same {@link Map.Entry#getKey() key},
 * when the stream of {@link Map.Entry entries} are collected into linked map or sorted map.
 */
public enum MergeFunction {

    /**
     * The default merging-function, that emulates the behaviour of consecutive invocation
     * of the method {@link Map#put(Object, Object) put(key,value)} with the same {@code key}.
     * So, <b>the last entry</b> with same {@code key} overwrites the values of all previous ones.
     */
    OVERWRITE {
        @Override
        <U> BinaryOperator<U> op() {
            return (oldValue, newValue) -> newValue;
        }
    },

    /**
     * This merging-function ignores any subsequent attempts to overwrite the existing {@code value}
     * for the same {@code key}. So, <b>the first entry</b> with same {@code key} remains in the result map.
     */
    IGNORE {
        @Override
        <U> BinaryOperator<U> op() {
            return (oldValue, newValue) -> oldValue;
        }
    },

    /**
     * This merging-function guarantees that the source stream does not contain entries with the same keys.
     * Otherwise, an {@link IllegalStateException} is thrown on the first attempt to violate that.
     */
    THROW {
        @Override
        <U> BinaryOperator<U> op() {
            return (oldValue, newValue) -> {
                throw new IllegalStateException(String.format(
                    "attempt to overwrite the value '%s' with the value '%s', which is NOT allowed",
                    oldValue, newValue
                ));
            };
        }
    };

    /**
     * @return the merging-function as {@link BinaryOperator}
     * @param <U> the type of value
     */
    abstract <U> BinaryOperator<U> op();
}
