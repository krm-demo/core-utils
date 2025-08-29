package org.krmdemo.techlabs.sysdump;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.krmdemo.techlabs.stream.TechlabsStreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.nameValue;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedMap;

/**
 * TODO: provide the comprehensive Java-Doc !!!
 */
public class PropertiesUtils {

    public static Map.Entry<String, String> propEntryEsc(Map.Entry<?,?> entry) {
        return propEntryEsc(entry.getKey(), entry.getValue());
    }

    public static Map.Entry<String, String> propEntryEsc(Object propName, Object propValue) {
        return nameValue(
            Objects.toString(propName),
            StringEscapeUtils.escapeJava(Objects.toString(propValue))
        );
    }

    public static Map.Entry<String, String> propEntry(Map.Entry<?,?> entry) {
        return propEntry(entry.getKey(), entry.getValue());
    }

    public static Map.Entry<String, String> propEntry(Object propName, Object propValue) {
        return nameValue(
            Objects.toString(propName),
            Objects.toString(propValue)
        );
    }

    public static NavigableMap<String, String> propsMapResource(String propsResourcePath) {
        return sortedMap(propsEntriesResource(propsResourcePath));
    }

    public static Stream<Map.Entry<String, String>> propsEntriesResource(String propsResourcePath) {
        try {
            return propsEntries(IOUtils.resourceToURL(propsResourcePath));
        } catch (IOException ex) {
            return Stream.empty();
        }
    }

    public static Stream<Map.Entry<String, String>> propsEntries(URL propsURL) {
        try (InputStream propsInputStream = propsURL.openStream()) {
            Properties props = new Properties();
            props.load(propsInputStream);
            return props.entrySet().stream().map(PropertiesUtils::propEntry);
        } catch (IOException ex) {
            return Stream.empty();
        }
    }

    public static Stream<Map.Entry<String, String>> propsEntries(File propsFile) {
        try (InputStream propsInputStream = new FileInputStream(propsFile)) {
            Properties props = new Properties();
            props.load(propsInputStream);
            return props.entrySet().stream().map(PropertiesUtils::propEntry);
        } catch (IOException ex) {
            return Stream.empty();
        }
    }

    public static Stream<Map.Entry<String, String>> propsEntries(Path propsPath) {
        return propsEntries(propsPath.toFile());
    }

    private PropertiesUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
