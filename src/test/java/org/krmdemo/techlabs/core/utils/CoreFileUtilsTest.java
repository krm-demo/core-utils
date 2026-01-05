package org.krmdemo.techlabs.core.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.copyFile;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.imageFileData64;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.loadFileAsBytes;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.loadFileAsText;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.loadFileLines;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.pathParts;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.pathPartsList;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.pathPartsStr;
import static org.krmdemo.techlabs.core.utils.CoreFileUtils.saveFileContent;
import static org.krmdemo.techlabs.core.utils.CoreStringUtils.multiLine;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

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
        assertThat(loadFileAsText(fileToSave)).isEqualTo(contentToSave);
    }

    @Test
    @Order(2) // <-- here we assumed that test-file has been already created
    @DisplayName("Checking CoreFileUtils.copyFile(...)")
    void testCopy() {
        final File fileSource = PATH_TO_SAVE.toFile();
        final File fileTarget = PATH_TO_COPY.toFile();
        copyFile(fileSource, fileTarget);
        assertThat(loadFileAsText(fileTarget)).isEqualTo(loadFileAsText(fileSource));
    }

    @Test
    void testPathParts() {
        assertThat(pathPartsStr(PATH_TO_SAVE)).isEqualTo("./target/test-dir-1/test-dir-2/test-file.txt");
        assertThat(pathPartsList(PATH_TO_SAVE)).hasSize(5);
    }

    @Test
    void testImageFileData64() {
        assertThat(imageFileData64(
                Paths.get("./src/test/resources/jpeg-samples/NYC-Downtown.jpeg").toFile())
            ).hasSize(827_358);
        assertThat(imageFileData64(
                Paths.get("./src/test/resources/jpeg-samples/NYC-Liberty.jpg").toFile())
            ).hasSize(632_646);
        assertThatIllegalArgumentException().isThrownBy(
            () -> imageFileData64(Paths.get("./pom.xml").toFile())
        ).withMessage("could not detect the mime-type of file './pom.xml' by extension '.xml'");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testLoadFileAsText() {
        assertThatIllegalStateException().isThrownBy(() -> loadFileAsText((File)null))
            .withMessage("could not load the file << NULL >> as text, because it does not exist or it's not a normal file");
        assertThatIllegalStateException().isThrownBy(() -> loadFileAsText("./target"))
            .withMessage("could not load the file './target' as text, because it does not exist or it's not a normal file");
        assertThatIllegalStateException().isThrownBy(() -> loadFileAsText("./target/classes/org/krmdemo/techlabs/core/CoreUtilsMain.class"))
            .withMessageEndingWith("because of IOException");

    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testLoadFileAsBytes() {
        assertThatIllegalStateException().isThrownBy(() -> loadFileAsBytes(null))
            .withMessage("could not load the file << NULL >> as bytes, because it does not exist or it's not a normal file");
        assertThatIllegalStateException().isThrownBy(() -> loadFileAsBytes(Paths.get("./target").toFile()))
            .withMessage("could not load the file './target' as bytes, because it does not exist or it's not a normal file");

        File licenseFile = Paths.get("./LICENSE").toFile();
        try (MockedStatic<Files> mockFiles = mockStatic(Files.class)) {
            mockFiles.when(() -> Files.readAllBytes(any())).thenThrow(new IOException("test IOException"));
            assertThatIllegalStateException().isThrownBy(() -> loadFileAsBytes(licenseFile))
                .withMessage("could not load the file './LICENSE' as bytes, because of IOException");
        }
        byte[] contentBytes = loadFileAsBytes(licenseFile);
        List<String> contentLines = loadFileLines(licenseFile.toPath());
        byte[] contentLinesBytes = concat(
            contentLines.stream(),
            Stream.of("")  // <-- the last lines-separator is lost when loading as lines
        ).collect(multiLine()).getBytes();
        assertThat(contentBytes).isEqualTo(contentLinesBytes);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testLoadFileLines() {
        assertThatIllegalStateException().isThrownBy(() -> loadFileLines(null))
            .withMessage("the path to file to load lines MUST NOT be null");
        assertThatIllegalStateException().isThrownBy(() -> loadFileLines(Paths.get("src/test/resources/jpeg-samples/NYC-Liberty.jpg")))
            .withMessage("could not load the file 'src/test/resources/jpeg-samples/NYC-Liberty.jpg' as list of lines");

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

    // --------------------------------------------------------------------------------------------

    @Test
    void testCreationIsProhibited() {
        UnsupportedOperationException uoEx = assertThrows(UnsupportedOperationException.class,
            () -> CorePropsUtils.newInstance(CoreFileUtils.class)
        );
        assertThat(uoEx.getMessage()).isEqualTo(
            "Cannot instantiate utility-class org.krmdemo.techlabs.core.utils.CoreFileUtils");
    }
}
