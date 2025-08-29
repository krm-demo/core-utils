package org.krmdemo.techlabs.thtool;

import org.krmdemo.techlabs.sysdump.SysDumpUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * This class is an entry-point of <b>{@code th-tool}</b>
 * that is used to process {@code *.md.th} and {@code *.html.th}
 * and other <a href="https://www.thymeleaf.org/"></a>-templates
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
    mixinStandardHelpOptions = true, usageHelpWidth = 120
)
public class ThymeleafTool implements Callable<Integer> {

    @Option(
        names = {"--vars-dir"},
        defaultValue = "./th-vars",
        description = "directory with 'var-XXX.json' files (default: '@|blue,bold ${DEFAULT-VALUE}|@')",
        required = true)
    private File inputVarsDir;

    @Option(
        names = {"--output-location"},
        description = "output directory or a file (default: printing to the stadard output)")
    private File outputLocation;

    @Option(names = {"--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;

    @Spec
    CommandSpec spec; // Injected by picocli

    @Override
    public Integer call() throws Exception {
        System.out.println("... executing 'th-tool' with command-line: ...");
        System.out.printf("- inputVarsDir = '%s';%n", inputVarsDir.getCanonicalPath());
        //System.out.println("Java SystemProperties --> " + SysDumpUtils.dumpSysPropsExAsJson());
        if (outputLocation == null) {
            System.out.println("- no output location is specified (the result will be printed here)");
        } else if (outputLocation.isFile()) {
            System.out.printf("- output location is a file '%s';%n", outputLocation.getCanonicalPath());
        } else {
            System.out.printf("- output location is a directory '%s';%n", outputLocation.getCanonicalPath());
        }
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
