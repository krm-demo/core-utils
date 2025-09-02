package org.krmdemo.techlabs.thtool;

import org.apache.commons.lang3.ArrayUtils;
import org.krmdemo.techlabs.json.JacksonUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedSet;

/**
 * This class is an entry-point of <b>{@code th-tool}</b>
 * that is used to process {@code *.md.th} and {@code *.html.th}
 * and other <a href="https://www.thymeleaf.org/">Thymeleaf</a>-templates
 * in the current project during the build and preparing the documentation for releases.
 *
 * @see <a href="https://belief-driven-design.com/thymeleaf-part-1-basics-3a1d9/">
 *     Templating with Thymeleaf: The Basics (Part 1)
 * </a>
 * @see <a href="https://belief-driven-design.com/thymeleaf-part-2-fragments-reusability-b2e61/">
 *     Templating with Thymeleaf: Fragments and Reusability (Part 2)
 * </a>
 * @see <a href="https://belief-driven-design.com/thymeleaf-part-3-custom-dialects-0beee/">
 *     Templating with Thymeleaf: Custom Dialects and More (Part 3)
 * </a>
 */
@Command(name = "th-tool", version = "0.0.1",
    description = "A tool to process the input-templates with Thymeleaf",
    mixinStandardHelpOptions = true, usageHelpWidth = 140
)
public class ThymeleafTool implements Callable<Integer> {

    @Option(
        names = {"--vars-dir"},
        paramLabel = "<path>",
        defaultValue = ".github/th-vars",
        description = """
            directory with files like '@|bold,yellow var-|@@|blue XXX|@@|bold,yellow .json|@' and '@|bold,yellow var-|@@|blue YYY|@@|bold,yellow .properties|@',
            where @|blue XXX|@ and @|blue YYY|@ are going to be the names of corresponding 'th-tool' variables
            (default: '@|white,bold ${DEFAULT-VALUE}|@').""",
        required = true)
    private File varsDir;

    @Option(
        names = {"--var-file"},
        paramLabel = "name=path",
        description = """
            a pair of variable name and a path to the file with either '@|bold,yellow .json|@' or '@|bold,yellow .properties|@' extension,
            which should be provided in format '@|blue varName|@@|yellow,bold =|@<@|white,bold path-to-file/without/extension|@>@|bold,yellow .json|@'
            or '@|blue varName|@@|yellow,bold =|@<@|white,bold path-to-file/without/extension|@>@|bold,yellow .json|@'.""")
    private String[] varFilePairs;

    @Option(
        names = {"--var-res"},
        paramLabel = "name=path",
        description = """
            a pair of variable name and a classpath-resource with either '@|yellow,bold .json|@' or '@|yellow,bold .properties|@' extension,
            which should be provided in format '@|blue varName|@@|yellow,bold =|@<@|white,bold path-to-resource/without/extension|@>@|yellow,bold .json|@'
            or '@|blue varName|@@|yellow,bold =|@<@|white,bold path-to-resource/without/extension|@>@|yellow,bold .json|@'.""")
    private String[] varResPairs;

    @Option(
        names = {"--output"},
        paramLabel = "<path>",
        description = "output directory or a file (default: printing to the stadard output)")
    private File outputLocation;

    @Parameters(
        paramLabel = "input-template-path",
        description = "the path to 'th-tool' template that going to be processed with 'th-tool' variables",
        arity = "1..*"
    )
    private File[] inputTemplates;

    @Option(names = {"--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;

    @Spec
    CommandSpec spec; // Injected by picocli

    final ThymeleafToolCtx varsCtx = new ThymeleafToolCtx();

    @Override
    public Integer call() throws Exception {
        System.out.println("... executing 'th-tool' via command-line: ...");
        if (varsDir != null) {
            System.out.printf("- varsDir = '%s';%n", varsDir.getCanonicalPath());
            varsCtx.processDirectory(varsDir);
        }
        if (varFilePairs != null) {
            System.out.println("- varFilePairs --> " + Arrays.toString(varFilePairs));
            for (String varPair : varFilePairs) {
                varsCtx.processVarFilePair(varPair);
            }
        }
        if (varResPairs != null) {
            System.out.println("- varResPairs --> " + Arrays.toString(varResPairs));
            for (String varPair : varResPairs) {
                varsCtx.processVarResourcePair(varPair);
            }
        }
        System.out.println("... variables with following names are available in templates --> " +
            dumpAsJsonPrettyPrint(sortedSet(varsCtx.getVariableNames().stream())));

        if (outputLocation == null) {
            System.out.println("- no output location is specified (the result will be printed here)");
        } else if (outputLocation.isFile()) {
            System.out.printf("- output location is a file '%s';%n", outputLocation.getCanonicalPath());
        } else {
            System.out.printf("- output location is a directory '%s';%n", outputLocation.getCanonicalPath());
        }

        System.out.println("- inputTemplates --> " + Arrays.toString(inputTemplates));

        return 0;
    }

    /**
     *
     * @param args command-line arguments that will be processed by PicoCli-framework
     */
    public static void main(String... args) {

        int exitCode = new CommandLine(new ThymeleafTool()).execute(args);
        System.exit(exitCode);
    }
}
