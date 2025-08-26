package org.krmdemo.techlabs.stream;

import java.util.function.BinaryOperator;

/**
 * TODO: provide the comprehensive Java-Doc !!!
 */
public enum MergeFunction {

    OVERWRITE {
        @Override
        <U> BinaryOperator<U> op() {
            return (oldValue, newValue) -> newValue;
        }
    },

    IGNORE {
        @Override
        <U> BinaryOperator<U> op() {
            return (oldValue, newValue) -> oldValue;
        }
    },

    THROW {
        @Override
        <U> BinaryOperator<U> op() {
            return (oldValue, newValue) -> {
                throw new IllegalStateException(String.format(
                    "attempt to overwrite the value '%s' with the value'%s', which is NOT allowed",
                    newValue, oldValue
                ));
            };
        }
    };

    abstract <U> BinaryOperator<U> op();
}
