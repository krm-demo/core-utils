package org.krmdemo.techlabs.core.dump;

import java.util.Map;
import java.util.stream.Stream;

/**
 * This interface represents the ability to dump the data of JSON-like and YAML-like
 * hierarchical structure in multicolor TEXT-base, HTML-base and SVG-base formats
 *
 * @see <a href="https://en.wikipedia.org/wiki/Visitor_pattern">(Wikipedia) Visitor pattern</a>
 * @see <a href="https://www.baeldung.com/java-visitor-pattern">
 *     (Baeldung) Visitor Design Pattern in Java
 * </a>
 * @see <a href="https://www.geeksforgeeks.org/system-design/visitor-design-pattern/">
 *     (Geeks For Geeks) Visitor design pattern
 * </a>
 * @see <a href="https://www.digitalocean.com/community/tutorials/visitor-design-pattern-java">
 *     (Digital Ocean) Visitor Design Pattern in Java
 * </a>
 */
public interface TreeDumper {

    interface Node {
        default String identity() { return null; }
        default String comment() { return null; }
        void visit(TreeDumper dumper);
    }

    interface ScalarNode extends Node {
        String text();
    }

    interface SequenceNode extends Node {
        Stream<Node> sequenceItems();
    }

    interface MappingsNode extends Node {
        Stream<Map.Entry<String, Node>> mappingsItems();
    }

    Node NULL = TreeDumper::acceptNull;

    void acceptNull();
    void acceptScalar(ScalarNode scalarNode);
    void acceptSequence(SequenceNode sequenceNode);
    void acceptMappings(MappingsNode mappingsNode);

    default void acceptRoot(Node rootNode) {
        rootNode.visit(this);
    }
}
