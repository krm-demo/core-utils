package org.krmdemo.techlabs.dump.render;

import java.util.HashMap;
import java.util.Map;

public abstract class RenderConfig<H extends Highlighter> {

    final H highlighter;
    final Map<String, Object> renderProps = new HashMap<>();

    protected RenderConfig(H highlighter) {
        this.highlighter = highlighter;
    }
}
