package org.krmdemo.techlabs.dump;

/**
 * Utility-class that is a static facade of {@link ObjectPrinter#DEFAULT_STD_OUT},
 * where the serializing of an object is provided with {@link JacksonTree}.
 * <hr/>
 * Set of methods with the second {@link Highlighter} arguments  
 * allow to suppress the default highlighting by providing {@link Highlighter#NONE}.  
 */
public class PrintUtils {

    /**
     * @param objToPrint object to print into standard output
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     */
    public static void printAsJsonTxt(Object objToPrint, Highlighter highlighter) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsJsonTxt(objToPrint, highlighter);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     */
    public static void printAsJsonHtml(Object objToPrint, Highlighter highlighter) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsJsonHtml(objToPrint, highlighter);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     */
    public static void printAsJsonSvg(Object objToPrint, Highlighter highlighter) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsJsonSvg(objToPrint, highlighter);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     */
    public static void printAsYamlTxt(Object objToPrint, Highlighter highlighter) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsYamlTxt(objToPrint, highlighter);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     */
    public static void printAsYamlHtml(Object objToPrint, Highlighter highlighter) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsYamlHtml(objToPrint, highlighter);
    }

    /**
     * @param objToPrint object to print into standard output
     * @param highlighter an instance of {@link Highlighter} to customize some styles JSON-elements
     */
    public static void printAsYamlSvg(Object objToPrint, Highlighter highlighter) {
        ObjectPrinter.DEFAULT_STD_OUT.printAsYamlSvg(objToPrint, highlighter);
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
