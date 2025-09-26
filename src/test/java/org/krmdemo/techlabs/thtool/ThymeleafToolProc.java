package org.krmdemo.techlabs.thtool;

import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.sortedSet;
import static org.krmdemo.techlabs.core.utils.SysDumpUtils.fileAttrsAsJson;
import static org.krmdemo.techlabs.json.JacksonUtils.dumpAsJsonPrettyPrint;

/**
 * Sub-command {@code process} of <b>th-tool</b> that transforms the input-templates
 * by {@link ThymeleafTool#templateEngine Thymeleaf-Engine} with {@link ThymeleafTool#varsCtx th-tool variables}
 */
@Command(name = "process",
    description = "Process the input-templates by Thymeleaf-Engine"
)
public class ThymeleafToolProc implements Callable<Integer> {

    @ParentCommand
    ThymeleafTool tt;

    @Option(
        names = {"--output"},
        paramLabel = "<path>",
        description = "output directory or a file\n(default: printing to the standard output)")
    File outputLocation;

    @Parameters(
        paramLabel = "input-template-path",
        description = "the path to 'th-tool' template that going\nto be processed with 'th-tool' variables",
        arity = "1..*"
    )
    File[] inputTemplates;

    @Option(names = {"--help"}, usageHelp = true,
        description = "Display this help message for @|bold process|@ command.")
    boolean usageHelpRequested;

    /**
     * Entry-point of Pico-Cli Command
     *
     * @return zero if everything is processed successfully and non-zero otherwise
     * @throws Exception in case of any un-handled error
     */
    @Override
    public Integer call() throws Exception {
        System.out.println("... executing 'th-tool' via command-line: ...");
        if (tt.varsDir != null) {
            System.out.printf("- varsDir = '%s';%n", tt.varsDir.getCanonicalPath());
            tt.varsCtx.processDirectory(tt.varsDir);
        }
        if (tt.varFilePairs != null) {
            System.out.println("- varFilePairs --> " + Arrays.toString(tt.varFilePairs));
            for (String varPair : tt.varFilePairs) {
                tt.varsCtx.processVarFilePair(varPair);
            }
        }
        if (tt.varResPairs != null) {
            System.out.println("- varResPairs --> " + Arrays.toString(tt.varResPairs));
            for (String varPair : tt.varResPairs) {
                tt.varsCtx.processVarResourcePair(varPair);
            }
        }
        System.out.println("... variables with following names are available in templates --> " +
            dumpAsJsonPrettyPrint(sortedSet(tt.varsCtx.getVariableNames().stream())));

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
     * @param templateFile 'th-tool' template to process by {@link ThymeleafTool#templateEngine Thymeleaf-Engine}
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
        if (StringUtils.isBlank(templateContent)) {
            // TODO: maybe it's better to ignore this case or handle it in some different way
            System.out.println("(the content of template is blank)");
            return false;
        }
        String outputContent = tt.templateEngine.process(templateContent, tt.varsCtx);

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
}
