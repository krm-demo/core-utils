package org.krmdemo.techlabs.thtool;

import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
     * @param templateFile 'th-tool' template-file to process
     *                     by {@link ThymeleafTool#templateEngine Thymeleaf-Engine}
     *                     with {@link ThymeleafTool#varsCtx th-tool variables}
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
        String outputContent = tt.processTemplateContent(templateContent);
        if (StringUtils.isBlank(templateContent)) {
            // TODO: maybe it's better to ignore this case or handle it in some different way
            System.out.println("(the content of template is blank)");
            return false;
        }

        if (outputLocation != null && (!outputLocation.exists() || outputLocation.isFile())) {
            saveFileContent(outputLocation, outputContent);
            System.out.printf("(successfully saved into '%s'%n)", outputLocation.toPath());
        } else {
            System.out.println("(successfully processed and printed");
            System.out.println("--- " + "-".repeat(100) + " ---");
            System.out.println(outputContent);
            System.out.println("... " + "-".repeat(100) + " ...");
            if (outputLocation != null) {
                System.out.println("... output into location like '%s' is not supported yet :-( ...");
            }
        }
        return true;
    }
}
