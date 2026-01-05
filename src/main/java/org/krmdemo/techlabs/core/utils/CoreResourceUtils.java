package org.krmdemo.techlabs.core.utils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Utility-class to load the classpath-resources and copy them to the local file system,
 * that invokes standard JDK utilities, but wraps {@link java.io.IOException}
 * into {@link IllegalStateException} and creates missing parent directories.
 * <hr/>
 * TODO: cover each method with tests
 */
public class CoreResourceUtils {

    /**
     * Loading the content of classpath-resource by path {@code file} as {@link String}
     * (it's allowed to use for text-resources only) and throws {@link IllegalStateException}
     * if the resource could not be located or its content could not be loaded for any reason.
     *
     * @param resourcePath the absolute path of classpath-resource (without leading {@code '/'}-slash)
     * @return the content of classpath-resource by path {@code resourcePath} as {@link String}
     */
    public static String resourceAsText(String resourcePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = classLoader.getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException(String.format(
                "could not locate teh resource by path '%s'", resourcePath));
        }
        try (InputStream resourceStream = resourceUrl.openStream()) {
            return IOUtils.toString(resourceStream, Charset.defaultCharset());
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(
                String.format("could not load the resource by path '%s' (resource URL is '%s')",
                    resourcePath, resourceUrl), ioEx);
        }
    }

    /**
     * Loading the content of classpath-resource by path {@code file} as {@code Optional<String>},
     * and the returned value is {@link Optional#empty()} if the resource could not be loaded for any reason.
     *
     * @param resourcePath the absolute path of classpath-resource (without leading {@code '/'}-slash)
     * @return the content of classpath-resource by path {@code resourcePath} or {@link Optional#empty()}
     */
    public static Optional<String> resourceAsTextOpt(String resourcePath) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = classLoader.getResource(resourcePath);
        if (resourceUrl == null) {
            return Optional.empty();
        }
        try (InputStream resourceStream = resourceUrl.openStream()) {
            return Optional.of(IOUtils.toString(resourceStream, Charset.defaultCharset()));
        } catch (IOException ignored) {
            return Optional.empty();
        }
    }

    public static List<String> resourceAsTextList(String... resourcePathArr) {
        return Arrays.stream(resourcePathArr)
            .map(CoreResourceUtils::resourceAsTextOpt)
            .flatMap(Optional::stream)
            .toList();
    }

    /**
     * Copy the classpath-resource by path {@code resourcePath} into the file {@code targetFile}
     * and creates all missing parent directories if necessary. An existing target file is completely replaced.
     *
     * @param resourcePath the absolute path of classpath-resource (without leading {@code '/'}-slash)
     * @param targetFile the destination file to copy the loaded resource over into
     */
    public static void copyResourceToFile(String resourcePath, File targetFile) {
        if (targetFile == null) {
            throw new IllegalStateException(String.format(
                "could not copy the resource by path '%s' because the target file is null", resourcePath));
        } else if (targetFile.isDirectory()) {
            throw new IllegalStateException(String.format(
                "could not copy the resource by path '%s' because existing target '%s' is a directory",
                resourcePath, targetFile.getAbsoluteFile()));
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = classLoader.getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException(String.format(
                "could not locate the resource by path '%s' to copy it into the target file '%s'",
                resourcePath, targetFile.getAbsoluteFile()));
        }
        try (InputStream resourceStream = resourceUrl.openStream()) {
            if (targetFile.getParentFile() != null) {
                Files.createDirectories(targetFile.getParentFile().toPath());
            }
            Files.copy(resourceStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioEx) {
            throw new IllegalStateException(String.format(
                "could not copy the resource by path '%s' (resource URL is '%s') into the target file '%s'",
                resourcePath, resourceUrl, targetFile.getAbsoluteFile()), ioEx);
        }
    }

    /**
     * Copy the classpath-resource by path {@code resourcePath} to the taget directory {@code targetDir}
     * and creates all missing parent directories if necessary (including the target one).
     * An existing target file is completely replaced.
     *
     * @param resourcePath the absolute path of classpath-resource (without leading {@code '/'}-slash)
     * @param targetDir the destination directory file to copy the loaded resource into
     */
    public static void copyResourceToDir(String resourcePath, File targetDir) {
        if (targetDir == null) {
            throw new IllegalStateException(String.format(
                "could not copy the resource by path '%s' because the target directory is null", resourcePath));
        } else if (targetDir.exists() && !targetDir.isDirectory()) {
            throw new IllegalStateException(String.format(
                "could not copy the resource by path '%s' because existing target '%s' is not a directory",
                resourcePath, targetDir.getAbsoluteFile()));
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = classLoader.getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException(String.format(
                "could not locate the resource by path '%s' to copy it into the target directory '%s'",
                resourcePath, targetDir.getAbsoluteFile()));
        }
        try (InputStream resourceStream = resourceUrl.openStream()) {
            Files.createDirectories(targetDir.toPath());
            Path targetPath = targetDir.toPath().resolve(Paths.get(resourceUrl.toURI()).getFileName());
            Files.copy(resourceStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (URISyntaxException | IOException ex) {
            throw new IllegalStateException(String.format(
                "could not copy the resource by path '%s' (resource URL is '%s') into the target file '%s'",
                resourcePath, resourceUrl, targetDir.getAbsoluteFile()), ex);
        }
    }
}
