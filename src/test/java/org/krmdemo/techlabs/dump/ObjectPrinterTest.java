package org.krmdemo.techlabs.dump;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import picocli.CommandLine.Help.Ansi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.stream.CoreStreamUtils.nameValue;

/**
 * A unit-test to verify the abilities of implementations of {@link ObjectPrinter} interface
 * <hr/>
 * TODO: Cover the output of list and map to file
 */
public class ObjectPrinterTest {

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
    void testAnsi() {
        assertThat(Ansi.AUTO.enabled()).isTrue();  // <-- it's possible because of beforeAll()
        assertThat(Ansi.AUTO.string("this @|blue text|@ is blue")).isEqualTo(
            "this \u001B[34mtext\u001B[39m\u001B[0m is blue"
        );
    }

    @Test
    void testResultLists(TestInfo testInfo) {
        System.out.printf("---- %s (started): ----%n", testInfo.getDisplayName());
        Map<String, Object> resultLists = linkedMap(
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
        PrintUtils.printAsJsonTxt(resultLists, AnsiHighlighter.DEFAULT);
        assertThat(resultLists).hasSize(7);
        System.out.printf("%n... %s (finished). ...%n", testInfo.getDisplayName());
    }


    @Test
    void testSingleRecord(TestInfo testInfo) {
        System.out.printf("--- %s: ---%n", testInfo.getDisplayName());
        PrintUtils.printAsJsonTxt(anglesArr[5], AnsiHighlighter.DEFAULT);
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
        System.out.printf("%n... %s (finished). ...%n", testInfo.getDisplayName());
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
