package org.krmdemo.techlabs.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Utility class to serialize / de-serialize JSON-Objects and JSON-Arrays
 * using <a href="https://github.com/FasterXML/jackson">Jackson Library</a>
 *
 * @see <a href="https://github.com/FasterXML/jackson-docs">
 *     Jackson Library Documentation
 * </a>
 */
@Slf4j
public class JacksonUtils {

    @Getter
    @ToString
    @JsonInclude(Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnyError {
        private final String exceptionClass;
        private final String message;
        private final List<String> stackTrace;
        public AnyError(@NonNull Exception ex) {
            this.exceptionClass = ex.getClass().getName();
            this.message = ex.getMessage();
            this.stackTrace = ExceptionUtils.getRootCauseStackTraceList(ex);
        }
    }

    @Getter @ToString
    @JsonInclude(Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JsonError extends AnyError {
        private final String originalMessage;
        private final String location;
        public JsonError(JsonProcessingException jsonEx) {
            super(jsonEx);
            this.originalMessage = jsonEx.getOriginalMessage();
            this.location = jsonEx.getLocation() == null ? "" : jsonEx.getLocation().toString();
        }
    }

    public static AnyError errorFrom(Exception ex) { return new AnyError(ex); }
    public static JsonError errorFrom(JsonProcessingException jsonEx) { return new JsonError(jsonEx); }

    private static final ObjectMapper OBJECT_MAPPER_DUMP =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static JsonNode jsonTreeFromString(String jsonContent) {
        try {
            return OBJECT_MAPPER_DUMP.readTree(jsonContent);
        } catch (JsonProcessingException jsonEx) {
            throw new IllegalArgumentException(
                String.format("could not read the JSON-Tree:%n---%n'%s'%n---%n", jsonContent), jsonEx);
        }
    }

    public static <T> T jsonValueFromString(String jsonContent, TypeReference<T> typeRef) {
        try {
            return OBJECT_MAPPER_DUMP.readValue(jsonContent, typeRef);
        } catch (JsonProcessingException jsonEx) {
            throw new IllegalArgumentException(
                String.format("could not read <%s>:%n---%n'%s'%n---%n", typeRef, jsonContent), jsonEx);
        }
    }

    public static Map<String, Object> jsonObjFromString(String jsonContent) {
        // automatic casting to "java.util.Map" looks like a magic
        return jsonValueFromString(jsonContent, new TypeReference<>(){});
    }

    public static List<Object> jsonArrFromString(String jsonContent) {
        // automatic casting to "java.util.List" looks like a magic
        return jsonValueFromString(jsonContent, new TypeReference<>(){});
    }

    public static JsonNode jsonTreeFromResource(String resourcePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = classLoader.getResourceAsStream(resourcePath)) {
            return OBJECT_MAPPER_DUMP.readTree(resourceStream);
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(
                String.format("could not load the JSON-Tree from JSON-resource by path '%s'", resourcePath), ioEx);
        }
    }

    public static <T> T jsonValueFromResource(String resourcePath, TypeReference<T> typeRef) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = classLoader.getResourceAsStream(resourcePath)) {
            return OBJECT_MAPPER_DUMP.readValue(resourceStream, typeRef);
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(
                String.format("could not load <%s> from resource by path '%s'", typeRef, resourcePath), ioEx);
        }
    }

    /**
     * @param resourcePath the path to JSON-resource within the current class-path (no leading slash !!!)
     * @return de-serialized JSON-Object as {@link Map}
     */
    public static Map<String, Object> jsonObjFromResource(String resourcePath) {
        // automatic casting to "java.util.Map" looks like a magic
        return jsonValueFromResource(resourcePath, new TypeReference<>(){});
    }

    /**
     * @param resourcePath the path to JSON-resource within the current class-path (no leading slash !!!)
     * @return de-serialized JSON-Object as {@link List}
     */
    public static List<Object> jsonArrFromResource(String resourcePath) {
        // automatic casting to "java.util.List" looks like a magic
        return jsonValueFromResource(resourcePath, new TypeReference<>(){});
    }

    /**
     * @param jsonFile a file that contains JSON-value
     * @return the root node of de-serialized JSON-Tree
     */
    public static JsonNode jsonTreeFromFile(File jsonFile) {
        try (FileInputStream resourceStream = new FileInputStream(jsonFile)) {
            return OBJECT_MAPPER_DUMP.readTree(resourceStream);
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(
                String.format("could not load the JSON-Tree from JSON-file '%s'", jsonFile), ioEx);
        }
    }

    /**
     * @param jsonFile a file that contains JSON-value
     * @param typeRef a type-reference to cast the de-serialized object to
     * @return de-serialized JSON-vlaue of type <code>T</code>
     * @param <T> the type to de-serialize the JSON-value to
     */
    public static <T> T jsonValueFromFile(File jsonFile, TypeReference<T> typeRef) {
        try (FileInputStream resourceStream = new FileInputStream(jsonFile)) {
            return OBJECT_MAPPER_DUMP.readValue(resourceStream, typeRef);
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(
                String.format("could not load <%s> from JSON-file '%s'", typeRef, jsonFile), ioEx);
        }
    }

    /**
     * @param jsonFile a file that contains JSON-Object
     * @return de-serialized JSON-Object as {@link Map}
     */
    public static Map<String, Object> jsonObjFromFile(File jsonFile) {
        // automatic casting to "java.util.Map" looks like a magic
        return jsonValueFromFile(jsonFile, new TypeReference<>(){});
    }

    /**
     * @param jsonFile a file that contains JSON-Array
     * @return de-serialized JSON-Object as {@link List}
     */
    public static List<Object> jsonArrFromFile(File jsonFile) {
        // automatic casting to "java.util.List" looks like a magic
        return jsonValueFromFile(jsonFile, new TypeReference<>(){});
    }

    /**
     * @param value the object to dump
     * @return serialized object in JSON-format (the default, NOT pretty-print)
     */
    public static String dumpAsJson(Object value) {
        try {
            return OBJECT_MAPPER_DUMP.writeValueAsString(value);
        } catch (JsonProcessingException jsonEx) {
            log.error("could not dump the value as JSON", jsonEx);
            return dumpAsJson(errorFrom(jsonEx));
        }
    }

    private static final PrettyPrinter PRETTY_PRINTER = prettyPrinter();
    private static DefaultPrettyPrinter prettyPrinter() {
        DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();
        defaultPrettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE); // <-- 2 spaces
        return defaultPrettyPrinter;
    }

    private static final ObjectMapper OBJECT_MAPPER_PRETTY_PRINT = objectMapperPrettyPrint();
    private static ObjectMapper objectMapperPrettyPrint() {
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        objectMapper.configOverride(Map.class).setInclude(
            JsonInclude.Value.construct(Include.NON_EMPTY, Include.NON_EMPTY));
        return objectMapper;
    }

    /**
     * @param value the object to dump in a pretty-print way
     * @return serialized object in JSON-format (pretty-print - the keys are sorted and arrays are indented)
     */
    public static String dumpAsJsonPrettyPrint(Object value) {
        try {
            return OBJECT_MAPPER_PRETTY_PRINT.writer(PRETTY_PRINTER).writeValueAsString(value);
        } catch (JsonProcessingException jsonEx) {
            log.error("could not dump the value as pretty-print JSON", jsonEx);
            return dumpAsJson(errorFrom(jsonEx));
        }
    }

    /**
     * Prettify the passed un-formatted JSON
     *
     * @param jsonToPrettify JSON to prettify
     * @return properly formatted JSON (with indentation of 2 spaces)
     */
    public static String prettifyJson(String jsonToPrettify) {
        if (isBlank(jsonToPrettify)) {
            log.warn("attempt to pretty-print the blank string - return the empty string");
            return "";
        }
        JsonNode jsonTree = jsonTreeFromString(jsonToPrettify);
        return dumpAsJsonPrettyPrint(jsonTree);
    }

    private JacksonUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
