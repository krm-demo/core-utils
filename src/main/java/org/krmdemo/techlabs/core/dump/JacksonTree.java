package org.krmdemo.techlabs.core.dump;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;

/**
 * A static-factory that provides an implementation of of {@link TreeDumper.Node}
 * over {@link JsonNode}, which is the base data-unit that is provided by
 * <a href="https://github.com/FasterXML/jackson?tab=readme-ov-file">Jackson-library</a>.
 * <hr/>
 * Utility-classes {@link DumpUtils}, {@link PrintUtils} and {@link ToFileUtils}
 * use the default static instance of this class - {@link #DEFAULT_JACKSON_TREE} via {@link ObjectPrinter.UnitOp}.
 */
public class JacksonTree {

    public static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    static final JacksonTree DEFAULT_JACKSON_TREE = new JacksonTree(DEFAULT_MAPPER);

    final ObjectMapper jacksonMapper;

    /**
     * An optional public constructor that allows to provide an instance of {@link ObjectMapper Jackson-ObjectMapper}
     * that is different from the default one - {@link #DEFAULT_MAPPER}.
     *
     * @param jacksonMapper an instance of {@link ObjectMapper Jackson-ObjectMapper}
     */
    public JacksonTree(ObjectMapper jacksonMapper) {
        this.jacksonMapper = jacksonMapper;
    }

    /**
     * An entry-point of this static-factory, that is used as singleton
     * over the predefined static instance of {@link ObjectMapper} - {@link #DEFAULT_MAPPER}.
     * <hr/>
     * In order to customize the default behavior you could either
     * register additional {@link Module Jackson-ObjectMapper-Module}s
     * or provide your own instance of {@link ObjectMapper Jackson-ObjectMapper}
     *
     * @param fromValue the object to build the Jackson-Tree from as an instance of {@link JsonNode Jackson-Tree-Node}
     * @return the instance of {@link TreeDumper.Node}, which repeats the hierarchy of {@link JsonNode Jackson-Tree-Node}
     */
    public TreeDumper.Node dumperNode(Object fromValue) {
        if (fromValue == null) {
            return TreeDumper.NULL;
        } else if (fromValue instanceof TreeDumper.Node dumperNode) {
            return dumperNode;
        }
        JsonNode root = jacksonMapper.valueToTree(fromValue);
        return fromJsonNode(root);
    }

    /**
     * A factory-method that returns an implementation of {@link TreeDumper.Node}
     * over {@link JsonNode}, which is the base data-unit that is provided by
     * <a href="https://github.com/FasterXML/jackson?tab=readme-ov-file">Jackson-library</a>
     *
     * @param jsonNode an instance of {@link JsonNode}
     * @return a proper implementation of {@link TreeDumper.Node}
     */
    public static TreeDumper.Node fromJsonNode(JsonNode jsonNode) {
        if (jsonNode == null) {
            return TreeDumper.NULL;
        }
        return switch (jsonNode.getNodeType()) {
            case NULL -> TreeDumper.NULL;
            case ARRAY -> new SequenceNode(jsonNode);
            case OBJECT, POJO -> new MappingsNode(jsonNode);
            default -> new ScalarNode(jsonNode);
        };
    }


    abstract static class JacksonNode implements TreeDumper.Node {
        protected final JsonNode jsonNode;
        protected JacksonNode(JsonNode jsonNode) {
            this.jsonNode = Objects.requireNonNull(jsonNode);
        }
    }

    static class ScalarNode extends JacksonNode implements TreeDumper.ScalarNode {
        protected ScalarNode (JsonNode jsonNode) {
            super(jsonNode);
        }
        @Override
        public String text() {
            return jsonNode.asText();
        }
        @Override
        public void visit(TreeDumper dumper) {
            dumper.acceptScalar(this);
        }
    }

    static class SequenceNode extends JacksonNode implements TreeDumper.SequenceNode {
        protected SequenceNode (JsonNode jsonNode) {
            super(jsonNode);
        }
        @Override
        public Stream<TreeDumper.Node> sequenceItems() {
            return IntStream.range(0, jsonNode.size())
                .mapToObj(jsonNode::get)
                .map(JacksonTree::fromJsonNode);
        }
        @Override
        public void visit(TreeDumper dumper) {
            dumper.acceptSequence(this);
        }
    }

    static class MappingsNode extends JacksonNode implements TreeDumper.MappingsNode {
        private final Set<String> fieldNames = new LinkedHashSet<>();
        protected MappingsNode (JsonNode jsonNode) {
            super(jsonNode);
            jsonNode.fieldNames().forEachRemaining(fieldNames::add);
        }
        @Override
        public Stream<Map.Entry<String, TreeDumper.Node>> mappingsItems() {
            return fieldNames.stream().map(name -> {
                JsonNode fieldNode = jsonNode.get(name);
                return nameValue(name, fromJsonNode(fieldNode));
            });
        }
        @Override
        public void visit(TreeDumper dumper) {
            dumper.acceptMappings(this);
        }
    }
}
