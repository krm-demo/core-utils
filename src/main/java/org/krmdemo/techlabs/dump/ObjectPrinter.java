package org.krmdemo.techlabs.dump;

import org.krmdemo.techlabs.dump.json.JsonHtmlDumper;
import org.krmdemo.techlabs.dump.json.JsonSvgDumper;
import org.krmdemo.techlabs.dump.json.JsonTxtDumper;
import org.krmdemo.techlabs.dump.render.Highlighter;
import org.krmdemo.techlabs.dump.yaml.YamlHtmlDumper;
import org.krmdemo.techlabs.dump.yaml.YamlSvgDumper;
import org.krmdemo.techlabs.dump.yaml.YamlTxtDumper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.krmdemo.techlabs.dump.JacksonTree.DEFAULT_JACKSON_TREE;

/**
 * Interface to represent any Java-Object in most popular hierarchical structures (JSON and YAML)
 * and with suitable output formats (TXT, HTML, SVG) with a flexible way to control the highlighting of elements.
 * Together with 3 most popular Java-API approaches (String, Std-Out and File) we have a multiple ways
 * and a multiple API-methods that the current interface exposes.
 * <hr/>
 * It's also recommended to use methods from corresponding utility-classes.
 *
 * @see PrintUtils for standard output {@link System#out}
 * @see DumpUtils for output just as {@link String}
 * @see ToFileUtils for output into target {@link File}
 */
public interface ObjectPrinter {

    /**
     * Default instance of {@link StdOut} that uses the default {@link JacksonTree}
     */
    StdOut DEFAULT_STD_OUT = new StdOut();

    void printAsJsonTxt(Object objToPrint, Highlighter highlighter);

    void printAsJsonHtml(Object objToPrint, Highlighter highlighter);

    void printAsJsonSvg(Object objToPrint, Highlighter highlighter);

    void printAsYamlTxt(Object objToPrint, Highlighter highlighter);

    void printAsYamlHtml(Object objToPrint, Highlighter highlighter);

    void printAsYamlSvg(Object objToPrint, Highlighter highlighter);

    default void printAsJsonTxt(Object objToPrint) {
        printAsJsonTxt(objToPrint, Highlighter.NONE);  // <-- TODO: think about passing "AnsiHighlighter.DEFAULT"
    }

    default void printAsJsonHtml(Object objToPrint) {
        printAsJsonHtml(objToPrint, Highlighter.NONE);
    }

    default void printAsJsonSvg(Object objToPrint) {
        printAsJsonSvg(objToPrint, Highlighter.NONE);
    }

    default void printAsYamlTxt(Object objToPrint) {
        printAsYamlTxt(objToPrint, Highlighter.NONE);  // <-- TODO: think about passing "AnsiHighlighter.DEFAULT"
    }

    default void printAsYamlHtml(Object objToPrint) {
        printAsYamlHtml(objToPrint, Highlighter.NONE);
    }

    default void printAsYamlSvg(Object objToPrint) {
        printAsYamlSvg(objToPrint, Highlighter.NONE);
    }

    record UnitOp(
        Function<Object, TreeDumper.Node> dumperNodeFunc,
        BiFunction<PrintStream, Highlighter, TreeDumper> dumperFactory
    ) {
        void printStdOut(Object objToPrint, Highlighter highlighter) {
            print(System.out, objToPrint, highlighter);
        }
        void print(PrintStream out, Object objToPrint, Highlighter highlighter) {
            TreeDumper.Node root = dumperNodeFunc.apply(objToPrint);
            TreeDumper dumper = dumperFactory.apply(out, highlighter);
            dumper.acceptRoot(root);
        }
    }

    /**
     *
     */
    class StdOut implements ObjectPrinter {
        private final Function<Object, TreeDumper.Node> dumperNodeFunc;

        public StdOut() {
            this(DEFAULT_JACKSON_TREE::dumperNode);
        }

        public StdOut(Function<Object, TreeDumper.Node> dumperNodeFunc) {
            this.dumperNodeFunc = dumperNodeFunc;
        }

        @Override
        public void printAsJsonTxt(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonTxtDumper::new);
            unitOp.printStdOut(objToPrint, highlighter);
        }

        @Override
        public void printAsJsonHtml(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonHtmlDumper::new);
            unitOp.printStdOut(objToPrint, highlighter);
        }

        @Override
        public void printAsJsonSvg(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonSvgDumper::new);
            unitOp.printStdOut(objToPrint, highlighter);
        }

        @Override
        public void printAsYamlTxt(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlTxtDumper::new);
            unitOp.printStdOut(objToPrint, highlighter);
        }

        @Override
        public void printAsYamlHtml(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlHtmlDumper::new);
            unitOp.printStdOut(objToPrint, highlighter);
        }

        @Override
        public void printAsYamlSvg(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlSvgDumper::new);
            unitOp.printStdOut(objToPrint, highlighter);
        }
    }

    /**
     *
     */
    class ToString implements ObjectPrinter {
        private final StringBuilderOut out = StringBuilderOut.create();
        private final Function<Object, TreeDumper.Node> dumperNodeFunc;

        public ToString() {
            this(DEFAULT_JACKSON_TREE::dumperNode);
        }

        public ToString(Function<Object, TreeDumper.Node> dumperNodeFunc) {
            this.dumperNodeFunc = dumperNodeFunc;
        }

        public StringBuilder stringBuilder() {
            return this.out.stringBuilder();
        }

        @Override
        public String toString() {
            return this.out.toString();
        }

        @Override
        public void printAsJsonTxt(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonTxtDumper::new);
            unitOp.print(this.out, objToPrint, highlighter);
        }

        @Override
        public void printAsJsonHtml(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonHtmlDumper::new);
            unitOp.print(this.out, objToPrint, highlighter);
        }

        @Override
        public void printAsJsonSvg(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonSvgDumper::new);
            unitOp.print(this.out, objToPrint, highlighter);
        }

        @Override
        public void printAsYamlTxt(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlTxtDumper::new);
            unitOp.print(this.out, objToPrint, highlighter);
        }

        @Override
        public void printAsYamlHtml(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlHtmlDumper::new);
            unitOp.print(this.out, objToPrint, highlighter);
        }

        @Override
        public void printAsYamlSvg(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlSvgDumper::new);
            unitOp.print(this.out, objToPrint, highlighter);
        }
    }

    /**
     *
     */
    class ToFile implements ObjectPrinter {
        private final File file;
        private final Function<Object, TreeDumper.Node> dumperNodeFunc;

        public ToFile(File file) {
            this(file, DEFAULT_JACKSON_TREE::dumperNode);
        }

        public ToFile(File file, Function<Object, TreeDumper.Node> dumperNodeFunc) {
            this.file = file;
            this.dumperNodeFunc = dumperNodeFunc;
        }

        PrintStream filePrintStream() {
            try {
                return new PrintStream(new FileOutputStream(file));
            } catch (IOException ioEx) {
                throw new IllegalStateException("could not print to file " + file, ioEx);
            }
        }

        @Override
        public void printAsJsonTxt(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonTxtDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, highlighter);
        }

        @Override
        public void printAsJsonHtml(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonHtmlDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, highlighter);
        }

        @Override
        public void printAsJsonSvg(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonSvgDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, highlighter);
        }

        @Override
        public void printAsYamlTxt(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlTxtDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, highlighter);
        }

        @Override
        public void printAsYamlHtml(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlHtmlDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, highlighter);
        }

        @Override
        public void printAsYamlSvg(Object objToPrint, Highlighter highlighter) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlSvgDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, highlighter);
        }
    }
}
