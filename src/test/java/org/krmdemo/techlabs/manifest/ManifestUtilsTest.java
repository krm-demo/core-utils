package org.krmdemo.techlabs.manifest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Objects;
import java.util.jar.Manifest;

import static org.junit.jupiter.api.Assertions.*;
import static org.krmdemo.techlabs.manifest.ManifestUtils.manifestForClass;

/**
 * Unit-test for {@link ManifestUtils}
 */
class ManifestUtilsTest {

    @Test
    void testManifestForJackson() {
        Manifest manifest = manifestForClass(ObjectMapper.class);
        System.out.println("Jackson-Manifest --> " + dumpAsJsonPrettyPrint(manifest));
        System.out.println("Jackson-Manifest.entries --> " + dumpAsJsonPrettyPrint(manifest.getEntries()));
    }

    @Test
    void testManifestForCoreUtils() {
        Manifest manifest = manifestForClass(ManifestUtils.class);
        System.out.println("'core-utils'-Manifest --> " + dumpAsJsonPrettyPrint(manifest));
        System.out.println("'core-utils'-Manifest.entries --> " + dumpAsJsonPrettyPrint(manifest.getEntries()));
    }

    private static final PrettyPrinter PRETTY_PRINTER = prettyPrinter();
    private static DefaultPrettyPrinter prettyPrinter() {
        DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();
        defaultPrettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        return defaultPrettyPrinter;
    }

    private static final ObjectMapper OBJECT_MAPPER_PRETTY_PRINT = objectMapperPrettyPrint();
    private static ObjectMapper objectMapperPrettyPrint() {
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);
            //.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
        objectMapper.configOverride(Map.class).setInclude(
            JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, JsonInclude.Include.NON_EMPTY));
        return objectMapper;
    }

    private static String dumpAsJsonPrettyPrint(Object value) {
        try {
            return OBJECT_MAPPER_PRETTY_PRINT.writer(PRETTY_PRINTER).writeValueAsString(value);
        } catch (JsonProcessingException jsonEx) {
            throw new IllegalArgumentException(String.format(
                "could not dump the object '%s' as pretty-print JSON",
                Objects.toIdentityString(value)), jsonEx);
        }
    }
}