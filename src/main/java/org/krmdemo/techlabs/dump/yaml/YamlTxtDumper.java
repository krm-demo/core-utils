package org.krmdemo.techlabs.dump.yaml;

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
 *  Implementation of {@link TreeDumper} that dumps the tree
 *  in a {@link Highlight.Structure#YAML YAML} logical structure.
 */
public class YamlTxtDumper implements TreeDumper {

    final PrintStream out;
    final RenderSpec renderSpec;
    final Deque<String> currentPath = new ArrayDeque<>();

    public YamlTxtDumper(PrintStream out, RenderSpec renderSpec) {
        this.out = Objects.requireNonNull(out);
        this.renderSpec = Objects.requireNonNull(renderSpec);
    }

    @Override
    public void acceptNull() {
        if (parentIsMapping()) {
            out.print(' ');
        }
        out.print(renderSpec.highlightNullAnsi(Highlight.Structure.YAML));
    }

    @Override
    public void acceptScalar(ScalarNode scalarNode) {
        if (parentIsMapping()) {
            out.print(' ');
        }
        out.print(renderSpec.highlightValueAnsi(Highlight.Structure.YAML, scalarNode.text()));
    }

    @Override
    public void acceptSequence(SequenceNode sequenceNode) {
        int count = 0;
        Iterator<Node> itNode = sequenceNode.sequenceItems().iterator();
        while (itNode.hasNext()) {
            if (count > 0 || parentIsMapping()) {
                out.println();
                out.print(indent());
            }
            out.print(dashSpace());
            count++;
            currentPath.addLast(String.format("[%d]", count));
            itNode.next().visit(this);
            currentPath.removeLast();
        }
        if (count == 0) {
            out.print(emptySequence());
        }
    }

    @Override
    public void acceptMappings(MappingsNode mappingsNode) {
        int count = 0;
        Iterator<Map.Entry<String, Node>> itEntry = mappingsNode.mappingsItems().iterator();
        while (itEntry.hasNext()) {
            if (count > 0 || parentIsMapping()) {
                out.println();
                out.print(indent());
            }
            count++;
            Map.Entry<String, Node> entry = itEntry.next();
            currentPath.addLast(String.format("(%s)", entry.getKey()));
            out.print(renderSpec.highlightKeyAnsi(Highlight.Structure.JSON, entry.getKey()));
            out.print(colon());
            entry.getValue().visit(this);
            currentPath.removeLast();
        }
        if (count == 0) {
            out.print(emptyMappings());
        }
    }

    private boolean parentIsMapping() {
        return !currentPath.isEmpty() && currentPath.getLast().endsWith(")");
    }

    private String indent() {
        return "  ".repeat(currentPath.size());
    }

    private String dashSpace() {
        return renderSpec.highlightSyntaxAnsi(Highlight.Structure.YAML, "- ");
    }

    private String colon() {
        return renderSpec.highlightSyntaxAnsi(Highlight.Structure.YAML, ':');
    }

    private String emptySequence() {
        return renderSpec.highlightSyntaxAnsi(Highlight.Structure.YAML, " []");
    }

    private String emptyMappings() {
        return renderSpec.highlightSyntaxAnsi(Highlight.Structure.YAML, " {}");
    }
}
