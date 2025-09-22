package org.krmdemo.techlabs.dump.json;

import org.krmdemo.techlabs.dump.StringBuilderOut;
import org.krmdemo.techlabs.dump.TreeDumper;
import org.krmdemo.techlabs.dump.render.Highlight;
import org.krmdemo.techlabs.dump.render.RenderSpec;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of {@link TreeDumper} that dumps into the passed {@link PrintStream}
 * the tree of a {@link Highlight.Structure#JSON JSON} logical structure
 * in {@link Highlight.Target#SVG_ATTRS SVG-Attrs} target format (SVG-Class is coming soon).
 */
public class JsonSvgDumper implements TreeDumper {

    final PrintStream out;
    final RenderSpec renderSpec;
    final Deque<String> currentPath = new ArrayDeque<>();

    int totalRowCount = 0;
    int currentRowLen = 0;
    int maxRowLen = 0;

    public JsonSvgDumper(PrintStream out, RenderSpec renderSpec) {
        this.out = Objects.requireNonNull(out);
        this.renderSpec = Objects.requireNonNull(renderSpec);
    }

    @Override
    public void acceptRoot(Node rootNode) {
        PrintStream innerOut = StringBuilderOut.create();
        JsonSvgDumper innerDumper = new JsonSvgDumper(innerOut, renderSpec);
        rootNode.visit(innerDumper);
        out.println(renderSpec.dumpOuterSvg(
            innerOut.toString(), Highlight.Structure.JSON,
            14, innerDumper.maxRowLen, innerDumper.totalRowCount));
    }

    private void addWidth(int delta) {
        this.currentRowLen += delta;
        this.maxRowLen = Math.max(this.maxRowLen, this.currentRowLen);
    }

    @Override
    public void acceptNull() {
        if (currentPath.isEmpty()) {
            out.print(svgRowStart());
        }
        out.print(renderSpec.highlightNullSvgAttrs(Highlight.Structure.JSON));
        addWidth(4); // <-- null
        if (currentPath.isEmpty()) {
            out.print(svgRowEnd());
        }
    }

    @Override
    public void acceptScalar(ScalarNode scalarNode) {
        if (currentPath.isEmpty()) {
            out.print(svgRowStart());
        }
        out.print(doubleQuotes());
        out.print(renderSpec.highlightValueSvgAttrs(Highlight.Structure.JSON, scalarNode.text()));
        out.print(doubleQuotes());
        addWidth(2 + scalarNode.text().length()); // <-- "value"
        if (currentPath.isEmpty()) {
            out.print(svgRowEnd());
        }
    }

    @Override
    public void acceptSequence(SequenceNode sequenceNode) {
        if (currentPath.isEmpty()) {
            out.print(svgRowStart());
        }
        out.print(openSquareBracket());
        addWidth(1);
        int count = 0;
        Iterator<Node> itNode = sequenceNode.sequenceItems().iterator();
        while (itNode.hasNext()) {
            if (count > 0) {
                out.print(comma());
                addWidth(1);
            }
            count++;
            currentPath.addLast(String.format("[%d]", count));
            out.println(svgRowEnd());
            out.print(svgRowStart() + indent());
            addIndentWidth();
            itNode.next().visit(this);
            currentPath.removeLast();
        }
        if (count > 0) {
            out.println(svgRowEnd());
            out.print(svgRowStart() + indent());
            addIndentWidth();
        }
        out.print(closedSquareBracket());
        if (currentPath.isEmpty()) {
            out.print(svgRowEnd());
        }
    }

    @Override
    public void acceptMappings(MappingsNode mappingsNode) {
        if (currentPath.isEmpty()) {
            out.print(svgRowStart());
        }
        out.print(openFigureBracket());
        addWidth(1);
        int count = 0;
        Iterator<Map.Entry<String, Node>> itEntry = mappingsNode.mappingsItems().iterator();
        while (itEntry.hasNext()) {
            Map.Entry<String, Node> entry = itEntry.next();
            if (count > 0) {
                out.print(comma());
                addWidth(1);
            }
            count++;
            currentPath.addLast(String.format("(%s)", entry.getKey()));
            out.println(svgRowEnd());
            out.print(svgRowStart() + indent());
            addIndentWidth();
            out.print(doubleQuotes());
            out.print(renderSpec.highlightKeySvgAttrs(Highlight.Structure.JSON, entry.getKey()));
            out.print(doubleQuotes());
            out.print(colonSpace());
            addWidth(4 + entry.getKey().length());
            entry.getValue().visit(this);
            currentPath.removeLast();
        }
        if (count > 0) {
            out.println(svgRowEnd());
            out.print(svgRowStart() + indent());
            addIndentWidth();
        }
        out.print(closedFigureBracket());
        addWidth(1);
        if (currentPath.isEmpty()) {
            out.print(svgRowEnd());
        }
    }

    private String svgRowStart() {
        return String.format("""
            <tspan class="json-svg-row json-svg-row-%d" x="0" dy="1.2em">""",
            totalRowCount++
        );
    }

    private String svgRowEnd() {
        this.currentRowLen = 0;
        return "</tspan>";
    }

    private String indent() {
        return "  ".repeat(currentPath.size());
    }

    private void addIndentWidth() {
        addWidth(2 * currentPath.size());
    }

    private String comma() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.JSON, ',');
    }

    private String colonSpace() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.JSON, ": ");
    }

    private String doubleQuotes() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.JSON, '"');
    }

    private String openSquareBracket() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.JSON, '[');
    }

    private String closedSquareBracket() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.JSON, ']');
    }

    private String openFigureBracket() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.JSON, '{');
    }

    private String closedFigureBracket() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.JSON, '}');
    }

}
