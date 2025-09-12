package org.krmdemo.techlabs.thtool;

import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.sysdump.SysDumpUtils;
import org.thymeleaf.TemplateEngine;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedSet;
import static org.krmdemo.techlabs.sysdump.SysDumpUtils.fileAttrsAsJson;

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

    /**
     * An instance of {@link TemplateEngine Thymeleaf's Template Engine}
     */
    final TemplateEngine templateEngine = new TemplateEngine();

    /**
     * An instance of {@link TemplateEngine Thymeleaf's Context} that holds the variables for templates
     */
    final ThymeleafToolCtx varsCtx = new ThymeleafToolCtx();

    /**
     * Entry-point of Pico-Cli Command Line
     * @return zero if everything is processed successfully and non-zero otherwise
     * @throws Exception in case of any un-handled error
     */
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
            System.out.println("- attributes of output-file are --> " + fileAttrsAsJson(outputLocation));
        } else {
            System.out.printf("- output location is a directory '%s';%n", outputLocation.getCanonicalPath());
            System.out.println("- attributes of output-location are --> " + fileAttrsAsJson(outputLocation));
        }

        System.out.println("- inputTemplates --> " + Arrays.toString(inputTemplates));
        int successCount = 0;
        for (File templateFile : inputTemplates) {
            successCount += processTemplate(templateFile) ? 1 : 0;
        }
        System.out.printf("... %d input-templates (out of %d) were processed successfully ...%n",
            successCount, inputTemplates.length);

        return (successCount == inputTemplates.length) ? 0 : -1;
    }

    /**
     * @param templateFile 'th-tool' template to process with {@link #templateEngine} and {@link #varsCtx}
     * @return {@code true} if the template was processed successfully (and {@code false} otherwise)
     */
    public boolean processTemplate(File templateFile) {
        System.out.printf("- processing the template '%s' ", templateFile);
        if (!templateFile.isFile()) {
            System.out.println("(the file does not exists or the path does not represent the file)");
            return false;
        }
        String templateContent = loadFileContent(templateFile);
        if (templateContent == null) {
            System.out.println("(could not read the content of a file)");
            return false;
        }
        if (StringUtils.isBlank(templateContent)) {
            // TODO: maybe it's better to ignore this case or handle it in some different way
            System.out.println("(the content of template is blank)");
            return false;
        }
        String outputContent = templateEngine.process(templateContent, varsCtx);

        if (outputLocation != null && (!outputLocation.exists() || outputLocation.isFile())) {
            saveFileContent(outputLocation, outputContent);
            System.out.printf("(successfully saved into '%s')", outputLocation);
        } else {
            System.out.println("(successfully processed and printed");
            System.out.println("--- " + "-".repeat(100) + " ---");
            System.out.println(outputContent);
            System.out.println("... " + "-".repeat(100) + " ...");
            if (outputLocation != null) {
                System.out.println("... output into location like '%s' is not supported yet ...");
            }
        }

        return true;
    }

    /**
     * @param fileToLoad file to read the content
     * @return the content of {@code file} or {@code null} if it's impossible to do
     */
    public static String loadFileContent(File fileToLoad) {
        try {
            return Files.readString(fileToLoad.toPath());
        } catch (IOException ioEx) {
            ioEx.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Saving the content into the file {@code fileToSave} (if the file is not empty - it will be truncated)
     *
     * @param fileToSave the file to save into
     * @param fileContent the content to be saved
     * @throws IllegalStateException in case of something goes wrong (which must not really happen)
     */
    public static void saveFileContent(File fileToSave, String fileContent) {
        try {
            Files.writeString(fileToSave.toPath(), fileContent);
        } catch (IOException ioEx) {
            throw new IllegalStateException("could not save the content into the file " + fileToSave, ioEx);
        }
    }


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
