package org.krmdemo.techlabs.dump.yaml;

import org.krmdemo.techlabs.dump.StringBuilderOut;
import org.krmdemo.techlabs.dump.TreeDumper;
import org.krmdemo.techlabs.dump.json.JsonHtmlDumper;
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
 * the tree of a {@link Highlight.Structure#YAML YAML} logical structure
 * in {@link Highlight.Target#HTML_STYLE HTML} target format (HTML-CSS is coming soon).
 */
public class YamlHtmlDumper implements TreeDumper {

    final PrintStream out;
    final RenderSpec renderSpec;
    final Deque<String> currentPath = new ArrayDeque<>();

    int totalRowCount = 0;

    public YamlHtmlDumper(PrintStream out, RenderSpec renderSpec) {
        this.out = Objects.requireNonNull(out);
        this.renderSpec = Objects.requireNonNull(renderSpec);
    }

    @Override
    public void acceptRoot(Node rootNode) {
        PrintStream innerOut = StringBuilderOut.create();
        YamlHtmlDumper innerDumper = new YamlHtmlDumper(innerOut, renderSpec);
        rootNode.visit(innerDumper);
        out.println(renderSpec.dumpOuterHtml(innerOut.toString(), Highlight.Structure.YAML));
    }

    @Override
    public void acceptNull() {
        if (currentPath.isEmpty()) {
            out.print(divRowStart());
        }
        if (parentIsMapping()) {
            out.print(' ');
        }
        out.print(renderSpec.highlightNullHtmlStyle(Highlight.Structure.YAML));
        if (currentPath.isEmpty()) {
            out.print(divRowEnd());
        }
    }

    @Override
    public void acceptScalar(ScalarNode scalarNode) {
        if (currentPath.isEmpty()) {
            out.print(divRowStart());
        }
        if (parentIsMapping()) {
            out.print(' ');
        }
        out.print(renderSpec.highlightValueHtmlStyle(Highlight.Structure.YAML, scalarNode.text()));
        if (currentPath.isEmpty()) {
            out.print(divRowEnd());
        }
    }

    @Override
    public void acceptSequence(SequenceNode sequenceNode) {
        if (currentPath.isEmpty()) {
            out.print(divRowStart());
        }
        int count = 0;
        Iterator<Node> itNode = sequenceNode.sequenceItems().iterator();
        while (itNode.hasNext()) {
            if (count > 0 || parentIsMapping()) {
                out.println(divRowEnd());
                out.print(divRowStart());
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
        if (currentPath.isEmpty()) {
            out.print(divRowEnd());
        }
    }

    @Override
    public void acceptMappings(MappingsNode mappingsNode) {
        if (currentPath.isEmpty()) {
            out.print(divRowStart());
        }
        int count = 0;
        Iterator<Map.Entry<String, Node>> itEntry = mappingsNode.mappingsItems().iterator();
        while (itEntry.hasNext()) {
            if (count > 0 || parentIsMapping()) {
                out.println(divRowEnd());
                out.print(divRowStart());
                out.print(indent());
            }
            count++;
            Map.Entry<String, Node> entry = itEntry.next();
            currentPath.addLast(String.format("(%s)", entry.getKey()));
            out.print(renderSpec.highlightKeyHtmlStyle(Highlight.Structure.JSON, entry.getKey()));
            out.print(colon());
            entry.getValue().visit(this);
            currentPath.removeLast();
        }
        if (count == 0) {
            out.print(emptyMappings());
        }
        if (currentPath.isEmpty()) {
            out.print(divRowEnd());
        }
    }

    private boolean parentIsMapping() {
        return !currentPath.isEmpty() && currentPath.getLast().endsWith(")");
    }

    private String divRowStart() {
        return String.format("""
            <div class="yaml-row yaml-row-%d" style="white-space: pre">""",
            totalRowCount++
        );
    }

    private String divRowEnd() {
        return "</div>";
    }

    private String indent() {
        return "  ".repeat(currentPath.size());
    }

    private String dashSpace() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.YAML, "- ");
    }

    private String colon() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.YAML, ':');
    }

    private String emptySequence() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.YAML, " []");
    }

    private String emptyMappings() {
        return renderSpec.highlightSyntaxHtmlStyle(Highlight.Structure.YAML, " {}");
    }
}
