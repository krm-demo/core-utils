package org.krmdemo.techlabs.sysdump;

import org.krmdemo.techlabs.json.JacksonUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJson;
import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;
import static org.krmdemo.techlabs.stream.CoreCollectors.toSortedMap;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.nameValue;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.sortedMap;

/**
 * TODO: provide the comprehensive Java-Doc and reuse {@link org.krmdemo.techlabs.dump.ObjectPrinter} !!!
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
        return sortedMap(System.getProperties().entrySet()
            .stream()
            .map(PropertiesUtils::propEntryEsc));
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

    /**
     * @param file a file to get the file-attributes of
     * @return the attributes of {@code file} as JSON
     */
    public static String fileAttrsAsJson(File file) {
        return fileAttrsAsJson(file.toPath());
    }

    /**
     * @param filePath a path to the file to get the file-attributes of
     * @return the attributes of {@code file} as JSON
     */
    public static String fileAttrsAsJson(Path filePath) {
        try {
            BasicFileAttributes fileAtrrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            return dumpAsJsonPrettyPrint(linkedMap(
                nameValue("filePath", filePath),
                nameValue("fileSize", fileAtrrs.size()),
                nameValue("creationTime", fileAtrrs.creationTime()),
                nameValue("lastModifiedTime", fileAtrrs.lastAccessTime()),
                nameValue("lastModifiedTime", fileAtrrs.lastModifiedTime()),
                nameValue("isRegular", fileAtrrs.isRegularFile()),
                nameValue("isDirectory", fileAtrrs.isDirectory()),
                nameValue("isSymbolicLink", fileAtrrs.isSymbolicLink()),
                nameValue("isOther", fileAtrrs.isOther())
            ));
        } catch (Exception ex) {
            return dumpAsJson(JacksonUtils.errorFrom(ex));
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

    private SysDumpUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
