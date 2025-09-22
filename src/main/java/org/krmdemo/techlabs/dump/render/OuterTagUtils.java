package org.krmdemo.techlabs.dump.render;

import java.util.Base64;

/**
 * Utility-class to wrap inner-HTML or inner-SVG with outer HTML-tags
 */
public class OuterTagUtils {

    public static String outerDivStyle(String innerHtml, String outerStyle) {
        return String.format("""
            <div style="font-family: monospace; font-size: 14px; %s">
            %s</div>""",
            outerStyle,
            innerHtml.indent(2)
        );
    }

    public static String outerDivCss(String innerHtml, String outerClassName) {
        return String.format("""
            <div style="font-family: monospace; font-size: 14px;" class="%s">
            %s</div>""",
            outerClassName,
            innerHtml.indent(2)
        );
    }

    // --------------------------------------------------------------------------------------------

    public static int totalWidthPX(int fontSizePX, int maxRowLen) {
        return roundCeil(fontSizePX * 0.61 * maxRowLen + 4);
    }

    public static int totalHeightPX(int fontSizePX, int rowsCount) {
        return roundCeil(fontSizePX * 1.2 * (rowsCount + 0.2) + 2);
    }

    public static int roundCeil(double valueDouble) {
        return Math.toIntExact(Math.round(Math.ceil(valueDouble)));
    }

    public static String outerSvgAttrs(
            String innerSvg,
            int fontSizePX, int maxRowLen, int rowsCount,
            String outerStyle) {
        return String.format("""
                <svg xmlns="http://www.w3.org/2000/svg"
                     style="%s"
                     width="%dpx"
                     height="%dpx">
                <text font-family="monospace" font-size="%dpx" style="white-space: pre;">
                %s</text>
                </svg>""",
            outerStyle,
            totalWidthPX(fontSizePX, maxRowLen),
            totalHeightPX(fontSizePX, rowsCount),
            fontSizePX,
            innerSvg.indent(2)
        );
    }

    public static String outerSvgClass(
            String innerSvg,
            int fontSizePX, int maxRowLen, int rowsCount,
            String outerClassName) {
        return String.format("""
                <svg xmlns="http://www.w3.org/2000/svg"
                     class="%s"
                     width="%dpx"
                     height="%dpx">
                <text font-family="monospace" font-size="%dpx" style="white-space: pre;">
                %s</text>
                </svg>""",
            outerClassName,
            totalWidthPX(fontSizePX, maxRowLen),
            totalHeightPX(fontSizePX, rowsCount),
            fontSizePX,
            innerSvg.indent(2)
        );
    }

    // --------------------------------------------------------------------------------------------

    public static String outerImgSvgAttrs(
            String innerSvg,
            int fontSizePX, int maxRowLen, int rowsCount,
            String outerStyle) {
        return embeddedSvgTag(outerSvgAttrs(innerSvg, fontSizePX, maxRowLen, rowsCount, outerStyle));
    }

    public static String outerImgSvgClass(
            String innerSvg,
            int fontSizePX, int maxRowLen, int rowsCount,
            String outerClassName) {
        return embeddedSvgTag(outerSvgClass(innerSvg, fontSizePX, maxRowLen, rowsCount, outerClassName));
    }

    public static String embeddedSvgTag(String outerSvgTag) {
        return String.format("""
            <img alt="embedded SVG-iamge" src="data:image/svg+xml;base64,%s" />""",
            encodeBase64(outerSvgTag)
        );
    }
    private static String encodeBase64(String outerSvgTag) {
        return new String(Base64.getEncoder().encode(outerSvgTag.getBytes()));
    }

    // --------------------------------------------------------------------------------------------

    public static String outerHtml(String outerTag, String title) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <title>%s</title>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1">
            </head>
            <body>
            %s
            </body>
            </html>""",
            title, outerTag);
    }

    public static String outerHtmlDivStyle(String innerHtml, String outerStyle, String title) {
        return outerHtml(outerDivStyle(innerHtml, outerStyle), title);
    }

    public static String outerHtmlDivCss(String innerHtml, String outerClassName, String title) {
        // TODO: there should be HTML-style section generated properly in HTML-head
        return outerHtml(outerDivCss(innerHtml, outerClassName), title);
    }

    public static String outerHtmlSvgAttrs(
            String innerSvg,
            int fontSizePX, int maxRowLen, int rowsCount,
            String outerStyle, String title) {
        return outerHtml(outerSvgAttrs(innerSvg, fontSizePX, maxRowLen, rowsCount, outerStyle), title);
    }

    public static String outerHtmlSvgClass(
            String innerSvg,
            int fontSizePX, int maxRowLen, int rowsCount,
            String outerClassName, String title) {
        // TODO: there should be HTML-style section generated properly in HTML-head
        return outerHtml(outerSvgClass(innerSvg, fontSizePX, maxRowLen, rowsCount, outerClassName), title);
    }

    public static String outerHtmlImgSvgAttrs(
        String innerSvg,
        int fontSizePX, int maxRowLen, int rowsCount,
        String outerStyle, String title) {
        return outerHtml(outerImgSvgAttrs(innerSvg, fontSizePX, maxRowLen, rowsCount, outerStyle), title);
    }

    public static String outerHtmlImgSvgClass(
        String innerSvg,
        int fontSizePX, int maxRowLen, int rowsCount,
        String outerClassName, String title) {
        // TODO: there should be HTML-style section generated properly in HTML-head
        return outerHtml(outerImgSvgClass(innerSvg, fontSizePX, maxRowLen, rowsCount, outerClassName), title);
    }

    // --------------------------------------------------------------------------------------------

    private OuterTagUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
