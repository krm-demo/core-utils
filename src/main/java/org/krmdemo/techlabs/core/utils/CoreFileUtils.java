package org.krmdemo.techlabs.core.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility-class that invokes standard JDK utilities, but wraps {@link java.io.IOException}
 * into {@link IllegalStateException} and creates missing parent directories.
 */
public class CoreFileUtils {

    /**
     * Utility-method that provides Base64-encoding of the passed content
     * that allows to use it in HTML-attributes
     * (for HTTP-URL parameter - additional URL-encoding should be applied).
     *
     * @param contentToEncode the text-content to encode
     * @return Base64-encoded content
     */
    public static String encodeBase64(String contentToEncode) {
        return encodeBase64(contentToEncode.getBytes());
    }

    /**
     * Utility-method that provides Base64-encoding of the passed array of bytes
     * that allows to use it in HTML-attributes
     * (for HTTP-URL parameter - additional URL-encoding should be applied).
     *
     * @param bytesArr the binary-content to encode
     * @return Base64-encoded content
     */
    public static String encodeBase64(byte[] bytesArr) {
        return new String(Base64.getEncoder().encode(bytesArr));
    }

    /**
     * Load the content of image-file with, encodes its content {@link Base64#getEncoder() Base64-encoder}
     * and prepend it with corresponding mime-type prefix in order to use that final value
     * either in HTML-attributes (for HTTP-URL parameter - additional URL-encoding should be applied).
     * <hr/>
     * For more advanced detection it's recommended to use <a href="https://tika.apache.org/">Apache Tika</a>
     *
     * @param imageFile a file to load the text of binary content
     * @return a data-string to use in HTML-attributes
     */
    public static String imageFileData64(File imageFile) {
        byte[] imageFileContent = loadFileAsBytes(imageFile);
        String imageFileExt = FilenameUtils.getExtension(imageFile.getPath());
        String dataPrefix = switch (imageFileExt.toLowerCase()) {
            case "jpg", "jpeg" -> "data:image/jpg;base64,";
            case "png" -> "data:image/png;base64,";
            case "gif" -> "data:image/gif;base64,";
            case "svg" -> "data:image/svg+xml;base64,";
            default -> throw new IllegalArgumentException(String.format(
                "could not detect the mime-type of file '%s' by extension '.%s'",
                imageFile, imageFileExt));
        };
        return dataPrefix + encodeBase64(imageFileContent);
    }

    /**
     * Getting the sequence of {@link Path}-components as {@link Stream Stream&lt;String&gt;}.
     * <hr/>
     * This operation is inversion of standard static methods {@link Path#of(String, String...)}
     * or {@link Paths#get(String, String...)} - so, following expressions are identical:<pre>{@code
     *     pathParts(Path.of("a/b/c"))
     *     pathParts(Path.of("a", "b", "c"))
     *     Stream.of("a", "b", "c")
     * }</pre>
     *
     * @param path an instance of {@link Path}
     * @return components of {@link Path} as {@link Stream Stream&lt;String&gt;}
     */
    public static Stream<String> pathParts(Path path) {
        return StreamSupport.stream(path.spliterator(), false).map(Path::toString);
    }

    /**
     * Getting the sequence of {@link Path}-components as {@link List List&lt;String&gt;}
     * - so, following expressions are identical:<pre>{@code
     *      pathPartsList(Path.of("a/b/c"))
     *      pathPartsList(Path.of("a", "b", "c"))
     *      List.of("a", "b", "c")  // <-- this returns unmodifiable implementation of List
     *      Arrays.asList("a", "b", "c")  // <-- this returns modifiable instance of ArrayList
     * }</pre>
     *
     * @param path an instance of {@link Path}
     * @return components of {@link Path} as {@link List List&lt;String&gt;}
     */
    public static List<String> pathPartsList(Path path) {
        return pathParts(path).toList();
    }

    /**
     * Joining the {@link #pathParts(Path) stream of path components} with {@link File#separator}
     * - so, the assertions like following are always correct:<pre>{@code
     *     assertThat(pathPartsStr(Path.of("a/b/c"))).isEqualTo("a/b/c")
     * }</pre>
     * <hr/>
     * There could be some surprises with un-normalized parts of path, which includes reference to parent directory.
     * Such cases must be handled and tests properly, because the family of methods {@code pathPartsXXX} is
     * just a pure representation of path-parts without additional handling them.
     *
     * @param path an instance of {@link Path}
     * @return the sequence of {@link Path}-components joining with {@link File#separator}
     */
    public static String pathPartsStr(Path path) {
        return pathParts(path).collect(Collectors.joining(File.separator));
    }

    /**
     * The same as {@link #loadFileAsText(File)}, but accept the path to that file as {@link String}
     *
     * @param pathToLoadStr the path of file to laod as {@link String}
     * @return the content of that file as {@link String} or {@code null} if it's impossible to do
     */
    public static String loadFileAsText(String pathToLoadStr) {
        return loadFileAsText(Paths.get(pathToLoadStr).toFile());
    }

    /**
     * Loading the content of {@code file} as {@link String} (it's allowed to use for text-files only)
     * and throws {@link IllegalStateException} if the content of file could not be loaded for any reason.
     *
     * @param fileToLoad file to read the content
     * @return the content of {@code fileToLoad} as {@link String}
     */
    public static String loadFileAsText(File fileToLoad) {
        if (fileToLoad == null || !fileToLoad.isFile()) {
            throw new IllegalStateException(String.format(
                "could not load the file %s as text, because it does not exist or it's not a normal file",
                fileToLoad == null ? "<< NULL >>" : "'" + fileToLoad + "'"));
        }
        try {
            return Files.readString(fileToLoad.toPath());
        } catch (IOException ioEx) {
            throw new IllegalStateException(String.format(
                "could not load the file '%s' as text, because of IOException", fileToLoad), ioEx);
        }
    }

    /**
     * Loading the content of {@code file} as {@code byte[]}  and throws {@link IllegalStateException}
     * if the content of file could not be loaded for any reason.
     *
     * @param fileToLoad file to read the content
     * @return the content of {@code file} as {@link String} or {@code null} if it's impossible to do
     */
    public static byte[] loadFileAsBytes(File fileToLoad) {
        if (fileToLoad == null || !fileToLoad.isFile()) {
            throw new IllegalStateException(String.format(
                "could not load the file %s as bytes, because it does not exist or it's not a normal file",
                fileToLoad == null ? "<< NULL >>" : "'" + fileToLoad + "'"));
        }
        try {
            return Files.readAllBytes(fileToLoad.toPath());
        } catch (IOException ioEx) {
            throw new IllegalStateException(String.format(
                "could not load the file '%s' as bytes, because of IOException", fileToLoad), ioEx);
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
        } catch (Exception ex) {
            throw new IllegalStateException(String.format(
                "could not load the file '%s' as list of lines", filePathToLoad), ex);
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

    // --------------------------------------------------------------------------------------------

    private CoreFileUtils() {
        // prohibit the creation of utility-class instance
        throw new UnsupportedOperationException("Cannot instantiate utility-class " + getClass().getName());
    }
}
