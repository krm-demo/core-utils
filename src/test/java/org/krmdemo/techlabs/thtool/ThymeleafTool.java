package org.krmdemo.techlabs.thtool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.thtool.helpers.GitHelper;
import org.krmdemo.techlabs.thtool.helpers.GithubBadgeHelper;
import org.krmdemo.techlabs.thtool.helpers.GithubHelper;
import org.krmdemo.techlabs.thtool.helpers.GithubInputsHelper;
import org.krmdemo.techlabs.thtool.helpers.MavenHelper;
import org.krmdemo.techlabs.thtool.helpers.ZeroSpaceHelper;
import org.thymeleaf.TemplateEngine;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedSet;
import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;
import static org.krmdemo.techlabs.thtool.ThymeleafToolCtx.DEFAULT_VARS_DIR;

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
    subcommands = { ThymeleafToolEval.class, ThymeleafToolProc.class, ThymeleafToolProcDir.class },
    mixinStandardHelpOptions = true, usageHelpWidth = 140,
    description = """
        A tool to process the input-templates and evaluate the expressions using Thymeleaf.
        Following helper-objects and props-containers are available out of the box:
          @|blue mh|@ - to work with maven-project versions;
          @|blue cu|@ - to invoke most of utility-methods of @|bold,italic core-utils|@ library;
        Other custom variables could be introduced using folllowing command-line options:"""
)
@Slf4j
public class ThymeleafTool {

    @Option(
        names = {"--vars-dir"},
        paramLabel = "<path>",
        defaultValue = DEFAULT_VARS_DIR,
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
    boolean usageHelpRequested; // <-- required to display help option

    @Spec
    CommandSpec spec; // <-- injected by picocli

    /**
     * An instance of {@link TemplateEngine Thymeleaf's Template Engine}
     */
    final TemplateEngine templateEngine = new TemplateEngine();

    /**
     * An instance of {@link TemplateEngine Thymeleaf's Context} that holds the variables for templates
     */
    final ThymeleafToolCtx varsCtx = new ThymeleafToolCtx();

    @SafeVarargs
    @SuppressWarnings("varargs")
    public ThymeleafTool(Consumer<ThymeleafToolCtx>... ctxInitArr) {
        Arrays.stream(ctxInitArr).forEach(init -> init.accept(varsCtx));
    }

    /**
     * Initialize <b>{@code th-tool}</b> variables to be used in subcommands.
     *
     * @throws Exception in case of any un-handled error
     */
    public void initVars() throws Exception {
        if (varsDir != null) {
            logInfo("- varsDir = '%s';", () -> varsDir.getCanonicalPath());
            varsCtx.processDirectory(varsDir);
        }
        if (varFilePairs != null) {
            logInfo("- varFilePairs --> %s", () -> Arrays.toString(varFilePairs));
            for (String varPair : varFilePairs) {
                varsCtx.processVarFilePair(varPair);
            }
        }
        if (varResPairs != null) {
            logInfo("- varResPairs --> %s", () -> Arrays.toString(varResPairs));
            for (String varPair : varResPairs) {
                varsCtx.processVarResourcePair(varPair);
            }
        }
        // Here the predefined helper-objects are registered in Thymeleaf-Context
        varsCtx.setVariable("mh", new MavenHelper());
        varsCtx.setVariable("zh", new ZeroSpaceHelper());
        logInfo("... variables and helpers with following names are available in templates --> %s",
            () -> dumpAsJsonPrettyPrint(sortedSet(varsCtx.getVariableNames().stream())));
    }

    /**
     * @param templateContent the content of 'th-tool' template to process
     *                        by {@link ThymeleafTool#templateEngine Thymeleaf-Engine}
     *                        with {@link ThymeleafTool#varsCtx th-tool variables}
     * @return {@code true} if the template was processed successfully (and {@code false} otherwise)
     */
    public String processTemplateContent(String templateContent) {
        if (StringUtils.isBlank(templateContent)) {
            return null;
        } else {
            return templateEngine.process(templateContent, varsCtx);
        }
    }

    // --------------------------------------------------------------------------------------------

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
        System.exit(executeMain(args));
    }

    /**
     * Used in functional-tests - the same as {@link #main(String...)},
     * but without invocation of {@link System#exit(int)} to be able to verify things.
     *
     * @param args command-line arguments that will be processed by PicoCli-framework
     * @return system exit-code ({@code 0} means that everything is OK)
     */
    static int executeMain(String... args) {
        ThymeleafTool tt = new ThymeleafTool(
            GitHelper::register,
            GithubHelper::register,
            GithubInputsHelper::register,
            GithubBadgeHelper::register
        );
        return new CommandLine(tt).execute(args);
    }

    // --------------------------------------------------------------------------------------------

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

    // --------------------------------------------------------------------------------------------

    private static void logInfo(String formatString, Callable<?>... formatArgs) {
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
