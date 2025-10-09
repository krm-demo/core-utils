package org.krmdemo.techlabs.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Utility-class that invokes standard JDK utilities, but wraps {@link java.io.IOException}
 * into {@link IllegalStateException} and creates missing parent directories.
 */
public class CoreFileUtils {

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

}
