package org.krmdemo.techlabs.thtool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static org.krmdemo.techlabs.thtool.ThymeleafTool.saveFileContent;

/**
 * Sub-command {@code evaluate} of <b>{@code th-tool}</b> that evaluates the passed expression.
 */
@CommandLine.Command(name = "eval",
    mixinStandardHelpOptions = true, usageHelpWidth = 140,
    description = """
        Evaluates the Thymeleaf-Expression and print the result into the standard output,
        like such expression would be inlined into Thymeleaf-Template like '@|cyan [[$${...)}]]|@'
        or its unescaped version '@|cyan [($${...)})]|@' (for symbols like @|green &|@, @|green >|@, @|green <|@, ...)
        or as an attribute-value like '@|cyan <xxx th:text="$${...}"></xxx>|@'
        """
)
@Slf4j
public class ThymeleafToolEval implements Callable<Integer> {

    @ParentCommand
    ThymeleafTool tt;

    @Option(
        names = {"--output"},
        paramLabel = "<path>",
        description = "the path to output file\n(default: printing to the standard output)")
    File outputFile;

    @Parameters(arity="0..*", paramLabel="expression", description = """
        the tail of command line is treated as the expression to evaluate.
        If the expression contains quotes, brackets and other characters, which are sensitive for shell-script,
        the whole expression (or its parts) is required to be escaped like following:
        - @|italic th-tool eveal|@ @|cyan $'\\' "Hello, " + "World." \\''|@ - the result is @|green Hello, World.|@;
        - @|italic th-tool eveal|@ @|cyan $'\\' "Hello, " + "World \\\\u0021 \\\\u0021\\\\u0021" \\''|@ - the result is @|green Hello, World !!!|@;
        - @|italic th-tool eveal|@ @|cyan $'\\' "\\\\\\\\inside back-slashes\\\\\\\\" \\''|@ - the result is @|green \\inside back-slashes\\|@;
        - @|italic th-tool eveal|@ @|cyan $'\\' 1+2 * 3+4 * 5 \\''|@ - the result is @|green 27|@;
        - @|italic th-tool eveal|@ @|cyan $'\\' mavenProps["maven-project.artifact"] \\''|@ - the result is @|green core-utils|@;
        but in most casess and when using the same expression in templates such escaping is not necessary:
        - @|italic th-tool eveal|@ @|cyan 20 - 30|@ - the result is @|green -10|@;
        - @|italic th-tool eveal|@ @|cyan 20 + 30|@ - the result is @|green 50|@;
        - @|italic th-tool eveal|@ @|cyan mh.resourcePath |@ - the result is @|green /META-INF/maven/maven-project.properties|@;
        """
    )
    List<String> expressionsArgs;;

    @Option(names = {"--help"}, usageHelp = true,
        description = "Display this help message for @|bold eval|@ command.")
    boolean usageHelpRequested;  // <-- required to display help option

    /**
     * Entry-point of Pico-Cli Command {@code eval}
     *
     * @return zero if everything is processed successfully and non-zero otherwise
     * @throws Exception in case of any un-handled error
     */
    @Override
    public Integer call() throws Exception {
        logInfo("... evaluating the passed 'th-tool'-expression: ...");
        tt.initVars();

        if (outputFile == null) {
            logDebug("- no output location is specified (the result will be printed)");
        } else if (!outputFile.isFile()) {
            logDebug("- output location is NOT a file '%s';", () -> outputFile.getCanonicalPath());
        }

        String expression = String.join(" ", expressionsArgs);
        logInfo("- expression is <<%s>>", expression);

        String templateContent = String.format("[[${%s}]]", expression);
        logDebug("- templateContent is <<%s>>", () -> templateContent);

        String outputContent = StringEscapeUtils.unescapeHtml4(
            tt.processTemplateContent(templateContent));

        if (outputFile != null) {
            saveFileContent(outputFile, outputContent);
            logInfo("(successfully saved into '%s')", outputFile.toPath());
        } else {
            logInfo("(successfully processed and printed");
            logDebug("--- %s ---", () -> "-".repeat(100));
            logDebug(outputContent);
            logDebug("... %s ...", () -> "-".repeat(100));
            // The only hit to standard output !!!
            System.out.print(outputContent);
        }
        return 0;
    }

    // --------------------------------------------------------------------------------------------

    private static void logInfo(String formatString, Object... formatArgs) {
        if (log.isInfoEnabled()) {
            log.info(String.format(formatString, formatArgs));
        }
    }

    private static void logDebug(String formatString, Callable<?>... formatArgs) {
        if (log.isDebugEnabled()) {
            Object[] args = Arrays.stream(formatArgs).map(argFunc -> {
                try {
                    return argFunc.call();
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }).toArray();
            log.debug(String.format(formatString, args));
        }
    }
}
