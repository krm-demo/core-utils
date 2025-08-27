package org.krmdemo.techlabs.sysdump;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.krmdemo.techlabs.stream.TechlabsCollectors.toPropValue;
import static org.krmdemo.techlabs.stream.TechlabsCollectors.toSortedMap;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedMap;

/**
 * TODO: provide the comprehensive Java-Doc !!!
 */
public class SysDumpUtils {

    public static String dumpSysPropsAsJson() {
        return valueAsJson(dumpSysProps(), 0);
    }

    public static String dumpSysPropsExAsJson() {
        return valueAsJson(dumpSysPropsEx(), 0);
    }

    public static String dumpEnvVarsAsJson() {
        return valueAsJson(dumpEnvVars(), 0);
    }

    public static String dumpEnvVarsExAsJson() {
        return valueAsJson(dumpEnvVarsEx(), 0);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @return Java system-properties as a sorted-map
     */
    public static NavigableMap<String,String> dumpSysProps() {
        return sortedMap(System.getProperties().entrySet().stream().map(toPropValue()));
    }

    /**
     * @return the same as {@link #dumpEnvVars()}, but the values of {@code xxx.path} properties are transformed to list
     */
    public static NavigableMap<String, Object> dumpSysPropsEx() {
        return dumpSysProps().entrySet().stream()
            .collect(toSortedMap(e -> transformSysProps(e)));
    }

    /**
     * @return environment variables as a sorted-map
     */
    public static NavigableMap<String,String> dumpEnvVars() {
        return new TreeMap<>(System.getenv());
    }

    /**
     * @return the same as {@link #dumpEnvVars()}, but the values of {@code xxxPATH} variables are transformed to list
     */
    public static NavigableMap<String, Object> dumpEnvVarsEx() {
        return dumpEnvVars().entrySet().stream()
            .collect(toSortedMap(e -> transformPathVars(e)));
    }

    // --------------------------------------------------------------------------------------------

    private static Object transformSysProps(Map.Entry<String, ?> entry) {
        if (!entry.getKey().toUpperCase().endsWith("PATH")) {
            return entry.getValue();
        }
        String pathValue = "" + entry.getValue();
        return Arrays.stream(pathValue.split(File.pathSeparator)).toList();
    }

    private static Object transformPathVars(Map.Entry<String, String> entry) {
        if (!entry.getKey().toUpperCase().endsWith("PATH")) {
            return entry.getValue();
        } else {
            return Arrays.stream(entry.getValue().split(File.pathSeparator)).toList();
        }
    }

    // --------------------------------------------------------------------------------------------

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

    private static final int INDENT_JSON_PRETTY_PRINT = 2;
}
