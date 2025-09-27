package org.krmdemo.techlabs.thtool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.krmdemo.techlabs.core.utils.SysDumpUtils.fileAttrsAsJson;
import static org.krmdemo.techlabs.thtool.ThymeleafTool.loadFileContent;
import static org.krmdemo.techlabs.thtool.ThymeleafTool.saveFileContent;

/**
 * Sub-command {@code process} of <b>{@code th-tool}</b> that transforms the input-templates
 * by {@link ThymeleafTool#templateEngine Thymeleaf-Engine} with {@link ThymeleafTool#varsCtx th-tool variables}
 */
@Command(name = "process",
    description = "Process the input-templates by Thymeleaf-Engine",
    mixinStandardHelpOptions = true, usageHelpWidth = 140
)
@Slf4j
public class ThymeleafToolProc implements Callable<Integer> {

    @ParentCommand
    ThymeleafTool tt;

    @Option(
        names = {"--output"},
        paramLabel = "<path>",
        description = "the path to output file or directory\n(default: printing to the standard output)")
    File outputLocation;

    @Parameters(
        paramLabel = "input-template-path",
        description = "the path to 'th-tool' template that going\nto be processed with 'th-tool' variables",
        arity = "1..*"
    )
    File[] inputTemplates;

    @Option(names = {"--help"}, usageHelp = true,
        description = "Display this help message for @|bold process|@ command.")
    boolean usageHelpRequested;  // <-- required to display help option

    /**
     * Entry-point of Pico-Cli Command {@code process}
     *
     * @return zero if everything is processed successfully and non-zero otherwise
     * @throws Exception in case of any un-handled error
     */
    @Override
    public Integer call() throws Exception {
        System.out.println("... executing 'th-tool' to process input-template: ...");
        tt.initVars();

        if (outputLocation == null) {
            logDebug("- no output location is specified (the result will be printed)");
        } else if (outputLocation.isFile()) {
            logDebug("- output location is a file '%s';", () -> outputLocation.getCanonicalPath());
            logDebug("- attributes of output-file are --> ", () -> fileAttrsAsJson(outputLocation));
        } else {
            logDebug("- output location is a directory '%s';", () -> outputLocation.getCanonicalPath());
            logDebug("- attributes of output-location are --> ", () -> fileAttrsAsJson(outputLocation));
        }

        logInfo("- inputTemplates --> %s", Arrays.toString(inputTemplates));
        int successCount = 0;
        for (File templateFile : inputTemplates) {
            successCount += processTemplate(templateFile) ? 1 : 0;
        }
        logInfo("... %d input-templates (out of %d) were processed successfully ...",
            successCount, inputTemplates.length);

        return (successCount == inputTemplates.length) ? 0 : -1;
    }

    /**
     * @param templateFile 'th-tool' template-file to process
     *                     by {@link ThymeleafTool#templateEngine Thymeleaf-Engine}
     *                     with {@link ThymeleafTool#varsCtx th-tool variables}
     * @return {@code true} if the template was processed successfully (and {@code false} otherwise)
     */
    public boolean processTemplate(File templateFile) {
        logInfo("- processing the template '%s' ", templateFile);
        if (!templateFile.isFile()) {
            logInfo("(the file does not exists or the path does not represent the file)");
            return false;
        }
        String templateContent = loadFileContent(templateFile);
        if (templateContent == null) {
            logInfo("(could not read the content of a file)");
            return false;
        }
        String outputContent = tt.processTemplateContent(templateContent);
        if (StringUtils.isBlank(templateContent)) {
            // TODO: maybe it's better to ignore this case or handle it in some different way
            logInfo("(the content of template is blank)");
            return false;
        }

        if (outputLocation != null && (!outputLocation.exists() || outputLocation.isFile())) {
            saveFileContent(outputLocation, outputContent);
            logInfo("(successfully saved into '%s')", outputLocation.toPath());
        } else {
            logInfo("(successfully processed and printed");
            logDebug("--- %s ---", () -> "~".repeat(100));
            logDebug(outputContent);
            logDebug("... %s ...", () -> "~".repeat(100));
            if (outputLocation != null) {
                logInfo("... output into location like '%s' is not supported yet :-( ...");
            }
        }
        return true;
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
