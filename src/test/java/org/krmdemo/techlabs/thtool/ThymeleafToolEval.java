package org.krmdemo.techlabs.thtool;

import org.apache.commons.text.StringEscapeUtils;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import static org.krmdemo.techlabs.core.utils.SysDumpUtils.fileAttrsAsJson;
import static org.krmdemo.techlabs.thtool.ThymeleafTool.saveFileContent;

/**
 * Sub-command {@code evaluate} of <b>{@code th-tool}</b> that evaluates the passed expression.
 */
@CommandLine.Command(name = "eval",
    description = "Evaluates the Thymeleaf-Expression and print the result into the standard output",
    mixinStandardHelpOptions = true, usageHelpWidth = 140
)
public class ThymeleafToolEval implements Callable<Integer> {

    @ParentCommand
    ThymeleafTool tt;

    @Option(
        names = {"--output"},
        paramLabel = "<path>",
        description = "the path to output file\n(default: printing to the standard output)")
    File outputFile;

    @Parameters(arity="0..*", paramLabel="expression", description = """
        the tail of command line is treated as the expression to evaluate
        like it was inline like '@|cyan [[$${...)}]]|@',
        or as an attribute-value at '@|cyan <xxx th:text="$${...}"></xxx>|@'""")
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
        System.out.println("... evaluating the passed 'th-tool'-expression: ...");
        tt.initVars();

        if (outputFile == null) {
            System.out.println("- no output location is specified (the result will be printed here)");
        } else {
            System.out.println("- attributes of output-file are --> " + fileAttrsAsJson(outputFile));
            if (!outputFile.isFile()) {
                System.out.printf("- output location is NOT a file '%s';%n", outputFile.getCanonicalPath());
                return -1;
            }
        }

        String expression = String.join(" ", expressionsArgs);
        System.out.printf("- expression is <<%s>>%n", expression);

        String templateContent = String.format("[[${%s}]]", expression);
        System.out.printf("- templateContent is <<%s>>%n", templateContent);

        String outputContent = tt.processTemplateContent(templateContent);

        if (outputFile != null) {
            saveFileContent(outputFile, outputContent);
            System.out.printf("(successfully saved into '%s')", outputFile);
        } else {
            System.out.println("(successfully processed and printed");
            System.out.println("--- " + "-".repeat(100) + " ---");
            System.out.println(StringEscapeUtils.unescapeHtml4(outputContent));
            System.out.println("... " + "-".repeat(100) + " ...");
        }
        return 0;
    }
}
