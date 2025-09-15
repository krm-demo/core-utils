package org.krmdemo.techlabs.dump;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine.Help.Ansi;

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
    record AngleRecord(int degrees, double radians) {
        /**
         * @param partOfCircle a part of circle (which is better to be a divisor of {@code 360})
         */
        AngleRecord(int partOfCircle) {
            this (360 / partOfCircle, 2 * Math.PI / partOfCircle);
        }
        double sinusValue() {
            return Math.sin(radians);
        }
        double cosinusValue() {
            return Math.cos(radians);
        }
        @JsonGetter("formula-sinus")
        String formulaSinus() {
            return String.format("sin(%d°) = sin(~%.3f) = %.4f", degrees, radians, sinusValue());
        }
        @JsonGetter("formula-cosinus")
        String formulaCosinus() {
            return String.format("cos(%d°) = cos(~%.3f) = %.4f", degrees, radians, cosinusValue());
        }
        @JsonGetter("formulas-map")
        Map<String, Double> formulasMap() {
            return linkedMap(
                nameValue("sin", sinusValue()),
                nameValue("cos", cosinusValue())
            );
        }
    }

    private final AngleRecord[] anglesArr = IntStream.of(2, 3, 4, 6, 8, 9, 12, 18)
        .mapToObj(AngleRecord::new).toArray(AngleRecord[]::new);

    @Test
    void testAnsi() {
        assertThat(Ansi.AUTO.enabled()).isTrue();  // <-- it's possible because of beforeAll()
        assertThat(Ansi.AUTO.string("this @|blue text|@ is blue")).isEqualTo(
            "this \u001B[34mtext\u001B[39m\u001B[0m is blue"
        );
    }

    @Test
    void testSingleRecord() {
        PrintUtils.printAsJsonTxt(anglesArr[5], AnsiHighlighter.DEFAULT);
        assertThat(DumpUtils.dumpAsJsonTxt(anglesArr[5])).isEqualTo("""
            {
              "degrees": "40",
              "radians": "0.6981317007977318",
              "formula-sinus": "sin(40°) = sin(~0.698) = 0.6428",
              "formula-cosinus": "cos(40°) = cos(~0.698) = 0.7660",
              "formulas-map": {
                "sin": "0.6427876096865393",
                "cos": "0.766044443118978"
              }
            }""");
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
