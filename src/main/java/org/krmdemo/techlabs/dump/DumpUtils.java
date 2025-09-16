package org.krmdemo.techlabs.dump;

import org.krmdemo.techlabs.dump.render.Highlighter;

/**
 * Utility-class that is a static facade of {@link ObjectPrinter.ToString},
 * where the serializing of an object is provided with {@link JacksonTree}.
 * <hr/>
 * Set of methods with the second {@link Highlighter} arguments
 * allow to suppress the default highlighting by providing {@link Highlighter#NONE}.  
 */
public class DumpUtils {

    /**
     * @param objToDump object to dump
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     * @return JSON-representation of an object in text-format as {@link String}
     */
    public static String dumpAsJsonTxt(Object objToDump, Highlighter highlighter) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonTxt(objToDump, highlighter);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     * @return JSON-representation of an object in HTML-format as {@link String}
     */
    public static String dumpAsJsonHtml(Object objToDump, Highlighter highlighter) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonHtml(objToDump, highlighter);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     * @return JSON-representation of an object in SVG-format as {@link String}
     */
    public static String dumpAsJsonSvg(Object objToDump, Highlighter highlighter) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonSvg(objToDump, highlighter);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     * @return YAML-representation of an object in text-format as {@link String}
     */
    public static String dumpAsYamlTxt(Object objToDump, Highlighter highlighter) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlTxt(objToDump, highlighter);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     * @return YAML-representation of an object in HTML-format as {@link String}
     */
    public static String dumpAsYamlHtml(Object objToDump, Highlighter highlighter) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlHtml(objToDump, highlighter);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     * @return YAML-representation of an object in SVG-format as {@link String}
     */
    public static String dumpAsYamlSvg(Object objToDump, Highlighter highlighter) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlSvg(objToDump, highlighter);
        return printer.toString();
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @param objToDump object to dump with default highlighting
     * @return JSON-representation of an object in text-format as {@link String}
     */
    public static String dumpAsJsonTxt(Object objToDump) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonTxt(objToDump);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump with default highlighting
     * @return JSON-representation of an object in HTML-format as {@link String}
     */
    public static String dumpAsJsonHtml(Object objToDump) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonHtml(objToDump);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump with default highlighting
     * @return JSON-representation of an object in SVG-format as {@link String}
     */
    public static String dumpAsJsonSvg(Object objToDump) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonSvg(objToDump);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump with default highlighting
     * @return YAML-representation of an object in text-format as {@link String}
     */
    public static String dumpAsYamlTxt(Object objToDump) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlTxt(objToDump);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump with default highlighting
     * @return YAML-representation of an object in HTML-format as {@link String}
     */
    public static String dumpAsYamlHtml(Object objToDump) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlHtml(objToDump);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump with default highlighting
     * @return YAML-representation of an object in SVG-format as {@link String}
     */
    public static String dumpAsYamlSvg(Object objToDump) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlSvg(objToDump);
        return printer.toString();
    }
}
