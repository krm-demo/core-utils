package org.krmdemo.techlabs.dump.json;

import org.krmdemo.techlabs.dump.TreeDumper;
import org.krmdemo.techlabs.dump.render.AnsiHighlighter;
import org.krmdemo.techlabs.dump.render.Highlighter;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class JsonTxtDumper implements TreeDumper {

    final PrintStream out;
    final Highlighter highlighter;
    final Deque<String> currentPath = new ArrayDeque<>();

    public JsonTxtDumper(PrintStream out, Highlighter highlighter) {
        this.out = Objects.requireNonNull(out);
        this.highlighter = Objects.requireNonNull(highlighter);
    }

    @Override
    public void acceptNull() {
        out.print(highlighter.highlightNull());
    }

    @Override
    public void acceptScalar(ScalarNode scalarNode) {
        out.print(doubleQuotes());
        out.print(highlighter.highlightValue(scalarNode.text()));
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
                out.print(",");
            }
            count++;
            currentPath.addLast(String.format("(%s)", entry.getKey()));
            out.println();
            out.print(indent());
            out.print(doubleQuotes());
            out.printf(highlighter.highlightKey(entry.getKey()));
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
        return highlighter.syntaxJson(',');
    }

    private String colonSpace() {
        return highlighter.syntaxJson(": ");
    }

    private String doubleQuotes() {
        return highlighter.syntaxJson('"');
    }

    private String openSquareBracket() {
        return highlighter.syntaxJson('[');
    }

    private String closedSquareBracket() {
        return highlighter.syntaxJson(']');
    }

    private String openFigureBracket() {
        return highlighter.syntaxJson('{');
    }

    private String closedFigureBracket() {
        return highlighter.syntaxJson('}');
    }
}
