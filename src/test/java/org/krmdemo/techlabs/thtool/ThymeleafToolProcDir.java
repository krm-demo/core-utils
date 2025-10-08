package org.krmdemo.techlabs.thtool;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.core.utils.SysDumpUtils.fileAttrsAsJson;

/**
 * Sub-command {@code process} of <b>{@code th-tool}</b> that transforms the input-templates
 * by {@link ThymeleafTool#templateEngine Thymeleaf-Engine} with {@link ThymeleafTool#varsCtx th-tool variables}
 */
@Command(name = "process-dir",
    mixinStandardHelpOptions = true, usageHelpWidth = 140,
    description = """
        Process the content of the input directory (option '@|yellow --input-dir|@') as @|italic th-tool|@-templates
        and save the rendering results into the output directory (option '@|yellow --output-dir|@')
        by their initial relative names in input directory. By default all files will be copied,
        and only those of them will be changed, which have corresponding Thymeleaf-tags and constructions.
        The result is exactly the same as each file would be processed individually with the command @|bold process|@.
        """
)
@Slf4j
public class ThymeleafToolProcDir implements Callable<Integer> {

    @ParentCommand
    ThymeleafTool tt;

    @Option(
        names = {"--input-dir"},
        paramLabel = "<path>",
        required = true,
        description = "the path of the source directory to copy and process files @|bold from|@")
    File inputLocation;

    @Option(
        names = {"--output-dir"},
        paramLabel = "<path>",
        required = true,
        description = "the path of the target directory to copy the processed files @|bold into|@")
    File outputLocation;

    @Option(names = {"--help"}, usageHelp = true,
        description = "Display this help message for @|bold process-dir|@ command.")
    boolean usageHelpRequested;  // <-- required to display help option

    /**
     * Entry-point of Pico-Cli Command {@code process}
     *
     * @return zero if everything is processed successfully and non-zero otherwise
     * @throws Exception in case of any un-handled error
     */
    @Override
    public Integer call() throws Exception {
        System.out.println("... executing 'th-tool' to process the directory: ...");
        tt.initVars();

        // we have to add the file-resolver to be possible to work with Thymeleaf-Fragments
        FileTemplateResolver fileResolver = new FileTemplateResolver();
        fileResolver.setOrder(1);
        fileResolver.setCharacterEncoding("UTF-8");
        fileResolver.setCacheable(false); // <-- there's no need to cache things like that for CLI-application
        fileResolver.setCheckExistence(true);  // <-- very important for the chain of resolvers
        tt.templateEngine.addTemplateResolver(fileResolver);

        System.out.println("inputLocation --> " + inputLocation);
        if (!Objects.requireNonNull(inputLocation).isDirectory()) {
            throw new IllegalArgumentException(String.format(
                "input location '%s' is not a directory", inputLocation));
        }

        System.out.println("outputLocation --> " + outputLocation);
//        if (!Objects.requireNonNull(outputLocation).isDirectory()) {
//            throw new IllegalArgumentException(String.format(
//                "output location '%s' is not a directory", outputLocation));
//        }

        try (Stream<Path> paths = Files.walk(inputLocation.toPath())) {
            paths.forEach(path -> {
                // Process each path (file or directory) found during the walk
                if (Files.isDirectory(path)) {
                    System.out.println("Directory: " + path);
                } else {
                    System.out.println("File: " + path);
                }
            });
        } catch (IOException ioEx) {
            System.err.printf("could not walk through the input directory '%s'", inputLocation);
            return -3;
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
