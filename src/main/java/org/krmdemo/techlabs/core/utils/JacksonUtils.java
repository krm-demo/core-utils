package org.krmdemo.techlabs.core.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
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
 * <hr/>
 * TODO: git rid of this class or move it to proper place !!!
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
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static JsonNode jsonTreeFromString(String jsonContent) {
        try {
            return OBJECT_MAPPER_DUMP.readTree(jsonContent);
        } catch (JsonProcessingException jsonEx) {
            throw new IllegalArgumentException(String.format(
                "could not read the JSON-Tree:%n---%n'%s'%n---%n", jsonContent), jsonEx);
        }
    }

    public static <T> T jsonValueFromString(String jsonContent, TypeReference<T> typeRef) {
        try {
            return OBJECT_MAPPER_DUMP.readValue(jsonContent, typeRef);
        } catch (JsonProcessingException jsonEx) {
            throw new IllegalArgumentException(String.format(
                "could not read by type-ref <%s>:%n---%n'%s'%n---%n", typeRef, jsonContent), jsonEx);
        }
    }

    public static <T> T jsonValueFromString(String jsonContent, JavaType javaType) {
        try {
            return OBJECT_MAPPER_DUMP.readValue(jsonContent, javaType);
        } catch (JsonProcessingException jsonEx) {
            throw new IllegalArgumentException(String.format(
                "could not read by java-type <%s>:%n---%n'%s'%n---%n", javaType, jsonContent), jsonEx);
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
            throw new IllegalArgumentException(String.format(
                "could not load the JSON-Tree from JSON-resource by path '%s'", resourcePath), ioEx);
        }
    }

    public static <T> T jsonValueFromResource(String resourcePath, Class<T> classValue) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = classLoader.getResourceAsStream(resourcePath)) {
            return OBJECT_MAPPER_DUMP.readValue(resourceStream, classValue);
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(String.format(
                "could not load the value of class <%s> from resource by path '%s'",
                classValue, resourcePath), ioEx);
        }
    }

    public static <T> T jsonValueFromResource(String resourcePath, TypeReference<T> typeRef) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = classLoader.getResourceAsStream(resourcePath)) {
            return OBJECT_MAPPER_DUMP.readValue(resourceStream, typeRef);
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(String.format(
                "could not load the value of type <%s> from resource by path '%s'",
                typeRef, resourcePath), ioEx);
        }
    }

    public static <T> T jsonValueFromResource(String resourcePath, JavaType javaType) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = classLoader.getResourceAsStream(resourcePath)) {
            return OBJECT_MAPPER_DUMP.readValue(resourceStream, javaType);
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(String.format(
                "could not load the value of java-type <%s> from resource by path '%s'",
                javaType, resourcePath), ioEx);
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
     * @return de-serialized JSON-Array as {@link List}
     */
    public static List<Object> jsonArrFromResource(String resourcePath) {
        // automatic casting to "java.util.List" looks like a magic
        return jsonValueFromResource(resourcePath, new TypeReference<>(){});
    }

    /**
     * Loading the JSON-Array from resource with path {@code resourcePath} as {@code List<T>},
     * where the type of elements is determined by passed {@code itemClass}.
     *
     * @param resourcePath the path to JSON-resource within the current class-path (no leading slash !!!)
     * @param itemClass the class of elements in returning list
     * @return de-serialized JSON-Array as {@link List}
     * @param <T> type of elements in returning list
     */
    public static <T> List<T> jsonArrFromResource(String resourcePath, Class<T> itemClass) {
        JavaType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, itemClass);
        return jsonValueFromResource(resourcePath, listType);
    }

    /**
     * Loading the JSON-Array from resource with path {@code resourcePath} as {@code List<T>},
     * where the type of elements is determined by passed java-type {@code itemType}.
     * <hr/>
     * This method is similar to {@link #jsonArrFromResource(String, Class)}
     * but it's mostly for dynamic cases, where no reference to concrete {@link Class}.
     *
     * @param resourcePath the path to JSON-resource within the current class-path (no leading slash !!!)
     * @param itemType the java-type of elements in returning list
     * @return de-serialized JSON-Array as {@link List}
     * @param <T> type of elements in returning list
     */
    public static <T> List<T> jsonArrFromResource(String resourcePath, JavaType itemType) {
        JavaType listType = TypeFactory.defaultInstance().constructCollectionType(List.class, itemType);
        return jsonValueFromResource(resourcePath, listType);
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

    private JacksonUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
