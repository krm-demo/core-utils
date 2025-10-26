package org.krmdemo.techlabs.core.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.copyFile;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.loadFileAsText;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.saveFileContent;

/**
 * A unit-test to verify the utility-class {@link CoreFileUtils}
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CoreFileUtilsTest {

    private final static Path PATH_TO_SAVE =
        Paths.get(".", "target", "test-dir-1", "test-dir-2", "test-file.txt");

    private final static Path PATH_TO_COPY =
        Paths.get(".", "target", "test-dir-1", "test-dir-to-copy", "test-file-copy.txt");

    @Test
    @Order(1)
    @DisplayName("Checking CoreFileUtils.saveFileContent(...)")
    void testSave(TestInfo testInfo) {
        final File fileToSave = PATH_TO_SAVE.toFile();
        final String contentToSave = fileContent(testInfo);
        assertThat(contentToSave).contains("CoreFileUtils.saveFileContent");

        saveFileContent(fileToSave, contentToSave);

        assertThat(fileToSave.exists()).isTrue();
        assertThat(fileToSave.isFile()).isTrue();
        assertThat(CoreFileUtils.loadFileAsText(fileToSave)).isEqualTo(contentToSave);
    }

    @Test
    @Order(2) // <-- here we assumed that test-file has been already created
    @DisplayName("Checking CoreFileUtils.copyFile(...)")
    void testCopy(TestInfo testInfo) {
        final File fileSource = PATH_TO_SAVE.toFile();
        final File fileTarget = PATH_TO_COPY.toFile();

        copyFile(fileSource, fileTarget);

        assertThat(CoreFileUtils.loadFileAsText(fileTarget))
            .isEqualTo(CoreFileUtils.loadFileAsText(fileSource));
    }

    private static String fileContent(TestInfo testInfo) {
        return String.format("""
            ... this file is saved during JUnit-test: ...
            - displayName = '%s';
            - testClass is %s;
            - testMethods is %s
            """,
            testInfo.getDisplayName(),
            testInfo.getTestClass(),
            testInfo.getTestMethod());
    }
}
