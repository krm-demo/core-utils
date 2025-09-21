package org.krmdemo.techlabs.dump.json;

import org.krmdemo.techlabs.dump.TreeDumper;
import org.krmdemo.techlabs.dump.render.Highlight;
import org.krmdemo.techlabs.dump.render.Highlight.Structure;
import org.krmdemo.techlabs.dump.render.RenderSpec;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of {@link TreeDumper} that dumps into the passed {@link PrintStream}
 * the tree of a {@link Structure#JSON JSON} logical structure
 * in {@link Highlight.Target#TXT_ANSI ANSI-Txt} target format.
 */
public class JsonTxtDumper implements TreeDumper {

    final PrintStream out;
    final RenderSpec renderSpec;
    final Deque<String> currentPath = new ArrayDeque<>();

    public JsonTxtDumper(PrintStream out, RenderSpec renderSpec) {
        this.out = Objects.requireNonNull(out);
        this.renderSpec = Objects.requireNonNull(renderSpec);
    }

    @Override
    public void acceptNull() {
        out.print(renderSpec.highlightNullAnsi(Structure.JSON));
    }

    @Override
    public void acceptScalar(ScalarNode scalarNode) {
        out.print(doubleQuotes());
        out.print(renderSpec.highlightValueAnsi(Structure.JSON, scalarNode.text()));
        out.print(doubleQuotes());
    }

    @Override
    public void acceptSequence(SequenceNode sequenceNode) {
        out.print(openSquareBracket());
        int count = 0;
        Iterator<Node> itNode = sequenceNode.sequenceItems().iterator();
        while (itNode.hasNext()) {
            if (count > 0) {
                out.print(comma());
            }
            count++;
            currentPath.addLast(String.format("[%d]", count));
            out.println();
            out.print(indent());
            itNode.next().visit(this);
            currentPath.removeLast();
        }
        if (count > 0) {
            out.println();
            out.print(indent());
        }
        out.print(closedSquareBracket());
    }

    @Override
    public void acceptMappings(MappingsNode mappingsNode) {
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
            out.println();
            out.print(indent());
            out.print(doubleQuotes());
            out.print(renderSpec.highlightKeyAnsi(Structure.JSON, entry.getKey()));
            out.print(doubleQuotes());
            out.print(colonSpace());
            entry.getValue().visit(this);
            currentPath.removeLast();
        }
        if (count > 0) {
            out.println();
            out.print(indent());
        }
        out.print(closedFigureBracket());
    }

    private String indent() {
        return "  ".repeat(currentPath.size());
    }

    private String comma() {
        return renderSpec.highlightSyntaxAnsi(Structure.JSON, ',');
    }

    private String colonSpace() {
        return renderSpec.highlightSyntaxAnsi(Structure.JSON, ": ");
    }

    private String doubleQuotes() {
        return renderSpec.highlightSyntaxAnsi(Structure.JSON, '"');
    }

    private String openSquareBracket() {
        return renderSpec.highlightSyntaxAnsi(Structure.JSON, '[');
    }

    private String closedSquareBracket() {
        return renderSpec.highlightSyntaxAnsi(Structure.JSON, ']');
    }

    private String openFigureBracket() {
        return renderSpec.highlightSyntaxAnsi(Structure.JSON, '{');
    }

    private String closedFigureBracket() {
        return renderSpec.highlightSyntaxAnsi(Structure.JSON, '}');
    }
}
