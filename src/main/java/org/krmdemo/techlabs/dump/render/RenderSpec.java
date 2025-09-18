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
        USE_HTML_CSS,
        NESTED_SECTIONS,
        INLINE_IMG,
        SKIP_OUTER_HTML
    }

    private final Highlight highlight;
    private final EnumSet<Feature> featuresSet = EnumSet.noneOf(Feature.class);

    public RenderSpec(Highlight highlight, Feature... featuresArr) {
        this.highlight = highlight;
        this.featuresSet.addAll(Arrays.asList(featuresArr));
    }

    boolean hasFeature(Feature feature) {
        return featuresSet.contains(feature);
    }

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
        String cssClass = highlight.lookupValue(Place.of(kind, struct, Target.HTML_CSS));
        if (cssClass == null) {
            return str;
        }
        return String.format("""
            <span class="%s">%s</span>""",
            cssClass, str);
    }

    public String highlightSyntaxAnsi(Structure struct, char syntaxSymbolChar) {
        return highlightSyntaxAnsi(struct, String.valueOf(syntaxSymbolChar));
    }

    public String highlightSyntaxHtmlStyle(Structure struct, char syntaxSymbolChar) {
        return highlightSyntaxHtmlStyle(struct, String.valueOf(syntaxSymbolChar));
    }

    public String highlightSyntaxHtmlCss(Structure struct, char syntaxSymbolChar) {
        return highlightSyntaxHtmlCss(struct, String.valueOf(syntaxSymbolChar));
    }

    public String highlightSyntaxAnsi(Structure struct, String syntaxSymbol) {
        return highlightAnsi(Kind.SYNTAX, struct, syntaxSymbol);
    }

    public String highlightSyntaxHtmlStyle(Structure struct, String syntaxSymbol) {
        return highlightHtmlStyle(Kind.SYNTAX, struct, syntaxSymbol);
    }

    public String highlightSyntaxHtmlCss(Structure struct, String syntaxSymbol) {
        return highlightHtmlCss(Kind.SYNTAX, struct, syntaxSymbol);
    }

    public String highlightNullAnsi(Structure struct) {
        return highlightAnsi(Kind.NULL, struct, "null");
    }

    public String highlightNullHtmlStyle(Structure struct) {
        return highlightHtmlStyle(Kind.NULL, struct, "null");
    }

    public String highlightNullHtmlCss(Structure struct) {
        return highlightHtmlCss(Kind.NULL, struct, "null");
    }

    public String highlightKeyAnsi(Structure struct, String keyStr) {
        return highlightAnsi(Kind.KEY, struct, keyStr);
    }

    public String highlightKeyHtmlStyle(Structure struct, String keyStr) {
        return highlightHtmlStyle(Kind.KEY, struct, keyStr);
    }

    public String highlightKeyHtmlCss(Structure struct, String keyStr) {
        return highlightHtmlCss(Kind.KEY, struct, keyStr);
    }

    public String highlightValueAnsi(Structure struct, String valueStr) {
        return highlightAnsi(Kind.VALUE, struct, valueStr);
    }

    public String highlightValueHtmlStyle(Structure struct, String valueStr) {
        return highlightHtmlStyle(Kind.VALUE, struct, valueStr);
    }

    public String highlightValueHtmlCss(Structure struct, String valueStr) {
        return highlightHtmlCss(Kind.VALUE, struct, valueStr);
    }
}
