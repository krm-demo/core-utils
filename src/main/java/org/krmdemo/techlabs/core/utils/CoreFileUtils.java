package org.krmdemo.techlabs.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility-class that invokes standard JDK utilities, but wraps {@link java.io.IOException}
 * into {@link IllegalStateException} and creates missing parent directories.
 */
public class CoreFileUtils {

    /**
     * The same as {@link #loadFileContent(File)}, but accept the path to that file as {@link String}
     *
     * @param pathToLoadStr the path of file to laod as {@link String}
     * @return the content of that file as {@link String} or {@code null} if it's impossible to do
     */
    public static String loadFileContent(String pathToLoadStr) {
        return loadFileContent(Paths.get(pathToLoadStr).toFile());
    }

    /**
     * Loading the content of {@code file} as {@link String} (it's better to use for text-files only)
     * and returning {@code null} if the content of file could not be loaded for any reason.
     *
     * @param fileToLoad file to read the content
     * @return the content of {@code file} as {@link String} or {@code null} if it's impossible to do
     */
    public static String loadFileContent(File fileToLoad) {
        if (fileToLoad == null || !fileToLoad.isFile()) {
            throw new IllegalStateException(String.format(
                "could not load the file %s because it does not exist or it's not a normal file",
                fileToLoad == null ? "<< NULL >>" : "'" + fileToLoad + "'"));
        }
        try {
            return Files.readString(fileToLoad.toPath());
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * Loading the content of {@code file} as list of lines,
     * where each line is an element of returning {@link List} of {@link String}.
     *
     * @param filePathToLoad path of file to read the content as lines
     * @return the content of {@code filePathToLoad} as {@link List List&lt;String&gt;}
     */
    public static List<String> loadFileLines(Path filePathToLoad) {
        if (filePathToLoad == null) {
            throw new IllegalStateException(
                "the path to file to load lines MUST NOT be null");
        }
        try (Stream<String> lines = Files.lines(filePathToLoad)) {
            return lines.toList();
        } catch (IOException ioEx) {
            throw new IllegalStateException(String.format(
                "could not load the file '%s' as list of lines", filePathToLoad), ioEx);
        }
    }

    /**
     * Saving the content into the file {@code fileToSave} (if the file is not empty - it will be truncated).
     * All missing parent directories will be created if necessary.
     *
     * @param fileToSave the file to save into
     * @param fileContent the content to be saved
     * @throws IllegalStateException in case of something goes wrong (which must not really happen)
     */
    public static void saveFileContent(File fileToSave, String fileContent) {
        if (fileToSave == null) {
            throw new IllegalStateException(
                "could not save the file content, because fileToSave is null");
        }
        try {
            if (fileToSave.getParentFile() != null) {
                Files.createDirectories(fileToSave.getParentFile().toPath());
            }
            Files.writeString(fileToSave.toPath(), fileContent);
        } catch (IOException ioEx) {
            throw new IllegalStateException("could not save the content into the file " + fileToSave, ioEx);
        }
    }

    /**
     * Copy the file {@code sourceFile} to the file {@code targetFile}
     * and creates all missing parent directories if necessary.
     */
    public static void copyFile(File sourceFile, File targetFile) {
        if (sourceFile == null || !sourceFile.isFile()) {
            throw new IllegalStateException(String.format(
                "could not copy the file %s because it's not a normal file",
                sourceFile == null ? "<< NULL >>" : "'" + sourceFile + "'"));
        }
        if (targetFile == null) {
            throw new IllegalStateException(String.format(
                "could not copy the file '%s' because target file is null", sourceFile));
        }
        try {
            if (targetFile.getParentFile() != null) {
                Files.createDirectories(targetFile.getParentFile().toPath());
            }
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioEx) {
            throw new IllegalStateException(String.format(
                "could not copy the file '%s' into '%s' because of IOException",
                sourceFile, targetFile), ioEx);
        }
    }

    /**
     * The same as {@link Files#deleteIfExists(Path)}, but ignore any kind of {@link IOException}.
     * <hr/>
     * <u><i>Note:</i></u> For non-empty directory the method {@link #removeSilent(File)} should be used,
     * because this method will always return {@code false} for non-empty directory and remove nothing.
     *
     * @param pathToRemove the path to either a file or an empty directory to remove
     * @return {@code true} if the item (file or empty directory) is removed, or {@code false} otherwise
     */
    public static boolean removePathSilent(Path pathToRemove) {
        if (pathToRemove == null) {
            return false;
        }
        try {
            return Files.deleteIfExists(pathToRemove);
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Recursively removes either a single file or the whole directory and returns the number of removed items
     *
     * @param fileOrDirToRemove path to either a file or a directory to remove recursively
     * @return the number of removed items
     */
    public static int removeSilent(File fileOrDirToRemove) {
        if (fileOrDirToRemove == null) {
            return -1;
        }
        if (!fileOrDirToRemove.exists()) {
            return 0;
        }
        try {
            if (fileOrDirToRemove.isFile()) {
                return fileOrDirToRemove.delete() ? 1 : 0;
            }
            try (Stream<Path> walkPathStream = Files.walk(fileOrDirToRemove.toPath())) {
                return (int) walkPathStream.sorted(Comparator.reverseOrder()) // Sort in reverse order to delete contents before directories
                    .filter(CoreFileUtils::removePathSilent)
                    .count();
            }
        } catch (IOException ioEx) {
            return -2;
        }
    }
}
