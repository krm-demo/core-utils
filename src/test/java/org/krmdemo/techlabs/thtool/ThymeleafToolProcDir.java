package org.krmdemo.techlabs.thtool;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.krmdemo.techlabs.core.dump.DumpUtils;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static org.krmdemo.techlabs.core.utils.CoreCollectors.toSortedSet;
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
        description = "the path of the target directory to copy the processed files @|bold into|@")
    File outputLocation;

    @CommandLine.Parameters(
        paramLabel = "pattern-to-process",
        description = "file-matcher patterns to narrow the files to process",
        arity = "0..*"
    )
    String[] matcherPatterns;

    @Option(names = {"--help"}, usageHelp = true,
        description = "Display this help message for @|bold process-dir|@ command.")
    boolean usageHelpRequested;  // <-- required to display help option

    /**
     * Entry-point of Pico-Cli Command {@code process-dir}
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

        System.out.println("- inputLocation --> " + inputLocation);
        if (!Objects.requireNonNull(inputLocation).isDirectory()) {
            throw new IllegalArgumentException(String.format(
                "input location '%s' is not a directory", inputLocation));
        }

        if (outputLocation == null) {
            System.out.println("- no processing because output directory is not specified;");
        } else {
            System.out.println("- outputLocation --> " + outputLocation);
        }

        Result result = new Result();
        System.out.println("- processing result is --> " + DumpUtils.dumpAsJsonTxt(result));
        return 0;
    }

    @JsonPropertyOrder(alphabetic = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private class Result {

        @JsonGetter("count-total")
        public int countTotal() {
            return processPairs.size();
        }

        @JsonGetter("count-to-process")
        public int countToProcess() {
            return (int) processPairs.stream().filter(Pair::isProcessing).count();
        }

        @JsonGetter("count-to-copy")
        public int countToCopy() {
            return countTotal() - countToProcess();
        }

        @JsonGetter("files-to-process")
        public SortedSet<String> filesToProcess() {
            final Path inputDirPath = inputLocation.toPath().toAbsolutePath();
            return processPairs.stream()
                .filter(Pair::isProcessing)
                .map(pair -> inputDirPath.relativize(pair.input.toPath().toAbsolutePath()))
                .map(String::valueOf)
                .collect(toSortedSet());
        }

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private class Pair {
            @Getter private final File input;
            @Getter private final File output;
            @Getter private final boolean processing;
            private Pair(Path walkPath) throws IOException {
                Path inputDirPath = inputLocation.toPath().toAbsolutePath();
                Path relativePath = inputDirPath.relativize(walkPath.toAbsolutePath());
                this.input = walkPath.toFile();
                this.output = outputLocation == null ? null : Paths.get(
                    outputLocation.getCanonicalPath(),
                    relativePath.toString()
                ).toFile();
                this.processing = shouldProcess(walkPath) && !shouldSkip(walkPath);
            }
        }

        @JsonProperty("match-patterns-to-process")
        final List<String> matchPatternsToProc = new ArrayList<>();

        @JsonProperty("match-patterns-to-skip")
        final List<String> matchPatternsToSkip = new ArrayList<>();

        final List<Pair> processPairs;

        private Result() {
            if (matcherPatterns == null) {
                System.out.println("- no matching-patters specified - all files will be processed;");
            } else {
                System.out.println("- matcherPatterns --> " + Arrays.toString(matcherPatterns));
                for (String patternStr : matcherPatterns) {
                    if (patternStr.startsWith("!")) {
                        matchPatternsToSkip.add(pathMatcherPattern(patternStr.substring(1)));
                    } else if (patternStr.startsWith(":-:")) {
                        matchPatternsToSkip.add(pathMatcherPattern(patternStr.substring(3)));
                    } else {
                        matchPatternsToProc.add(pathMatcherPattern(patternStr));
                    }
                }
            }
            this.processPairs = buildPairList();
        }

        private String pathMatcherPattern(String patternStr) {
            if (StringUtils.isBlank(patternStr)) {
                throw new IllegalArgumentException("path-matcher pattern must not be blank");
            }
            return switch (patternStr.trim()) {
                case String patternGlob when patternGlob.startsWith("glob:") -> patternGlob;
                case String patternRegex when patternRegex.startsWith("regex:") -> patternRegex;
                case String patternUnknown when patternUnknown.contains(":") -> {
                    String syntaxUnknown = patternUnknown.substring(0, patternStr.indexOf(':') + 1);
                    throw new IllegalArgumentException(String.format(
                        "invalid syntax-prefix '%s' for path-matcher (only 'glob:' and 'regex:' are allowed)",
                        syntaxUnknown));
                }
                case String patternFirstSlash when patternFirstSlash.startsWith(File.separator) ->
                    "glob:" + inputLocation + patternFirstSlash;
                default -> String.format("glob:%s/**%s", inputLocation, patternStr.trim());
            };
        }

        private static Stream<PathMatcher> pathMatchers(List<String> matchPatterns) {
            return matchPatterns.stream().map(FileSystems.getDefault()::getPathMatcher);
        }

        private boolean shouldProcess(Path walkPath) {
            if (matchPatternsToProc.isEmpty()) {
                return true;  // <-- if not specified, all files should be processed as Thymeleaf-templates
            }
            return pathMatchers(matchPatternsToProc).anyMatch(
                pathMatcher -> pathMatcher.matches(walkPath)
            );
        }

        private boolean shouldSkip(Path walkPath) {
            if (matchPatternsToSkip.isEmpty()) {
                return false;  // <-- if not specified, nothing should be skipped
            }
            return pathMatchers(matchPatternsToSkip).anyMatch(
                pathMatcher -> pathMatcher.matches(walkPath)
            );
        }

        private Optional<Pair> toPair(Path walkPath) {
            try {
                return Optional.of(new Pair(walkPath));
            } catch (IOException ioEx) {
                logException(ioEx, "an exception when creating a pair for '%s'", walkPath);
                return Optional.empty();
            }
        }

        private List<Pair> buildPairList() {
            try (Stream<Path> walkPathStream = Files.walk(inputLocation.toPath())) {
                return walkPathStream
                    .filter(walkPath -> walkPath.toFile().isFile())
                    .map(this::toPair)
                    .flatMap(Optional::stream)
                    .toList();
            } catch (IOException ioEx) {
                logException(ioEx, "an exception when creating a list of pairs to process input-dir '%s'", inputLocation);
                return Collections.emptyList();
            }
        }
    }

    // --------------------------------------------------------------------------------------------

    private static void logException(Throwable throwable, String formatString, Object... formatArgs) {
        if (log.isWarnEnabled()) {
            log.warn(String.format(formatString, formatArgs), throwable);
        }
    }

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
