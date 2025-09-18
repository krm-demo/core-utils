package org.krmdemo.techlabs.dump;

import org.krmdemo.techlabs.dump.render.RenderSpec;

/**
 * Utility-class that is a static facade of {@link ObjectPrinter#DEFAULT_STD_OUT},
 * where the serializing of an object is provided with {@link JacksonTree}.
 * <hr/>
 * Set of methods with the second {@link RenderSpec} arguments
 * allow to suppress the default highlighting by providing {@code new RenderSpec(Highlight.HONE, ...)}.
 */
public class PrintUtils {

    /**
     * @param objToPrint object to print into standard output
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     */
    public static void printAsJsonTxt(Object objToPrint, RenderSpec renderSpec) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsJsonTxt(objToPrint, renderSpec);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     */
    public static void printAsJsonHtml(Object objToPrint, RenderSpec renderSpec) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsJsonHtml(objToPrint, renderSpec);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     */
    public static void printAsJsonSvg(Object objToPrint, RenderSpec renderSpec) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsJsonSvg(objToPrint, renderSpec);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles YAML-elements
     */
    public static void printAsYamlTxt(Object objToPrint, RenderSpec renderSpec) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsYamlTxt(objToPrint, renderSpec);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles YAML-elements
     */
    public static void printAsYamlHtml(Object objToPrint, RenderSpec renderSpec) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsYamlHtml(objToPrint, renderSpec);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles YAML-elements
     */
    public static void printAsYamlSvg(Object objToPrint, RenderSpec renderSpec) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsYamlSvg(objToPrint, renderSpec);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @param objToPrint object to print into standard output with default highlighting
     */
    public static void printAsJsonTxt(Object objToPrint) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsJsonTxt(objToPrint);
    }

    /**
     * @param objToPrint object to print into standard output with default highlighting
     */
    public static void printAsJsonHtml(Object objToPrint) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsJsonHtml(objToPrint);
    }

    /**
     * @param objToPrint object to print into standard output with default highlighting
     */
    public static void printAsJsonSvg(Object objToPrint) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsJsonSvg(objToPrint);
    }

    /**
     * @param objToPrint object to print into standard output with default highlighting
     */
    public static void printAsYamlTxt(Object objToPrint) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsYamlTxt(objToPrint);
    }

    /**
     * @param objToPrint object to print into standard output with default highlighting
     */
    public static void printAsYamlHtml(Object objToPrint) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsYamlHtml(objToPrint);
    }

    /**
     * @param objToPrint object to print into standard output with default highlighting
     */
    public static void printAsYamlSvg(Object objToPrint) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsYamlSvg(objToPrint);
    }
}
