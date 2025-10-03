package org.krmdemo.techlabs.thtool;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility-class to work with nested properties
 */
public class ThToolCtxUtils {

    /**
     * The same as {@link #propValue(Object, String...)}, but cast the final value to string,
     * or transform that value using standard {@link String#valueOf(Object)}
     * (which is the same as invoking {@link Object#toString()} on that object)
     *
     * @param parent the root value to start the property-chain
     * @param propNameChain the chain of nested property-names
     * @return the value of the last property in that property-chain as {@link String}
     */
    public static String propValueStr(Object parent, String... propNameChain) {
        Object propValueObj = propValue(parent, propNameChain);
        return propValueObj == null ? null : String.valueOf(propValueObj);
    }

    /**
     * Getting the value of nested properties of <b>{@code th-tool}</b>-variable by providing the chain of property-names,
     * which is quite the same as Thymeleaf-Engine does when processing the expression
     * of form {@code ${varName.propName.nestedPropName...}}. At the moment only nested objects
     * of type {@link Map Map&lt;String,Object&gt;} are supported.
     *
     * @param parent the root value to start the property-chain
     * @param propNameChain the chain of nested property-names
     * @return the value of the last property in that property-chain
     */
    @SuppressWarnings("rawtypes")
    public static Object propValue(Object parent, String... propNameChain) {
        Object value = parent;
        for (int propNameIndex = 0; propNameIndex < propNameChain.length; propNameIndex++) {
            String propName = propNameChain[propNameIndex];
            if (value instanceof Map parentMap) {
                value = parentMap.get(propName);
            } else {
                // all values except the last one are expected to be maps
                throw new IllegalArgumentException(String.format(
                    "Could not resolve the property-chain '%s' - the tail '%s' is unresolved " +
                        "(the value of '%s' is expected to be a Map, but %s).",
                    propChainStr(propNameChain),
                    propChainTailStr(propNameIndex, propNameChain),
                    propChainHeadStr(propNameIndex, propNameChain),
                    wrongValueDescription(value)
                ));
            }
        }
        return value;
    }

    private static String wrongValueDescription(Object value) {
        return value == null ? "it's null" : String.format("it's of type <<%s>>", value.getClass());
    }

    private static String propChainStr(String... propChain) {
        return propChainStr(0, propChain.length, propChain);
    }

    private static String propChainHeadStr(int propIndex, String... propChain) {
        return propChainStr(0, propIndex, propChain);
    }

    private static String propChainTailStr(int propIndex, String... propChain) {
        return propChainStr(propIndex, propChain.length, propChain);
    }

    private static String propChainStr(int indexFrom, int indexTo, String... propChain) {
        return Arrays.stream(propChain, indexFrom, indexTo).collect(Collectors.joining("."));
    }

    // --------------------------------------------------------------------------------------------

    private ThToolCtxUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
