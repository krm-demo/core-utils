package org.krmdemo.techlabs.thtool;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * Sub-command {@code evaluate} of <b>th-tool</b> that evaluates the passed expression.
 */
@CommandLine.Command(name = "eval",
    description = "Evaluates the Thymeleaf-Expression and print the result into the standard output"
)
public class ThymeleafToolEval implements Runnable {

    @ParentCommand
    ThymeleafTool parentCommand;

    @Parameters(arity="1", paramLabel="expr", description = """
        expression to evaluate like it was inline like '@|cyan [[$${...)}]]|@',
        or as an attribute-value '@|cyan <xxx th:text="$${...}"></xxx>|@'""")
    String expression;

    @Option(names = {"--help"}, usageHelp = true,
        description = "Display this help message for @|bold eval|@ command.")
    boolean usageHelpRequested;

    @Override
    public void run() {
        System.out.println("... evaluating the passed 'th-tool'-expression: ...");
        System.out.println(expression);
    }
}
