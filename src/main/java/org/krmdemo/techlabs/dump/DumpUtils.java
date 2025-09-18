package org.krmdemo.techlabs.dump;

import org.krmdemo.techlabs.dump.render.RenderSpec;

/**
 * Utility-class that is a static facade of {@link ObjectPrinter.ToString},
 * where the serializing of an object is provided with {@link JacksonTree}.
 * <hr/>
 * Set of methods with the second {@link RenderSpec} arguments
 * allow to suppress the default highlighting by providing {@code new RenderSpec(Highlight.HONE, ...)}.
 */
public class DumpUtils {

    /**
     * @param objToDump object to dump
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     * @return JSON-representation of an object in text-format as {@link String}
     */
    public static String dumpAsJsonTxt(Object objToDump, RenderSpec renderSpec) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonTxt(objToDump, renderSpec);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     * @return JSON-representation of an object in HTML-format as {@link String}
     */
    public static String dumpAsJsonHtml(Object objToDump, RenderSpec renderSpec) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonHtml(objToDump, renderSpec);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     * @return JSON-representation of an object in SVG-format as {@link String}
     */
    public static String dumpAsJsonSvg(Object objToDump, RenderSpec renderSpec) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonSvg(objToDump, renderSpec);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles YAML-elements
     * @return YAML-representation of an object in text-format as {@link String}
     */
    public static String dumpAsYamlTxt(Object objToDump, RenderSpec renderSpec) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlTxt(objToDump, renderSpec);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles YAML-elements
     * @return YAML-representation of an object in HTML-format as {@link String}
     */
    public static String dumpAsYamlHtml(Object objToDump, RenderSpec renderSpec) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlHtml(objToDump, renderSpec);
        return printer.toString();
    }

    /**
     * @param objToDump object to dump
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles YAML-elements
     * @return YAML-representation of an object in SVG-format as {@link String}
     */
    public static String dumpAsYamlSvg(Object objToDump, RenderSpec renderSpec) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlSvg(objToDump, renderSpec);
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
