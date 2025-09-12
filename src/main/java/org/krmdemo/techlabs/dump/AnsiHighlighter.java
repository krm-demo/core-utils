package org.krmdemo.techlabs.dump;

import picocli.CommandLine.Help.Ansi;

public interface AnsiHighlighter extends Highlighter {

    AnsiHighlighter DEFAULT = new AnsiHighlighter() {
        @Override
        public String syntaxJson(String syntaxSymbol) {
            return Ansi.AUTO.string("@|white " + syntaxSymbol + "|@");
        }
        @Override
        public String highlightKey(String keyStr) {
            return Ansi.AUTO.string("@|blue " + keyStr + "|@");
        }
        @Override
        public String highlightValue(String valueStr) {
            return Ansi.AUTO.string("@|green " + valueStr + "|@");
        }
    };
}
