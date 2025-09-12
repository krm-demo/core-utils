package org.krmdemo.techlabs.dump;

public interface Highlighter {

    String syntaxJson(String syntaxSymbol);
    String highlightKey(String keyStr);
    String highlightValue(String valueStr);

    default String highlightNull() {
        return "null";
    }

    default String syntaxJson(char syntaxSymbolChar) {
        return syntaxJson(String.valueOf(syntaxSymbolChar));
    }

    Highlighter NONE = new Highlighter() {
        @Override
        public String syntaxJson(String syntaxSymbol) {
            return syntaxSymbol;
        }
        @Override
        public String highlightKey(String keyStr) {
            return keyStr;
        }
        @Override
        public String highlightValue(String valueStr) {
            return valueStr;
        }
    };
}
