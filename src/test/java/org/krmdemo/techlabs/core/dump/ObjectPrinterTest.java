package org.krmdemo.techlabs.core.dump;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.commons.util.StringUtils;
import org.krmdemo.techlabs.core.dump.render.Highlight;
import org.krmdemo.techlabs.core.dump.render.RenderSpec;
import picocli.CommandLine.Help.Ansi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedSet;
import java.util.SortedSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toSortedSet;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedSet;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;

/**
 * A unit-test to verify the implementations of {@link ObjectPrinter} interface
 * <hr/>
 * TODO: Cover the output of list and map to file
 */
public class ObjectPrinterTest {

    @Test
    void testAnsi() {
        assertThat(Ansi.AUTO.enabled()).isTrue();  // <-- it's possible because of beforeAll()
        assertThat(Ansi.AUTO.string("this @|blue text|@ is blue")).isEqualTo(
            "this \u001B[34mtext\u001B[39m\u001B[0m is blue"
        );
    }

    @Test
    void testBooleans(TestInfo testInfo) {
        System.out.printf("---- %s (started): ----%n", testInfo.getDisplayName());

        // the order of properties is necessary to guarantee the same predefined dump
        @JsonPropertyOrder({"boolOne", "boolTwo", "boolThree", "boolList", "boolArr"})
        record MyBools(Boolean boolOne, Boolean boolTwo, Boolean boolThree) {
            @JsonGetter List<Boolean> boolList() {
                return Stream.of(boolOne, boolTwo, boolThree).toList();
            }
            @JsonGetter boolean[] boolArr() {
                List<Boolean> listNonNull = boolList().stream().filter(Objects::nonNull).toList();
                boolean[] arr = new boolean[listNonNull.size()];
                for (int i = 0; i < listNonNull.size(); i++) {
                    arr[i] = listNonNull.get(i);
                }
                return arr;
            }
        }

        SequencedSet<MyBools> linkedSetMyBools = linkedSet(
            new MyBools(true, false, null),
            new MyBools(true, null, true),
            new MyBools(false, null, null),
            new MyBools(null, null, null),
            null
        );
        System.out.printf("linkedSetMyBools (as YAML):%n%s%n",
            DumpUtils.dumpAsYamlTxt(linkedSetMyBools, new RenderSpec(Highlight.DEFAULT)));
        System.out.println("~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~");
        System.out.printf("linkedSetMyBools --(as JSON)--> %s%n",
            DumpUtils.dumpAsJsonTxt(linkedSetMyBools, new RenderSpec(Highlight.DEFAULT)));
        assertThat(linkedSetMyBools).hasSize(5);

        // verifying several rendered results with different output structure and target format
        verifyRendered(linkedSetMyBools, testInfo.getTestMethod().orElseThrow(), "linkedSetMyBools");

        SortedSet<MyBools> sortedSetMyBools = linkedSetMyBools.stream()
            .filter(Objects::nonNull)
            .collect(toSortedSet(Comparator.comparing(MyBools::toString)));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.printf("sortedSetMyBools (as YAML):%n%s%n",
            DumpUtils.dumpAsYamlTxt(sortedSetMyBools, new RenderSpec(Highlight.DEFAULT)));
        System.out.println("~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~");
        System.out.printf("sortedSetMyBools --(as JSON)--> %s%n",
            DumpUtils.dumpAsJsonTxt(sortedSetMyBools, new RenderSpec(Highlight.DEFAULT)));
        assertThat(sortedSetMyBools).hasSize(4);

        // verifying several rendered results with different output structure and target format
        verifyRendered(sortedSetMyBools, testInfo.getTestMethod().orElseThrow(), "sortedSetMyBools");

        System.out.printf("... %s (finished). ...%n", testInfo.getDisplayName());
    }

    /**
     * An example of Java-record to demonstrate the usage of Jackson-annotations
     * <hr/>
     * The annotation {@link JsonPropertyOrder} is necessary to guarantee the same predefined dump,
     * which could be un-predictable at different version of JVM if not specified.
     *
     * @param degrees an integer value of angle in degrees
     * @param radians a floating-point value of angle in radians
     */
    @JsonPropertyOrder({"degrees", "radians", "formula-sinus", "formula-cosinus", "formulas-result-map"})
    record Angle(int degrees, double radians) {
        /**
         * @param partOfCircle a part of circle (which is better to be a divisor of {@code 360})
         */
        Angle(int partOfCircle) {
            this (360 / partOfCircle, 2 * Math.PI / partOfCircle);
        }
        double sinusValue() {
            return cleanupError(Math.sin(radians));
        }
        double cosinusValue() {
            return cleanupError(Math.cos(radians));
        }

        @JsonGetter("formula-sinus")
        String formulaSinus() {
            return String.format("sin(%d°) = sin(~%.3f) = %.4f", degrees, radians, sinusValue());
        }

        @JsonGetter("formula-cosinus")
        String formulaCosinus() {
            return String.format("cos(%d°) = cos(~%.3f) = %.4f", degrees, radians, cosinusValue());
        }

        @JsonGetter("formulas-result-map")
        Map<String, Double> formulasMap() {
            return linkedMap(
                nameValue("sin", sinusValue()),
                nameValue("cos", cosinusValue())
            );
        }
    }

    private final Angle[] anglesArr = IntStream.of(2, 3, 4, 6, 8, 9, 12, 18)
        .mapToObj(Angle::new).toArray(Angle[]::new);

    @Test
    void testMapOfLists(TestInfo testInfo) {
        System.out.printf("---- %s (started): ----%n", testInfo.getDisplayName());
        Map<String, Object> mapOfLists = linkedMap(
            (Map.Entry<String,Object>)null, // <-- this entry must be filtered out, because it's null
            nameValue(null, "la-la-la"),  // <-- this also should be filtered out because the key is null
            nameValue("null-value", null),  // <-- when the value is null - the entry is also filtered out
            nameValue("empty-list", Collections.emptyList()),
            nameValue("singleton-null", Collections.singletonList(null)),
            nameValue("angles", Arrays.stream(anglesArr).map(Angle::degrees).toList()),
            nameValue("sinuses", Arrays.stream(anglesArr).map(Angle::sinusValue).toList()),
            nameValue("cosinuses", Arrays.stream(anglesArr).map(Angle::cosinusValue).toList()),
            nameValue("tangents", Arrays.stream(anglesArr).map(angle -> {
                    double sinus = angle.sinusValue();
                    double cosinus = angle.cosinusValue();
                    return cosinus == 0.0 ? null : sinus / cosinus;
                }).toList()
            ),
            nameValue("cotangents", Arrays.stream(anglesArr).map(angle -> {
                    double sinus = angle.sinusValue();
                    double cosinus = angle.cosinusValue();
                    return sinus == 0.0 ? null : cosinus / sinus;
                }).toList()
            )
        );
        PrintUtils.printAsJsonTxt(mapOfLists, new RenderSpec(Highlight.DEFAULT));
        System.out.println();
        System.out.println("~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~");
        PrintUtils.printAsYamlTxt(mapOfLists, new RenderSpec(Highlight.DEFAULT));
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        assertThat(mapOfLists).hasSize(7);
        PrintUtils.printAsJsonHtml(mapOfLists, new RenderSpec(Highlight.DEFAULT));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        PrintUtils.printAsJsonHtml(mapOfLists, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC));

        // verifying several rendered results with different output structure and target format
        verifyRendered(mapOfLists, testInfo.getTestMethod().orElseThrow(), "mapOfLists");

        System.out.printf("... %s (finished). ...%n", testInfo.getDisplayName());
    }


    @Test
    void testSingleRecord(TestInfo testInfo) {
        System.out.printf("--- %s: ---%n", testInfo.getDisplayName());
        PrintUtils.printAsJsonTxt(anglesArr[5], new RenderSpec(Highlight.DEFAULT));
        System.out.println();
        System.out.println("~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~");
        PrintUtils.printAsYamlTxt(anglesArr[5], new RenderSpec(Highlight.DEFAULT));
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        assertThat(DumpUtils.dumpAsJsonTxt(anglesArr[5])).isEqualTo("""
            {
              "degrees": "40",
              "radians": "0.6981317007977318",
              "formula-sinus": "sin(40°) = sin(~0.698) = 0.6428",
              "formula-cosinus": "cos(40°) = cos(~0.698) = 0.7660",
              "formulas-result-map": {
                "sin": "0.642787609687",
                "cos": "0.766044443119"
              }
            }""");
        PrintUtils.printAsJsonHtml(anglesArr[5], new RenderSpec(Highlight.DEFAULT));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        PrintUtils.printAsJsonHtml(anglesArr[5], new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC));

        // verifying several rendered results with different output structure and target format
        verifyRendered(anglesArr[5], testInfo.getTestMethod().orElseThrow());

        System.out.printf("... %s (finished). ...%n", testInfo.getDisplayName());
    }

    @Test
    void testArrayOfRecords(TestInfo testInfo) {
        System.out.printf("--- %s: ---%n", testInfo.getDisplayName());
        PrintUtils.printAsJsonTxt(anglesArr, new RenderSpec(Highlight.DEFAULT));
        System.out.println();
        System.out.println("~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~  ~~");
        PrintUtils.printAsYamlTxt(anglesArr, new RenderSpec(Highlight.DEFAULT));
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        PrintUtils.printAsJsonHtml(anglesArr, new RenderSpec(Highlight.DEFAULT));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        PrintUtils.printAsJsonHtml(anglesArr, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC));

        // verifying several rendered results with different output structure and target format
        verifyRendered(anglesArr, testInfo.getTestMethod().orElseThrow());

        System.out.printf("... %s (finished). ...%n", testInfo.getDisplayName());
    }

    // --------------------------------------------------------------------------------------------

    private void verifyRendered(Object objToDump, Method testMethod) {
        verifyRendered(objToDump, testMethod.getName());
    }

    private void verifyRendered(Object objToDump, Method testMethod, String resourceFilePrefix) {
        verifyRendered(objToDump, testMethod.getName(), resourceFilePrefix);
    }

    private void verifyRendered(Object objToDump, String testMethodName) {
        verifyRendered(objToDump, testMethodName, testMethodName);
    }

    private void verifyRendered(Object objToDump, String testCaseName, String resourceFilePrefix) {
        Path resourcePathJson = Path.of(testCaseName, resourceFilePrefix + "--expected.json");
        Path resourcePathYaml = Path.of(testCaseName, resourceFilePrefix + "--expected.yaml");
        Path resourcePathJsonHtml = Path.of(testCaseName, resourceFilePrefix + "--expected.json.html");;
        Path resourcePathYamlHtml = Path.of(testCaseName, resourceFilePrefix + "--expected.yaml.html");
        Path resourcePathJsonSvg = Path.of(testCaseName, resourceFilePrefix + "--expected.json.svg");;
        Path resourcePathYamlSvg = Path.of(testCaseName, resourceFilePrefix + "--expected.yaml.svg");
        Path resourcePathJsonSvgImgHtml = Path.of(testCaseName, resourceFilePrefix + "--expected.json.svg.img.html");;
        Path resourcePathYamlSvgImgHtml = Path.of(testCaseName, resourceFilePrefix + "--expected.yaml.svg.img.html");

        assertThat(DumpUtils.dumpAsJsonTxt(objToDump, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent(resourcePathJson));
        assertThat(DumpUtils.dumpAsYamlTxt(objToDump, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent(resourcePathYaml));

        assertThat(DumpUtils.dumpAsJsonHtml(objToDump, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent(resourcePathJsonHtml));
        assertThat(DumpUtils.dumpAsYamlHtml(objToDump, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent(resourcePathYamlHtml));

        assertThat(DumpUtils.dumpAsJsonSvg(objToDump, new RenderSpec(Highlight.DEFAULT)))
            .isEqualTo(resourceContent(resourcePathJsonSvg));
        assertThat(DumpUtils.dumpAsJsonSvg(objToDump, new RenderSpec(
            Highlight.DEFAULT,
            RenderSpec.Feature.RENDER_HTML_DOC,
            RenderSpec.Feature.EMBEDDED_SVG_IMG)))
            .isEqualTo(resourceContent(resourcePathJsonSvgImgHtml));

        assertThat(DumpUtils.dumpAsYamlSvg(objToDump, new RenderSpec(Highlight.DEFAULT)))
            .isEqualTo(resourceContent(resourcePathYamlSvg));
        assertThat(DumpUtils.dumpAsYamlSvg(objToDump, new RenderSpec(
            Highlight.DEFAULT,
            RenderSpec.Feature.RENDER_HTML_DOC,
            RenderSpec.Feature.EMBEDDED_SVG_IMG)))
            .isEqualTo(resourceContent(resourcePathYamlSvgImgHtml));
    }

    // --------------------------------------------------------------------------------------------

    private String resourceContent(Path resourcePath) {
        return resourceContent(Objects.requireNonNull(resourcePath).toString());
    }

    private String resourceContent(String resourcePathStr) {
        if (StringUtils.isBlank(resourcePathStr)) {
            throw new IllegalArgumentException("'resourcePathStr' is blank");
        }
        URL resourceURL = getClass().getResource(resourcePathStr);
        assertThat(resourceURL)
            .describedAs(
                String.format("resource of class %s with path '%s'",
                    getClass().getSimpleName(), resourcePathStr))
            .isNotNull();
        try (InputStream resourceStream = resourceURL.openStream()) {
            return IOUtils.toString(resourceStream, Charset.defaultCharset());
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(String.format(
                "could not read the content of resource '%s' for class %s",
                resourcePathStr, getClass().getSimpleName()), ioEx);
        }
    }

    /**
     * @param originalValue original value to clean up the error of
     * @return the value without error (rounding to <b>{@code 12}</b> digits after floating-point)
     */
    private static double cleanupError(double originalValue) {
        double factor = Math.pow(10, 12);
        return Math.round(originalValue * factor) / factor;
    }

    private final static String SYS_PROP_PICOCLI_ANSI = "picocli.ansi";
    private static final String picocliAnsiBefore = System.getProperty(SYS_PROP_PICOCLI_ANSI);
    @BeforeAll
    static void beforeAll() {
        System.setProperty(SYS_PROP_PICOCLI_ANSI, "true");
    }
    @AfterAll
    static void afterAll() {
        if (picocliAnsiBefore != null) {
            System.setProperty(SYS_PROP_PICOCLI_ANSI, picocliAnsiBefore);
        }
    }
}
