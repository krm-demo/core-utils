package org.krmdemo.techlabs.core.dump.yaml;

import org.krmdemo.techlabs.core.dump.StringBuilderOut;
import org.krmdemo.techlabs.core.dump.TreeDumper;
import org.krmdemo.techlabs.core.dump.render.Highlight;
import org.krmdemo.techlabs.core.dump.render.RenderSpec;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of {@link TreeDumper} that dumps into the passed {@link PrintStream}
 * the tree of a {@link Highlight.Structure#YAML YAML} logical structure
 * in {@link Highlight.Target#SVG_ATTRS SVG-Attrs} target format (SVG-Class is coming soon).
 */
public class YamlSvgDumper implements TreeDumper {

    final PrintStream out;
    final RenderSpec renderSpec;
    final Deque<String> currentPath = new ArrayDeque<>();

    int totalRowCount = 0;
    int currentRowLen = 0;
    int maxRowLen = 0;

    public YamlSvgDumper(PrintStream out, RenderSpec renderSpec) {
        this.out = Objects.requireNonNull(out);
        this.renderSpec = Objects.requireNonNull(renderSpec);
    }

    @Override
    public void acceptRoot(Node rootNode) {
        PrintStream innerOut = StringBuilderOut.create();
        YamlSvgDumper innerDumper = new YamlSvgDumper(innerOut, renderSpec);
        rootNode.visit(innerDumper);
        System.out.println("innerDumper.maxRowLen = " + innerDumper.maxRowLen);
        out.println(renderSpec.dumpOuterSvg(
            innerOut.toString(), Highlight.Structure.YAML,
            14, innerDumper.maxRowLen, innerDumper.totalRowCount));
    }

    private void addWidth(int delta) {
        this.currentRowLen += delta;
        this.maxRowLen = Math.max(this.maxRowLen, this.currentRowLen);
    }

    private void addIndentWidth() {
        addWidth(2 * currentPath.size());
    }

    @Override
    public void acceptNull() {
        if (currentPath.isEmpty()) {
            out.print(svgRowStart());
        }
        if (parentIsMapping()) {
            out.print(' ');
            addWidth(1);
        }
        out.print(renderSpec.highlightNullSvgAttrs(Highlight.Structure.YAML));
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
        if (parentIsMapping()) {
            out.print(' ');
            addWidth(1);
        }
        out.print(renderSpec.highlightValueSvgAttrs(Highlight.Structure.YAML, scalarNode.text()));
        addWidth(scalarNode.text().length()); // <-- value
        if (currentPath.isEmpty()) {
            out.print(svgRowEnd());
        }
    }

    @Override
    public void acceptSequence(SequenceNode sequenceNode) {
        if (currentPath.isEmpty()) {
            out.print(svgRowStart());
        }
        int count = 0;
        Iterator<Node> itNode = sequenceNode.sequenceItems().iterator();
        while (itNode.hasNext()) {
            if (count > 0 || parentIsMapping()) {
                out.println(svgRowEnd());
                out.print(svgRowStart());
                out.print(indent());
                addIndentWidth();
            }
            out.print(dashSpace());  // <-- no effect on max-width
            addWidth(2);
            count++;
            currentPath.addLast(String.format("[%d]", count));
            itNode.next().visit(this);
            currentPath.removeLast();
        }
        if (count == 0) {
            out.print(emptySequence());
            addWidth(3);
        }
        if (currentPath.isEmpty()) {
            out.print(svgRowEnd());
        }
    }

    @Override
    public void acceptMappings(MappingsNode mappingsNode) {
        if (currentPath.isEmpty()) {
            out.print(svgRowStart());
        }
        int count = 0;
        Iterator<Map.Entry<String, Node>> itEntry = mappingsNode.mappingsItems().iterator();
        while (itEntry.hasNext()) {
            if (count > 0 || parentIsMapping()) {
                out.println(svgRowEnd());
                out.print(svgRowStart());
                out.print(indent());
                addIndentWidth();
            }
            count++;
            Map.Entry<String, Node> entry = itEntry.next();
            currentPath.addLast(String.format("(%s)", entry.getKey()));
            out.print(renderSpec.highlightKeySvgAttrs(Highlight.Structure.YAML, entry.getKey()));
            out.print(colon());
            addWidth(1 + entry.getKey().length());
            entry.getValue().visit(this);
            currentPath.removeLast();
        }
        if (count == 0) {
            out.print(emptyMappings());
            addWidth(3);
        }
        if (currentPath.isEmpty()) {
            out.print(svgRowEnd());
        }
    }

    private boolean parentIsMapping() {
        return !currentPath.isEmpty() && currentPath.getLast().endsWith(")");
    }

    private String svgRowStart() {
        return String.format("""
            <tspan class="yaml-svg-row yaml-svg-row-%d" x="0" dy="1.2em">""",
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

    private String dashSpace() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.YAML, "- ");
    }

    private String colon() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.YAML, ':');
    }

    private String emptySequence() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.YAML, " []");
    }

    private String emptyMappings() {
        return renderSpec.highlightSyntaxSvgAttrs(Highlight.Structure.YAML, " {}");
    }
}
