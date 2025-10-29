package org.krmdemo.techlabs.jacoco;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.dump.DumpUtils.dumpAsYamlTxt;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toSortedMap;
import static org.krmdemo.techlabs.jacoco.JacocoPercentageRange.fromPercentageValue;

public class JacocoPercentageRangeTest {

    @Test
    void testPercentageLimits_InvalidRange() {
        assertThat(fromPercentageValue(-0.1)).isEqualTo(JacocoPercentageRange.UNKNOWN);
        assertThat(fromPercentageValue(123.0)).isEqualTo(JacocoPercentageRange.UNKNOWN);
        assertThat(fromPercentageValue(null)).isEqualTo(JacocoPercentageRange.UNKNOWN);
        assertThat(fromPercentageValue(Double.NaN)).isEqualTo(JacocoPercentageRange.UNKNOWN);
        assertThat(fromPercentageValue(Double.NEGATIVE_INFINITY)).isEqualTo(JacocoPercentageRange.UNKNOWN);
        assertThat(fromPercentageValue(Double.POSITIVE_INFINITY)).isEqualTo(JacocoPercentageRange.UNKNOWN);
    }

    @Test
    void testPercentageLimits_ValidRange() {
        Map<Double, JacocoPercentageRange> limitsMap = Stream.of(
            0.0, 15.0, 35.789, 65.0, 75.0, 82.034, 85.0, 90.0, 93.6, 95.0, 99.999, 100.00
            ).collect(toSortedMap(
                Function.identity(),
                JacocoPercentageRange::fromPercentageValue
            ));
        assertThat(dumpAsYamlTxt(limitsMap)).isEqualTo("""
            0.0: ( Low: '0 up to 75%' #e05d44 )
            15.0: ( Low: '0 up to 75%' #e05d44 )
            35.789: ( Low: '0 up to 75%' #e05d44 )
            65.0: ( Low: '0 up to 75%' #e05d44 )
            75.0: ( Medium: '75 up to 90%' #dfb317 )
            82.034: ( Medium: '75 up to 90%' #dfb317 )
            85.0: ( Medium: '75 up to 90%' #dfb317 )
            90.0: ( Acceptable: '90 up to 95%' #a3c51c )
            93.6: ( Acceptable: '90 up to 95%' #a3c51c )
            95.0: ( Good: '95 up to and including 100%' #4c1 )
            99.999: ( Good: '95 up to and including 100%' #4c1 )
            100.0: ( Good: '95 up to and including 100%' #4c1 )""");
    }
}
