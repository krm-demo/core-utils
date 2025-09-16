package org.krmdemo.techlabs.dump.render;

/**
 *
 * @see <a href="https://www.google.com/search?q=Ansi+color+correspondence+to+HTML+color&sca_esv=dad87895c19efbc5&sxsrf=AE3TifN_jDYLtL-6wOSi6IueAs-ViDdZMQ%3A1757982148110&ei=xK3IaJDHBr_dptQPnsHZUA&ved=2ahUKEwjnorqhhNyPAxUoLFkFHXJIOuQQ0NsOegQIAxAB&uact=5&sclient=gws-wiz-serp&udm=50&fbs=AIIjpHxU7SXXniUZfeShr2fp4giZ1Y6MJ25_tmWITc7uy4KIeoJTKjrFjVxydQWqI2NcOha3O1YqG67F0QIhAOFN_ob1yXos5K_Qo9Tq-0cVPzex8akBC0YDCZ6Kdb3tXvKc6RFFaJZ5G23Reu3aSyxvn2qD41n-47oj-b-f0NcRPP5lz0IcnVzj2DIj_DMpoDz5XbfZAMcEl5-58jjbkgCC_7e4L5AEDQ&aep=10&ntc=1&mtid=OrLIaIP0OvHR5NoP0I_f6Ao&mstk=AUtExfATE4-cPysot3tSLSM6w6SCNf4XGEOt0J0wMfe0eUnu8V9D1GEttG2ZMfVNdzBNI-1lrl70F37M_JmQJ-1bAp96eo95YwYvy4HpxFs9dQ-ufdH8tx9CXG0onw8tG3T7vyCxWYsBCh6H6eVA9-SOqSnJiQrU3WT2Frdj1F4-XYy-IlshRyKz_XOaEwtC6lTx7JWoms6oIiBHSkMmKCLodyl8BwgKVgwhP9ssFpfiwMZKXRx7noSopA8GxUpaZF-tcqr3cjHMQc7Zk9AEvghcfYUVzrB_u81dcqjlmDs0Bbiv-NC9OaGkGBI2Q9-nM9AOEOl6XvPMW_fo5Q&csuir=1">
 *     (Google Gemini) Ansi color correspondence to HTML color
 * </a>
 */
public interface HtmlHighlighter extends Highlighter {

    default String htmlTitile() {
        return "Sample HTML-Content of " + getClass().getSimpleName();
    }

    default String htmlContent(String htmlDump) {
        return String.format("""
        <!DOCTYPE html>
        <html>
          <head>
            <title>%s</title>
            <meta charset="utf-8">
          </head>
          <body>
            <div style="font-family: monospace; font-size: 18px;">
              %s
            </div>
          </body
        </html>
        """, htmlTitile(), htmlDump.indent(6));
    }

    default String spanWithStyleColor(String color, String innerHTML) {
        return String.format("""
            <span style="color: %s">%s</span>""",
            color, innerHTML);
    }

    default String spanWithCssClass(String cssClass, String innerHTML) {
        return String.format("""
            <span class="%s">%s</span>""",
            cssClass, innerHTML);
    }

    HtmlHighlighter DEFAULT = new HtmlHighlighter() {
        @Override
        public String syntaxJson(String syntaxSymbol) {
            return spanWithStyleColor("#C0C0C0", syntaxSymbol);  // <-- @|white ...|@
        }
        @Override
        public String highlightNull() {
            return spanWithStyleColor("#800080", "null");  // <-- @|magenta ...|@
        }
        @Override
        public String highlightKey(String keyStr) {
            return spanWithStyleColor("#000080", keyStr);  // <-- @|blue ...|@
        }
        @Override
        public String highlightValue(String valueStr) {
            return spanWithStyleColor("#008000", valueStr);  // <-- @|green ...|@
        }
    };
}
