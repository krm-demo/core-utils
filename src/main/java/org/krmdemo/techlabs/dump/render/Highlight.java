package org.krmdemo.techlabs.dump.render;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.stream.CoreCollectors.toLinkedMap;

/**
 * This class represents an immutable multidimensional {@link Map} of {@link Rule highlight rules},
 * where the value of rule is either Ansi-Style or HTML-Style (mostly the color of text).
 */
public class Highlight {

    /**
     * The kind of JSON or YAML tokens to highlight
     */
    public enum Kind {
        SYNTAX,
        NULL,
        KEY,
        VALUE,
        BG
    }

    /**
     * The target logical structure of rendered document (<b>JSON</b> or <b>YAML</b>)
     */
    public enum Structure {
        /**
         * <a href="https://www.json.org/json-en.html">JSON</a>-structure of rendered document or fragment
         * <hr/>
         * (meaning of acronym is - <b>J</b>ava-<b>S</b>cript <b>O</b>bject <b>N</b>otation)
         */
        JSON,

        /**
         * <a href="https://yaml.org/spec/1.2.2/">YAML</a>-structure of rendered document or fragment
         * <hr/>
         * (meaning of acronym is - <b>Y</b>AML-<b>A</b>in't <b>M</b>arkup <b>L</b>anguage),<br/>
         * which mostly means that YAML is a markup-language without markup-tags as XML or HTML
         */
        YAML
    }

    /**
     * The target format of output (HTML-document/HTML-fragment, console with ANSI-escape sequences)
     */
    public enum Target {
        TXT_ANSI,
        HTML_STYLE,
        HTML_CSS,
        SVG
    }

    /**
     * This record represents a {@link Map.Entry#getKey() key} in multidimensional {@link Map}
     * to store and lookup the highlight-rules.
     *
     * @param kind the kind of JSON or YAML tokens to highlight
     * @param struct the target structure of rendered document (JSON or YAML)
     * @param target the target format of output (ANSI, HTML, SVG, ...)
     */
    public record Place(
        Kind kind,
        Structure struct,
        Target target
    ) {
        static Place of(Kind kind, Structure struct, Target target) {
            return new Place(kind, struct, target);
        }
    }

    /**
     * Individual highlight-rule - the correspondence between place and style to apply.
     *
     * @param kind the kind of JSON or YAML tokens to highlight
     * @param struct the target structure of rendered document (JSON or YAML)
     * @param target the target format of output (ANSI, HTML, SVG, ...)
     * @param valueStr the string value of ANSI-Style, HTML-Style or HTML-CSS
     */
    public record Rule(
        Kind kind,
        Structure struct,
        Target target,
        String valueStr
    ) {
        Place place() {
            return Place.of(kind, struct, target);
        }
        static Rule of(Kind kind, Structure struct, Target target, String valueStr) {
            return new Rule(kind, struct, target, valueStr);
        }
    }

    final private Map<Place, String> rulesMap;
    private Highlight(Stream<Rule> rules) {
        this.rulesMap = rules.collect(toLinkedMap(Rule::place, Rule::valueStr));
    }
    private Highlight(Highlight base, Stream<Rule> rulesOver) {
        this.rulesMap = new LinkedHashMap<>(base.rulesMap);
        rulesOver.forEach(rule -> this.rulesMap.put(rule.place(), rule.valueStr()));
    }

    /**
     * @param place a place to look up the value for
     * @return the string value of ANSI-Style, HTML-Style or HTML-CSS that correspond to that {@code place}
     */
    public String lookupValue(Place place) {
        return rulesMap.get(place);
    }

    /**
     * A factory-method to populate the multidimensional {@link Map} of {@link Rule highlight rules}
     *
     * @param rulesArr var-args-arrays of {@link Rule highlight rules}
     * @return an immutable instance of {@link Highlight} class
     */
    public static Highlight highlight(Rule... rulesArr) {
        return new Highlight(Arrays.stream(rulesArr));
    }

    /**
     * A factory-method to extends/overwrite the {@code base} instance of {@link Highlight}
     * with additional (substitutional) {@link Rule highlight rules}
     *
     * @param base a base instance of {@link Highlight} to extends/overwrite
     * @param rulesArr {@link Rule highlight rules} to extends/overwrite with
     * @return a new immutable instance of {@link Highlight} class, which is based on {@code base}
     */
    public static Highlight highlight(Highlight base, Rule... rulesArr) {
        return new Highlight(base, Arrays.stream(rulesArr));
    }

    /**
     * <b>no-highlighting</b> scheme (no {@link Rule highlight rules})
     */
    public static Highlight NONE = highlight();

    /**
     * The default highlight-scheme is based on: <ul>
     *     <li><b>white</b> syntax ( '{@code "}', '<code>{</code>', '<code>}</code>',
     *          '{@code [}', '{@code ]}', '{@code ,}', '{@code -}', '{@code :}' )</li>
     *     <li><b>magenta</b> {@code null}s</li>
     *     <li><b>blue</b> {@code key}s of JSON and YAML mappings</li>
     *     <li><b>green</b> {@code value}s of JSON and YAML mappings and any scalars and sequence elements</li>
     * </ul>
     * As for HTML-style correspondence - the settings of well-known Python-library
     * <a href="https://github.com/pycontribs/ansi2html/blob/main/src/ansi2html/style.py#L135-L153">
     *     ansi2html (/src/ansi2html/style.py)
     * </a>
     * for "<i>basic</i>"<b> {@code MacOS-Terminal}</b> are used.
     */
    public static Highlight DEFAULT = highlight(
        Rule.of(Kind.SYNTAX, Structure.JSON, Target.TXT_ANSI, "white"),
        Rule.of(Kind.SYNTAX, Structure.JSON, Target.HTML_STYLE, "color: #808080;"),
        Rule.of(Kind.SYNTAX, Structure.YAML, Target.TXT_ANSI, "white"),
        Rule.of(Kind.SYNTAX, Structure.YAML, Target.HTML_STYLE, "color: #808080;"),

        Rule.of(Kind.NULL, Structure.JSON, Target.TXT_ANSI, "magenta"),
        Rule.of(Kind.NULL, Structure.JSON, Target.HTML_STYLE, "color: #800080;"),
        Rule.of(Kind.NULL, Structure.YAML, Target.TXT_ANSI, "magenta"),
        Rule.of(Kind.NULL, Structure.YAML, Target.HTML_STYLE, "color: #800080;"),

        Rule.of(Kind.KEY, Structure.JSON, Target.TXT_ANSI, "blue"),
        Rule.of(Kind.KEY, Structure.JSON, Target.HTML_STYLE, "color: #000080;"),
        Rule.of(Kind.KEY, Structure.YAML, Target.TXT_ANSI, "blue"),
        Rule.of(Kind.KEY, Structure.YAML, Target.HTML_STYLE, "color: #000080;"),

        Rule.of(Kind.VALUE, Structure.JSON, Target.TXT_ANSI, "green"),
        Rule.of(Kind.VALUE, Structure.JSON, Target.HTML_STYLE, "color: #008000;"),
        Rule.of(Kind.VALUE, Structure.YAML, Target.TXT_ANSI, "green"),
        Rule.of(Kind.VALUE, Structure.YAML, Target.HTML_STYLE, "color: #008000;"),

        Rule.of(Kind.BG, Structure.JSON, Target.HTML_STYLE, "background-color: #FFFFFF;"),
        Rule.of(Kind.BG, Structure.YAML, Target.HTML_STYLE, "background-color: #FFFFFF;")
    );
}
