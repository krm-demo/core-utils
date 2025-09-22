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
 * in {@link Highlight.Target#HTML_STYLE HTML-Style} target format (HTML-CSS is coming soon).
 */
public class JsonHtmlDumper implements TreeDumper {

    final PrintStream out;
    final RenderSpec renderSpec;
    final Deque<String> currentPath = new ArrayDeque<>();

    int totalRowCount = 0;

    public JsonHtmlDumper(PrintStream out, RenderSpec renderSpec) {
        this.out = Objects.requireNonNull(out);
        this.renderSpec = Objects.requireNonNull(renderSpec);
    }

    @Override
    public void acceptRoot(Node rootNode) {
        PrintStream innerOut = StringBuilderOut.create();
        JsonHtmlDumper innerDumper = new JsonHtmlDumper(innerOut, renderSpec);
        rootNode.visit(innerDumper);
        out.println(renderSpec.dumpOuterHtml(innerOut.toString(), Highlight.Structure.JSON));
    }

    @Override
    public void acceptNull() {
        if (currentPath.isEmpty()) {
            out.print(divRowStart());
        }
        out.print(renderSpec.highlightNullHtmlStyle(Highlight.Structure.JSON));
        if (currentPath.isEmpty()) {
            out.print(divRowEnd());
        }
    }

    @Override
    public void acceptScalar(ScalarNode scalarNode) {
        if (currentPath.isEmpty()) {
            out.print(divRowStart());
        }
        out.print(doubleQuotes());
        out.print(renderSpec.highlightValueHtmlStyle(Highlight.Structure.JSON, scalarNode.text()));
        out.print(doubleQuotes());
        if (currentPath.isEmpty()) {
            out.print(divRowEnd());
        }
    }

    @Override
    public void acceptSequence(SequenceNode sequenceNode) {
        if (currentPath.isEmpty()) {
            out.print(divRowStart());
        }
        out.print(openSquareBracket());
        int count = 0;
        Iterator<Node> itNode = sequenceNode.sequenceItems().iterator();
        while (itNode.hasNext()) {
            if (count > 0) {
                out.print(comma());
            }
            count++;
            currentPath.addLast(String.format("[%d]", count));
            out.println(divRowEnd());
            out.print(divRowStart() + indent());
            itNode.next().visit(this);
            currentPath.removeLast();
        }
        if (count > 0) {
            out.println(divRowEnd());
            out.print(divRowStart() + indent());
        }
        out.print(closedSquareBracket());
        if (currentPath.isEmpty()) {
            out.print(divRowEnd());
        }
    }

    @Override
    public void acceptMappings(MappingsNode mappingsNode) {
        if (currentPath.isEmpty()) {
            out.print(divRowStart());
        }
        out.print(openFigureBracket());
        int count = 0;
        Iterator<Map.Entry<String, Node>> itEntry = mappingsNode.mappingsItems().iterator();
        while (itEntry.hasNext()) {
            Map.Entry<String, Node> entry = itEntry.next();
            if (count > 0) {
                out.print(comma());
            }
            count++;
            currentPath.addLast(String.format("(%s)", entry.getKey()));
            out.println(divRowEnd());
            out.print(divRowStart() + indent());
            out.print(doubleQuotes());
            out.print(renderSpec.highlightKeyHtmlStyle(Highlight.Structure.JSON, entry.getKey()));
            out.print(doubleQuotes());
            out.print(colonSpace());
            entry.getValue().visit(this);
            currentPath.removeLast();
        }
        if (count > 0) {
            out.println(divRowEnd());
            out.print(divRowStart() + indent());
        }
        out.print(closedFigureBracket());
        if (currentPath.isEmpty()) {
            out.print(divRowEnd());
        }
    }

    private String divRowStart() {
        return String.format("""
            <div class="json-row json-row-%d" style="white-space: pre">""",
            totalRowCount++
        );
    }

    private String divRowEnd() {
        return "</div>";
    }

    private String indent() {
        return "  ".repeat(currentPath.size());
    }

    private String comma() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.JSON, ',');
    }

    private String colonSpace() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.JSON, ": ");
    }

    private String doubleQuotes() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.JSON, '"');
    }

    private String openSquareBracket() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.JSON, '[');
    }

    private String closedSquareBracket() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.JSON, ']');
    }

    private String openFigureBracket() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.JSON, '{');
    }

    private String closedFigureBracket() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.JSON, '}');
    }
}
