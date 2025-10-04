package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.Test;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A unit-test for Java-record {@link VersionTag}
 */
public class VersionTagTest {

    @Test
    void parseInvalidVersion() {
        assertThat(VersionTag.parse(null)).isNull();
        assertThat(VersionTag.parse("")).isNull();
        assertThat(VersionTag.parse(" ")).isNull();
        assertThat(VersionTag.parse("\n")).isNull();
        assertThat(VersionTag.parse("\t \n")).isNull();
        assertThat(VersionTag.parse("la-la-la")).isNull();
        assertThat(VersionTag.parse("1.2.3-Some-Qualifier")).isNull();
        assertThat(VersionTag.parse("01.02.03-SNAPSHOT")).isNull();
    }

    @Test
    void parseInternalRelease() {
        assertThat(VersionTag.parse("1.2.3.Some.Qualifier"))
            .isEqualTo(new VersionTag("1", "2", "3.Some.Qualifier"));
        assertThat(VersionTag.parse("1.2.3"))
            .isEqualTo(new VersionTag("1", "2", "3"));
        assertThat(VersionTag.parse("01.02.03"))
            .isEqualTo(new VersionTag("01", "02", "03"));
        assertThat(VersionTag.parse("01.02.003"))
            .isEqualTo(new VersionTag("01", "02", "003"));
    }

    @Test
    void parsePublicRelease() {
        assertThat(VersionTag.parse("1.2+3+Some+Qualifier"))
            .isEqualTo(new VersionTag("1", "2+3+Some+Qualifier", ""));
        assertThat(VersionTag.parse("1.2"))
            .isEqualTo(new VersionTag("1", "2", ""));
        assertThat(VersionTag.parse("01.02"))
            .isEqualTo(new VersionTag("01", "02", ""));
    }

    @Test
    void parseIsValid() {
        assertThat(requireNonNull(VersionTag.parse("1.2")).isValid()).isTrue();
        assertThat(requireNonNull(VersionTag.parse("01.02")).isValid()).isTrue();
        assertThat(requireNonNull(VersionTag.parse("1.2.3")).isValid()).isTrue();
        assertThat(requireNonNull(VersionTag.parse("01.02.003")).isValid()).isTrue();
    }

    @Test
    void parseIsNotValid() {
        assertThat(requireNonNull(VersionTag.parse("1.2222222222222222222")).isValid()).isFalse();
        assertThat(requireNonNull(VersionTag.parse("01.abc")).isValid()).isFalse();
        assertThat(requireNonNull(VersionTag.parse("1.2.abc")).isValid()).isFalse();
        assertThat(requireNonNull(VersionTag.parse("01.02-SNAPSHOT")).isValid()).isFalse();
    }
}
