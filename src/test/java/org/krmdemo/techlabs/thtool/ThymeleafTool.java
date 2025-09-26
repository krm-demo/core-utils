package org.krmdemo.techlabs.thtool;

import org.krmdemo.techlabs.core.utils.SysDumpUtils;
import org.thymeleaf.TemplateEngine;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.io.File;

/**
 * This class represents <b>{@code th-tool}</b> that could be used to process {@code *.md.th}, {@code *.html.th}
 * and other <a href="https://www.thymeleaf.org/">Thymeleaf</a>-templates
 * in the current project during the build and preparing the documentation for releases.
 * <hr/>
 * This is an internal version that is available in the current project only (mostly via maven-exec plugin)
 *
 * @see <a href="https://picocli.info/">
 *     <b>picocli</b> - a mighty tiny command line interface
 * </a>
 * @see <a href="https://aragost.com/blog/java/picocli-subcommands/">
 *     <b>picocli</b> subcommands
 * </a>
 * @see <a href="https://www.mojohaus.org/exec-maven-plugin/">
 *     Exec <b>Maven Plugin</b>
 * </a>
 *
 * @see <a href="https://belief-driven-design.com/thymeleaf-part-1-basics-3a1d9/">
 *     Templating with <b>Thymeleaf</b>: The Basics (Part 1)
 * </a>
 * @see <a href="https://belief-driven-design.com/thymeleaf-part-2-fragments-reusability-b2e61/">
 *     Templating with <b>Thymeleaf</b>: Fragments and Reusability (Part 2)
 * </a>
 * @see <a href="https://belief-driven-design.com/thymeleaf-part-3-custom-dialects-0beee/">
 *     Templating with <b>Thymeleaf</b>: Custom Dialects and More (Part 3)
 * </a>
 */
@Command(name = "th-tool",
    version = "0.0.1", // <-- TODO: inject the version from maven properties via "versionProvider"
    subcommands = { ThymeleafToolEval.class, ThymeleafToolProc.class },
    description = "A tool to process the input-templates and evaluate the expressions with Thymeleaf",
    mixinStandardHelpOptions = true, usageHelpWidth = 140
)
public class ThymeleafTool {

    @Option(
        names = {"--vars-dir"},
        paramLabel = "<path>",
        defaultValue = ".github/th-vars",
        description = """
            directory with files like '@|bold,yellow var-|@@|blue XXX|@@|bold,yellow .json|@' and '@|bold,yellow var-|@@|blue YYY|@@|bold,yellow .properties|@',
            where @|blue XXX|@ and @|blue YYY|@ are going to be the names of corresponding 'th-tool' variables
            (default: '@|white,bold ${DEFAULT-VALUE}|@').""",
        required = true)
    File varsDir;

    @Option(
        names = {"--var-file"},
        paramLabel = "name=path",
        description = """
            a pair of variable name and a path to the file with either '@|bold,yellow .json|@' or '@|bold,yellow .properties|@' extension,
            which should be provided in format '@|blue varName|@@|yellow,bold =|@<@|white,bold path-to-file/without/extension|@>@|bold,yellow .json|@'
            or '@|blue varName|@@|yellow,bold =|@<@|white,bold path-to-file/without/extension|@>@|bold,yellow .json|@'.""")
    String[] varFilePairs;

    @Option(
        names = {"--var-res"},
        paramLabel = "name=path",
        description = """
            a pair of variable name and a classpath-resource with either '@|yellow,bold .json|@' or '@|yellow,bold .properties|@' extension,
            which should be provided in format '@|blue varName|@@|yellow,bold =|@<@|white,bold path-to-resource/without/extension|@>@|yellow,bold .json|@'
            or '@|blue varName|@@|yellow,bold =|@<@|white,bold path-to-resource/without/extension|@>@|yellow,bold .json|@'.""")
    String[] varResPairs;

    @Option(names = {"--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;

    @Spec
    CommandSpec spec; // Injected by picocli

    /**
     * An instance of {@link TemplateEngine Thymeleaf's Template Engine}
     */
    final TemplateEngine templateEngine = new TemplateEngine();

    /**
     * An instance of {@link TemplateEngine Thymeleaf's Context} that holds the variables for templates
     */
    final ThymeleafToolCtx varsCtx = new ThymeleafToolCtx();

    /**
     * JVM entry-point of <b>{@code th-tool}</b>
     *
     * @param args command-line arguments that will be processed by PicoCli-framework
     *
     * @see <a href="https://foojay.io/today/creating-a-command-line-tool-with-jbang-and-picocli-to-generate-release-notes/">
     *     Creating a Command Line Tool with JBang and PicoCLI to Generate Release Notes
     * </a>
     */
    public static void main(String... args) {
        // TODO: try to introduce and integrate a logger like it's demonstrated at "https://foojay.io/..."
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(SysDumpUtils.dumpEnvVarsExAsJson());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(SysDumpUtils.dumpSysPropsExAsJson());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");

//        CommandLine.Help.Ansi ansiMode = CommandLine.Help.Ansi.ON;
//        new CommandLine.Help.Ansi(ansiMode).out;
//        int exitCode = new CommandLine(new ThymeleafTool()).execute(new CommandLine.Help.Ansi(ansiMode).out, args);

        int exitCode = new CommandLine(new ThymeleafTool()).execute(args);
        System.exit(exitCode);
    }
}
