//DEPS org.apache.commons:commons-text:1.14.0

/*
 * This Java-source is included into JBang-scripts using "//SOURCES ..." directive.
 * In other reposaitories the current library should be included as dependency with "//DEPS ..." directive
 */

import org.apache.commons.text.StringEscapeUtils;

import java.io.File;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * This class contains the same utility public methods as main-source classes declare.
 * In order to avoid self-loop dependency we have to repeat their declarations here.
 */
class JBangUtils {

    public static String dumpSysPropsAsJson() {
        return valueAsJson(dumpSysProps(), 0);
    }

    public static String dumpEnvVarsAsJson() {
        return valueAsJson(dumpEnvVars(), 0);
    }

    public static String dumpEnvVarsExAsJson() {
        return valueAsJson(dumpEnvVarsEx(), 0);
    }

    // --------------------------------------------------------------------------------------------

    public static NavigableMap<String,String> dumpSysProps() {
        return sortedMap(System.getProperties().entrySet().stream().map(toPropValue()));
    }

    public static Function<Map.Entry<?,?>, Map.Entry<String, String>> toPropValue() {
        return entry -> propValue(entry.getKey(), entry.getValue());
    }

    public static Map.Entry<String, String> propValue(Object propName, Object propValue) {
        return new AbstractMap.SimpleEntry<>(
            Objects.toString(propName),
            StringEscapeUtils.escapeJava(Objects.toString(propValue))
        );
    }

    public static <K extends Comparable<K>, V> NavigableMap<K, V>
    sortedMap(Stream<Map.Entry<K,V>> entries) {
        return entries.collect(toSortedMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static <T, K, U>
    Collector<T, ?, NavigableMap<K,U>>
    toSortedMap(Function<? super T, ? extends K> keyMapper,
                Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, (oldValue, newValue) -> newValue, TreeMap::new);
    }

    public static NavigableMap<String,String> dumpEnvVars() {
        return new TreeMap<>(System.getenv());
    }

    // ------------------------------------------------------------------------

    public static NavigableMap<String, Object> dumpEnvVarsEx() {
        return dumpEnvVars().entrySet().stream()
            .collect(toSortedMap(e -> transformPathVars(e)));
    }

    private static Object transformPathVars(Map.Entry<String, String> entry) {
        if (!entry.getKey().toUpperCase().endsWith("PATH")) {
            return entry.getValue();
        } else {
            return Arrays.stream(entry.getValue().split(File.pathSeparator)).toList();
        }
    }

    public static <K, V, U>
    Collector<Map.Entry<K, V>, ?, NavigableMap<K,U>>
    toSortedMap(Function<Map.Entry<K, V>, U> valueMapper) {
        return toSortedMap(Map.Entry::getKey, valueMapper);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * The default pretty-print JSON-identation consist of 2 spaces
     */
    private static final int INDENT_JSON_PRETTY_PRINT = 2;

    public static String seqAsJson(Collection<?> collObj, int indent) {
        if (collObj == null) {
            return "null";
        } else if (collObj.isEmpty()) {
            return "[]";
        }
        return collObj.stream()
            .map(item -> valueAsJson(item, indent + INDENT_JSON_PRETTY_PRINT))
            .collect(Collectors.joining(
                String.format(",%n%s", " ".repeat(indent + INDENT_JSON_PRETTY_PRINT)),
                String.format("[%n%s", " ".repeat(indent + INDENT_JSON_PRETTY_PRINT)),
                String.format("%n%s]", " ".repeat(indent))
            ));
    }

    public static String mapAsJson(Map<?, ?> mapObj, int indent) {
        if (mapObj == null) {
            return "null";
        } else if (mapObj.isEmpty()) {
            return "{}";
        }
        return mapObj.entrySet().stream()
            .map(e -> entryAsJson(e, indent + INDENT_JSON_PRETTY_PRINT))
            .collect(Collectors.joining(
                String.format(",%n%s", " ".repeat(indent + INDENT_JSON_PRETTY_PRINT)),
                String.format("{%n%s", " ".repeat(indent + INDENT_JSON_PRETTY_PRINT)),
                String.format("%n%s}", " ".repeat(indent))
            ));
    }

    private static String entryAsJson(Map.Entry<?,?> entry, int indent) {
        String entryKeyStr = "" + entry.getKey();
        String entryValueStr = valueAsJson(entry.getValue(), indent);
        return String.format("\"%s\": %s", entryKeyStr, entryValueStr);
    }

    private static String valueAsJson(Object obj, int indent) {
        return obj == null ? "null" : switch (obj) {
            case Map<?,?> mapObj -> mapAsJson(mapObj, indent);
            case Collection<?> seqObj -> seqAsJson(seqObj, indent);
            default -> String.format("\"%s\"", obj); // <-- think about escaping
        };
    }
}