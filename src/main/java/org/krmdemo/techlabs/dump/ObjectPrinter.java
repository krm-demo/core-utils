package org.krmdemo.techlabs.dump;

import org.krmdemo.techlabs.dump.json.JsonHtmlDumper;
import org.krmdemo.techlabs.dump.json.JsonSvgDumper;
import org.krmdemo.techlabs.dump.json.JsonTxtDumper;
import org.krmdemo.techlabs.dump.render.Highlight;
import org.krmdemo.techlabs.dump.render.RenderSpec;
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

    void printAsJsonTxt(Object objToPrint, RenderSpec renderSpec);

    void printAsJsonHtml(Object objToPrint, RenderSpec renderSpec);

    void printAsJsonSvg(Object objToPrint, RenderSpec renderSpec);

    void printAsYamlTxt(Object objToPrint, RenderSpec renderSpec);

    void printAsYamlHtml(Object objToPrint, RenderSpec renderSpec);

    void printAsYamlSvg(Object objToPrint, RenderSpec renderSpec);

    default void printAsJsonTxt(Object objToPrint) {
        printAsJsonTxt(objToPrint, new RenderSpec(Highlight.NONE));  // <-- TODO: think about passing "Highlight.DEFAULT"
    }

    default void printAsJsonHtml(Object objToPrint) {
        printAsJsonHtml(objToPrint, new RenderSpec(Highlight.NONE));
    }

    default void printAsJsonSvg(Object objToPrint) {
        printAsJsonSvg(objToPrint, new RenderSpec(Highlight.NONE));
    }

    default void printAsYamlTxt(Object objToPrint) {
        printAsYamlTxt(objToPrint, new RenderSpec(Highlight.NONE));  // <-- TODO: think about passing "Highlight.DEFAULT"
    }

    default void printAsYamlHtml(Object objToPrint) {
        printAsYamlHtml(objToPrint, new RenderSpec(Highlight.NONE));
    }

    default void printAsYamlSvg(Object objToPrint) {
        printAsYamlSvg(objToPrint, new RenderSpec(Highlight.NONE));
    }

    record UnitOp(
        Function<Object, TreeDumper.Node> dumperNodeFunc,
        BiFunction<PrintStream, RenderSpec, TreeDumper> dumperFactory
    ) {
        void printStdOut(Object objToPrint, RenderSpec renderSpec) {
            print(System.out, objToPrint, renderSpec);
        }
        void print(PrintStream out, Object objToPrint, RenderSpec renderSpec) {
            TreeDumper.Node root = dumperNodeFunc.apply(objToPrint);
            TreeDumper dumper = dumperFactory.apply(out, renderSpec);
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
        public void printAsJsonTxt(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonTxtDumper::new);
            unitOp.printStdOut(objToPrint, renderSpec);
        }

        @Override
        public void printAsJsonHtml(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonHtmlDumper::new);
            unitOp.printStdOut(objToPrint, renderSpec);
        }

        @Override
        public void printAsJsonSvg(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonSvgDumper::new);
            unitOp.printStdOut(objToPrint, renderSpec);
        }

        @Override
        public void printAsYamlTxt(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlTxtDumper::new);
            unitOp.printStdOut(objToPrint, renderSpec);
        }

        @Override
        public void printAsYamlHtml(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlHtmlDumper::new);
            unitOp.printStdOut(objToPrint, renderSpec);
        }

        @Override
        public void printAsYamlSvg(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlSvgDumper::new);
            unitOp.printStdOut(objToPrint, renderSpec);
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
        public void printAsJsonTxt(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonTxtDumper::new);
            unitOp.print(this.out, objToPrint, renderSpec);
        }

        @Override
        public void printAsJsonHtml(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonHtmlDumper::new);
            unitOp.print(this.out, objToPrint, renderSpec);
        }

        @Override
        public void printAsJsonSvg(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonSvgDumper::new);
            unitOp.print(this.out, objToPrint, renderSpec);
        }

        @Override
        public void printAsYamlTxt(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlTxtDumper::new);
            unitOp.print(this.out, objToPrint, renderSpec);
        }

        @Override
        public void printAsYamlHtml(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlHtmlDumper::new);
            unitOp.print(this.out, objToPrint, renderSpec);
        }

        @Override
        public void printAsYamlSvg(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlSvgDumper::new);
            unitOp.print(this.out, objToPrint, renderSpec);
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
        public void printAsJsonTxt(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonTxtDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, renderSpec);
        }

        @Override
        public void printAsJsonHtml(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonHtmlDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, renderSpec);
        }

        @Override
        public void printAsJsonSvg(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, JsonSvgDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, renderSpec);
        }

        @Override
        public void printAsYamlTxt(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlTxtDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, renderSpec);
        }

        @Override
        public void printAsYamlHtml(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlHtmlDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, renderSpec);
        }

        @Override
        public void printAsYamlSvg(Object objToPrint, RenderSpec renderSpec) {
            UnitOp unitOp = new UnitOp(dumperNodeFunc, YamlSvgDumper::new);
            unitOp.print(this.filePrintStream(), objToPrint, renderSpec);
        }
    }
}
