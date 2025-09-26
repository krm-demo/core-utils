package org.krmdemo.techlabs.thtool;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

/**
 * Sub-command {@code evaluate} of <b>{@code th-tool}</b> that evaluates the passed expression.
 */
@CommandLine.Command(name = "eval",
    description = "Evaluates the Thymeleaf-Expression and print the result into the standard output"
)
public class ThymeleafToolEval implements Callable<Integer> {

    @ParentCommand
    ThymeleafTool tt;

    @Parameters(arity="1", paramLabel="expr", description = """
        expression to evaluate like it was inline like '@|cyan [[$${...)}]]|@',
        or as an attribute-value '@|cyan <xxx th:text="$${...}"></xxx>|@'""")
    String expression;

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

        System.out.println(expression);

        return 0;
    }
}
