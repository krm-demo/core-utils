package org.krmdemo.techlabs.dump.render;

import org.krmdemo.techlabs.dump.render.Highlight.Kind;
import org.krmdemo.techlabs.dump.render.Highlight.Place;
import org.krmdemo.techlabs.dump.render.Highlight.Structure;
import org.krmdemo.techlabs.dump.render.Highlight.Target;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.EnumSet;

public class RenderSpec {

    public enum Feature {
        SHOW_NULL,
        SHOW_EMPTY,
        RENDER_HTML_DOC,
        USE_HTML_CSS,
        NESTED_SECTIONS,
        INLINE_IMG,
    }

    private final Highlight highlight;
    private final EnumSet<Feature> featuresSet = EnumSet.noneOf(Feature.class);

    public RenderSpec(Highlight highlight, Feature... featuresArr) {
        this.highlight = highlight;
        this.featuresSet.addAll(Arrays.asList(featuresArr));
    }

    public boolean hasFeature(Feature feature) {
        return featuresSet.contains(feature);
    }

    public String dumpOuterHtml(String innerHTML, Structure struct) {
        String outerStyle = highlight.lookupValue(Place.of(Kind.BG, struct, Target.HTML_STYLE));
        if (hasFeature(Feature.RENDER_HTML_DOC)) {
            return OuterTagUtils.outerHtmlDivStyle(innerHTML, outerStyle,
                String.format("%s representation (rendered by Core-Utils)", struct));
        } else {
            return OuterTagUtils.outerDivStyle(innerHTML, outerStyle);
        }
    }

    // --------------------------------------------------------------------------------------------

    String highlightAnsi(Kind kind, Structure struct, String str) {
        String ansiStyle = highlight.lookupValue(Place.of(kind, struct, Target.TXT_ANSI));
        if (ansiStyle == null) {
            return str;
        }
        String ansiRenderStr = String.format("@|%s %s|@", ansiStyle, str);
        return CommandLine.Help.Ansi.AUTO.string(ansiRenderStr);
    }

    String highlightHtmlStyle(Kind kind, Structure struct, String str) {
        String styleValue = highlight.lookupValue(Place.of(kind, struct, Target.HTML_STYLE));
        if (styleValue == null) {
            return str;
        }
        return String.format("""
            <span style="%s">%s</span>""",
            styleValue, str);
    }

    String highlightHtmlCss(Kind kind, Structure struct, String str) {
        String cssClassName = highlight.lookupValue(Place.of(kind, struct, Target.HTML_CSS));
        if (cssClassName == null) {
            return str;
        }
        return String.format("""
            <span class="%s">%s</span>""",
            cssClassName, str);
    }

    String highlightSvgAttrs(Kind kind, Structure struct, String str) {
        String svgAttrs = highlight.lookupValue(Place.of(kind, struct, Target.SVG_ATTRS));
        if (svgAttrs == null) {
            return str;
        }
        return String.format("""
            <tspan %s>%s</tspan>""",
            svgAttrs, str);
    }

    String highlightSvgClass(Kind kind, Structure struct, String str) {
        String svgClassName = highlight.lookupValue(Place.of(kind, struct, Target.SVG_CLASS));
        if (svgClassName == null) {
            return str;
        }
        return String.format("""
            <tspan class="%s">%s</tspan>""",
            svgClassName, str);
    }

    // --------------------------------------------------------------------------------------------

    public String highlightSyntaxAnsi(Structure struct, char syntaxSymbolChar) {
        return highlightSyntaxAnsi(struct, String.valueOf(syntaxSymbolChar));
    }

    public String highlightSyntaxHtmlStyle(Structure struct, char syntaxSymbolChar) {
        return highlightSyntaxHtmlStyle(struct, String.valueOf(syntaxSymbolChar));
    }

    public String highlightSyntaxHtmlCss(Structure struct, char syntaxSymbolChar) {
        return highlightSyntaxHtmlCss(struct, String.valueOf(syntaxSymbolChar));
    }

    public String highlightSyntaxSvgAttrs(Structure struct, char syntaxSymbolChar) {
        return highlightSyntaxSvgAttrs(struct, String.valueOf(syntaxSymbolChar));
    }

    public String highlightSyntaxSvgClass(Structure struct, char syntaxSymbolChar) {
        return highlightSyntaxSvgClass(struct, String.valueOf(syntaxSymbolChar));
    }

    // --------------------------------------------------------------------------------------------

    public String highlightSyntaxAnsi(Structure struct, String syntaxSymbol) {
        return highlightAnsi(Kind.SYNTAX, struct, syntaxSymbol);
    }

    public String highlightSyntaxHtmlStyle(Structure struct, String syntaxSymbol) {
        return highlightHtmlStyle(Kind.SYNTAX, struct, syntaxSymbol);
    }

    public String highlightSyntaxHtmlCss(Structure struct, String syntaxSymbol) {
        return highlightHtmlCss(Kind.SYNTAX, struct, syntaxSymbol);
    }

    public String highlightSyntaxSvgAttrs(Structure struct, String syntaxSymbol) {
        return highlightSvgAttrs(Kind.SYNTAX, struct, syntaxSymbol);
    }

    public String highlightSyntaxSvgClass(Structure struct, String syntaxSymbol) {
        return highlightSvgClass(Kind.SYNTAX, struct, syntaxSymbol);
    }

    // --------------------------------------------------------------------------------------------

    public String highlightNullAnsi(Structure struct) {
        return highlightAnsi(Kind.NULL, struct, "null");
    }

    public String highlightNullHtmlStyle(Structure struct) {
        return highlightHtmlStyle(Kind.NULL, struct, "null");
    }

    public String highlightNullHtmlCss(Structure struct) {
        return highlightHtmlCss(Kind.NULL, struct, "null");
    }

    public String highlightNullSvgAttrs(Structure struct) {
        return highlightSvgAttrs(Kind.NULL, struct, "null");
    }

    public String highlightNullSvgClass(Structure struct) {
        return highlightSvgClass(Kind.NULL, struct, "null");
    }

    // --------------------------------------------------------------------------------------------

    public String highlightKeyAnsi(Structure struct, String keyStr) {
        return highlightAnsi(Kind.KEY, struct, keyStr);
    }

    public String highlightKeyHtmlStyle(Structure struct, String keyStr) {
        return highlightHtmlStyle(Kind.KEY, struct, keyStr);
    }

    public String highlightKeyHtmlCss(Structure struct, String keyStr) {
        return highlightHtmlCss(Kind.KEY, struct, keyStr);
    }

    public String highlightKeySvgAttrs(Structure struct, String keyStr) {
        return highlightSvgAttrs(Kind.KEY, struct, keyStr);
    }

    public String highlightKeySvgClass(Structure struct, String keyStr) {
        return highlightSvgClass(Kind.KEY, struct, keyStr);
    }

    // --------------------------------------------------------------------------------------------

    public String highlightValueAnsi(Structure struct, String valueStr) {
        return highlightAnsi(Kind.VALUE, struct, valueStr);
    }

    public String highlightValueHtmlStyle(Structure struct, String valueStr) {
        return highlightHtmlStyle(Kind.VALUE, struct, valueStr);
    }

    public String highlightValueHtmlCss(Structure struct, String valueStr) {
        return highlightHtmlCss(Kind.VALUE, struct, valueStr);
    }

    public String highlightValueSvgAttrs(Structure struct, String valueStr) {
        return highlightSvgAttrs(Kind.VALUE, struct, valueStr);
    }

    public String highlightValueSvgClass(Structure struct, String valueStr) {
        return highlightSvgClass(Kind.VALUE, struct, valueStr);
    }

}
