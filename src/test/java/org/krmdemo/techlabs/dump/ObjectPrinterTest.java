package org.krmdemo.techlabs.dump;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.krmdemo.techlabs.dump.render.Highlight;
import org.krmdemo.techlabs.dump.render.RenderSpec;
import picocli.CommandLine.Help.Ansi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
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
import static org.krmdemo.techlabs.stream.CoreCollectors.toSortedSet;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.linkedSet;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.nameValue;

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

        assertThat(DumpUtils.dumpAsJsonTxt(linkedSetMyBools, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("linkedSetMyBools--expected.json"));
        assertThat(DumpUtils.dumpAsYamlTxt(linkedSetMyBools, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("linkedSetMyBools--expected.yaml"));

        assertThat(DumpUtils.dumpAsJsonHtml(linkedSetMyBools, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("linkedSetMyBools--expected.json.html"));
        assertThat(DumpUtils.dumpAsYamlHtml(linkedSetMyBools, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("linkedSetMyBools--expected.yaml.html"));

        assertThat(DumpUtils.dumpAsJsonSvg(linkedSetMyBools, new RenderSpec(Highlight.DEFAULT)))
            .isEqualTo(resourceContent("linkedSetMyBools--expected.json.svg"));

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
        assertThat(DumpUtils.dumpAsJsonTxt(sortedSetMyBools, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("sortedSetMyBools--expected.json"));
        assertThat(DumpUtils.dumpAsYamlTxt(sortedSetMyBools, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("sortedSetMyBools--expected.yaml"));
        assertThat(DumpUtils.dumpAsJsonHtml(sortedSetMyBools, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("sortedSetMyBools--expected.json.html"));
        assertThat(DumpUtils.dumpAsYamlHtml(sortedSetMyBools, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("sortedSetMyBools--expected.yaml.html"));
        System.out.printf("... %s (finished). ...%n", testInfo.getDisplayName());
    }

    /**
     * An example of Java-record to demonstrate the usage of Jackson-annotations
     *
     * @param degrees an integer value of angle in degrees
     * @param radians a floating-point value of angle in radians
     */
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
        assertThat(DumpUtils.dumpAsJsonTxt(mapOfLists, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("mapOfLists--expected.json"));
        assertThat(DumpUtils.dumpAsYamlTxt(mapOfLists, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("mapOfLists--expected.yaml"));
        assertThat(DumpUtils.dumpAsJsonHtml(mapOfLists, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("mapOfLists--expected.json.html"));
        assertThat(DumpUtils.dumpAsYamlHtml(mapOfLists, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("mapOfLists--expected.yaml.html"));
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
        assertThat(DumpUtils.dumpAsJsonTxt(anglesArr[5], new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("testSingleRecord--expected.json"));
        assertThat(DumpUtils.dumpAsYamlTxt(anglesArr[5], new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("testSingleRecord--expected.yaml"));
        assertThat(DumpUtils.dumpAsJsonHtml(anglesArr[5], new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("testSingleRecord--expected.json.html"));
        assertThat(DumpUtils.dumpAsYamlHtml(anglesArr[5], new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("testSingleRecord--expected.yaml.html"));
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

        assertThat(DumpUtils.dumpAsJsonTxt(anglesArr, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("testArrayOfRecords--expected.json"));
        assertThat(DumpUtils.dumpAsYamlTxt(anglesArr, new RenderSpec(Highlight.NONE)))
            .isEqualTo(resourceContent("testArrayOfRecords--expected.yaml"));
        assertThat(DumpUtils.dumpAsJsonHtml(anglesArr, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("testArrayOfRecords--expected.json.html"));
        assertThat(DumpUtils.dumpAsYamlHtml(anglesArr, new RenderSpec(Highlight.DEFAULT, RenderSpec.Feature.RENDER_HTML_DOC)))
            .isEqualTo(resourceContent("testArrayOfRecords--expected.yaml.html"));
        System.out.printf("... %s (finished). ...%n", testInfo.getDisplayName());
    }

    private String resourceContent(String resourcePath) {
        URL resourceURL = getClass().getResource(resourcePath);
        assertThat(resourceURL)
            .describedAs(
                String.format("resource of class %s with path '%s'",
                    getClass().getSimpleName(), resourcePath))
            .isNotNull();
        try (InputStream resourceStream = resourceURL.openStream()) {
            return IOUtils.toString(resourceStream, Charset.defaultCharset());
        } catch (IOException ioEx) {
            throw new IllegalArgumentException(String.format(
                "could not read the content of resource '%s' for class %s",
                resourcePath, getClass().getSimpleName()), ioEx);
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
