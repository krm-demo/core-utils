package org.krmdemo.techlabs.dump.yaml;

import org.krmdemo.techlabs.dump.TreeDumper;
import org.krmdemo.techlabs.dump.render.Highlighter;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class YamlTxtDumper implements TreeDumper {

    final PrintStream out;
    final Highlighter highlighter;
    final Deque<String> currentPath = new ArrayDeque<>();

    public YamlTxtDumper(PrintStream out, Highlighter highlighter) {
        this.out = Objects.requireNonNull(out);
        this.highlighter = Objects.requireNonNull(highlighter);
    }

    @Override
    public void acceptNull() {
        // TODO: implement YamlTxtDumper.acceptNull()
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void acceptScalar(ScalarNode scalarNode) {
        // TODO: implement YamlTxtDumper.acceptScalar()
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void acceptSequence(SequenceNode sequenceNode) {
        // TODO: implement YamlTxtDumper.acceptSequence()
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public void acceptMappings(MappingsNode mappingsNode) {
        // TODO: implement YamlTxtDumper.acceptMappings()
        throw new UnsupportedOperationException("not implemented yet");
    }
}
