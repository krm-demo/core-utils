package org.krmdemo.techlabs.sysdump;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.nameValue;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedMap;

/**
 * TODO: provide the comprehensive Java-Doc !!!
 */
public class PropertiesUtils {

    public static NavigableMap<String, String> propsMapFromFile(File propsFile) {
        return sortedMap(propEntriesFromFile(propsFile));
    }

    public static NavigableMap<String, String> propsMapResource(String propsResourcePath) {
        return sortedMap(propEntriesResource(propsResourcePath));
    }

    public static NavigableMap<String, String> propsMapFromURL(URL propsURL) {
        return sortedMap(propEntriesFromURL(propsURL));
    }

    // --------------------------------------------------------------------------------------------

    public static Stream<Map.Entry<String, String>> propEntriesFromFile(File propsFile) {
        try (InputStream propsInputStream = new FileInputStream(propsFile)) {
            Properties props = new Properties();
            props.load(propsInputStream);
            return props.entrySet().stream().map(PropertiesUtils::propEntry);
        } catch (IOException ex) {
            return Stream.empty();
        }
    }

    public static Stream<Map.Entry<String, String>> propEntriesResource(String propsResourcePath) {
        try {
            return propEntriesFromURL(IOUtils.resourceToURL(propsResourcePath));
        } catch (IOException ex) {
            return Stream.empty();
        }
    }

    public static Stream<Map.Entry<String, String>> propEntriesFromURL(URL propsURL) {
        try (InputStream propsInputStream = propsURL.openStream()) {
            Properties props = new Properties();
            props.load(propsInputStream);
            return props.entrySet().stream().map(PropertiesUtils::propEntry);
        } catch (IOException ex) {
            return Stream.empty();
        }
    }

    // --------------------------------------------------------------------------------------------

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

    // --------------------------------------------------------------------------------------------

    private PropertiesUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
