package org.krmdemo.techlabs.core.utils;

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

import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedMap;

/**
 * Utility-class that simplifies the access to environment-variables, Java system-properties
 * and the content of any other properties-files at file-system or in classpath.
 */
public class PropertiesUtils {

    /**
     * @param propsFile the properties-file to load from file-system
     * @return the loaded properties as {@link NavigableMap sorted-map}
     */
    public static NavigableMap<String, String> propsMapFromFile(File propsFile) {
        return sortedMap(propEntriesFromFile(propsFile));
    }

    /**
     * @param propsResourcePath the path to properties-file in classpath
     * @return the loaded properties as {@link NavigableMap sorted-map}
     */
    public static NavigableMap<String, String> propsMapResource(String propsResourcePath) {
        return sortedMap(propEntriesResource(propsResourcePath));
    }

    /**
     * @param propsURL {@link URL} to properties-file
     * @return the loaded properties as {@link NavigableMap sorted-map}
     */
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

    /**
     * The same as {@link #propEntry(Map.Entry)}, but the values of properties
     * are escaped with {@link StringEscapeUtils#escapeJava(String)}, which is very useful
     * and convenient when the content of the result map is going to be represented as JSON or YAML,
     * but some values like {@link System#lineSeparator()} requires to be escaped.
     *
     * @param entry the entry of type {@link Map.Entry Map.Entry&lt;Object,Object&gt;}
     * @return an instance of type {@link Map.Entry Map.Entry&lt;String,String&gt;}
     */
    public static Map.Entry<String, String> propEntryEsc(Map.Entry<?,?> entry) {
        return propEntryEsc(entry.getKey(), entry.getValue());
    }

    /**
     * The same as {@link #propEntry(Object, Object)}, but the values of properties
     * are escaped with {@link StringEscapeUtils#escapeJava(String)}, which is very useful
     * and convenient when the content of the result map is going to be represented as JSON or YAML,
     * but some values like {@link System#lineSeparator()} requires to be escaped.
     *
     * @param propName name of property as {@link Object} (the legacy of no generics in {@link Properties}-class)
     * @param propValue value of property as {@link Object} (the legacy of no generics in {@link Properties}-class)
     * @return an instance of type {@link Map.Entry Map.Entry&lt;String,String&gt;}
     */
    public static Map.Entry<String, String> propEntryEsc(Object propName, Object propValue) {
        return nameValue(
            Objects.toString(propName),
            StringEscapeUtils.escapeJava(Objects.toString(propValue))
        );
    }

    /**
     * Transforms the entry of type {@link Map.Entry Map.Entry&lt;Object,Object&gt;}
     * into the entry of type {@link Map.Entry Map.Entry&lt;String,String&gt;}
     * with use of standard transformation via {@link Objects#toString(Object)}
     *
     * @param entry the entry of type {@link Map.Entry Map.Entry&lt;Object,Object&gt;}
     * @return the entry of type {@link Map.Entry Map.Entry&lt;String,String&gt;}
     */
    public static Map.Entry<String, String> propEntry(Map.Entry<?,?> entry) {
        return propEntry(entry.getKey(), entry.getValue());
    }

    /**
     * A factory-method produce an instance of type {@link Map.Entry Map.Entry&lt;String,String&gt;}
     *
     * @param propName name of property as {@link Object} (the legacy of no generics in {@link Properties}-class)
     * @param propValue value of property as {@link Object} (the legacy of no generics in {@link Properties}-class)
     * @return an instance of type {@link Map.Entry Map.Entry&lt;String,String&gt;}
     */
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
