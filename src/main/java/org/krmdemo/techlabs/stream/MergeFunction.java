package org.krmdemo.techlabs.stream;

import java.util.function.BinaryOperator;

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
                    "attempt to merge values '%s' and '%s', which is NOT allowed", oldValue, newValue));
            };
        }
    };

    abstract <U> BinaryOperator<U> op();
}
