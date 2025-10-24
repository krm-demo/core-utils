package org.krmdemo.techlabs.core.dump;

import org.krmdemo.techlabs.core.dump.render.RenderSpec;

import java.io.File;

/**
 * Utility-class that is a static facade of {@link ObjectPrinter.ToFile}
 */
public class ToFileUtils {

    /**
     * @param file the target file to save into
     * @param objToSave object to save the JSON-representation in text-format
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     */
    public static void saveAsJsonTxt(File file, Object objToSave, RenderSpec renderSpec) {
        // @start region="saveAsJsonTxt"
        ObjectPrinter.ToFile printer = new ObjectPrinter.ToFile(file);
        printer.printAsJsonTxt(objToSave, renderSpec);  // @highlight substring="printAsJsonTxt"
        // @end
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the JSON-representation in HTML-format
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     */
    public static void saveAsJsonHtml(File file, Object objToSave, RenderSpec renderSpec) {
        ObjectPrinter.ToFile printer = new ObjectPrinter.ToFile(file);
        printer.printAsJsonHtml(objToSave, renderSpec);
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the JSON-representation in SVG-format
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles JSON-elements
     */
    public static void saveAsJsonSvg(File file, Object objToSave, RenderSpec renderSpec) {
        ObjectPrinter.ToFile printer = new ObjectPrinter.ToFile(file);
        printer.printAsJsonSvg(objToSave, renderSpec);
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the YAML-representation in text-format
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles YAML-elements
     */
    public static void saveAsYamlTxt(File file, Object objToSave, RenderSpec renderSpec) {
        ObjectPrinter.ToFile printer = new ObjectPrinter.ToFile(file);
        printer.printAsYamlTxt(objToSave, renderSpec);
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the YAML-representation in HTML-format
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles YAML-elements
     */
    public static void saveAsYamlHtml(File file, Object objToSave, RenderSpec renderSpec) {
        ObjectPrinter.ToFile printer = new ObjectPrinter.ToFile(file);
        printer.printAsYamlHtml(objToSave, renderSpec);
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the YAML-representation in SVG-format
     * @param renderSpec an instance of {@link RenderSpec} to customize some styles YAML-elements
     */
    public static void saveAsYamlSvg(File file, Object objToSave, RenderSpec renderSpec) {
        ObjectPrinter.ToFile printer = new ObjectPrinter.ToFile(file);
        printer.printAsYamlSvg(objToSave, renderSpec);
    }

    // --------------------------------------------------------------------------------------------

    /**
     * @param file the target file to save into
     * @param objToSave object to save the JSON-representation in text-format with default highlighting
     */
    public static void saveAsJsonTxt(File file, Object objToSave) {
        ObjectPrinter.ToFile printer = new ObjectPrinter.ToFile(file);
        printer.printAsJsonTxt(objToSave);
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the JSON-representation in HTML-format with default highlighting
     */
    public static void saveAsJsonHtml(File file, Object objToSave) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonHtml(objToSave);
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the JSON-representation in SVG-format with default highlighting
     */
    public static void saveAsJsonSvg(File file, Object objToSave) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsJsonSvg(objToSave);
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the YAML-representation in text-format with default highlighting
     */
    public static void saveAsYamlTxt(File file, Object objToSave) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlTxt(objToSave);
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the YAML-representation in HTML-format with default highlighting
     */
    public static void saveAsYamlHtml(File file, Object objToSave) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlHtml(objToSave);
    }

    /**
     * @param file the target file to save into
     * @param objToSave object to save the YAML-representation in SVG-format with default highlighting
     */
    public static void saveAsYamlSvg(File file, Object objToSave) {
        ObjectPrinter.ToString printer = new ObjectPrinter.ToString();
        printer.printAsYamlSvg(objToSave);
    }
}
