package org.krmdemo.techlabs.dump.render;

public enum OuterHtml {

    FRAGMENT {
        @Override
        public String outerHtmlStyle(String innerHtml, String title, String backgroundStyle) {
            return String.format("""
                <div style="font-family: monospace; font-size: 14px; %s">
                %s</div>""",
                backgroundStyle,
                innerHtml.indent(2)
            );
        }
        @Override
        public String outerHtmlCss(String innerHtml, String title, String backgroundCss) {
            return String.format("""
                <div style="font-family: monospace; font-size: 14px;" class="%s">
                %s</div>""",
                backgroundCss,
                innerHtml.indent(2)
            );
        }
    },

    DOCUMENT {
        @Override
        public String outerHtmlStyle(String innerHtml, String title, String backgroundStyle) {
            return String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <title>%s</title>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                </head>
                <body>
                %s
                </body>
                </html>""",
                title,
                FRAGMENT.outerHtmlStyle(innerHtml, title, backgroundStyle)
            );
        }
        @Override
        public String outerHtmlCss(String innerHtml, String title, String backgroundCss) {
            throw new UnsupportedOperationException("not implemented yet !!!");
        }
    };

    public abstract String outerHtmlStyle(String innerHtml, String title, String backgroundStyle);

    public abstract String outerHtmlCss(String innerHtml, String title, String backgroundCss);
}
