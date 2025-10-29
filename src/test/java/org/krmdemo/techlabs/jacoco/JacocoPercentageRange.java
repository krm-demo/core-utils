package org.krmdemo.techlabs.jacoco;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.nio.file.Path;

/**
 * This enumeration represents the names and the hexadecimal color-values for the coverage-percentage range.
 * <hr/>
 * This approach (to give the name and color to range) was first introduced by
 * <a href="https://docs.gitlab.com/">GitLab</a> and now it's considered kind of IT-industry standard.
 *
 * @see <a href="https://docs.gitlab.com/user/project/badges/#test-coverage-report-badges">
 *     (GitLab Docs) Test coverage limits and badge colors
 * </a>
 */
public enum JacocoPercentageRange {

    /**
     * coverage-percentage value is in range {@code [ 95; 100 ]}
     */
    GOOD("Good", "95 up to and including 100%", 95, "4c1"),

    /**
     * coverage-percentage value is in range {@code [ 90; 95 )}
     */
    ACCEPTABLE("Acceptable", "90 up to 95%", 90, "a3c51c"),

    /**
     * coverage-percentage value is in range {@code [ 75; 90 )}
     */
    MEDIUM("Medium", "75 up to 90%", 75, "dfb317"),

    /**
     * coverage-percentage value is in range {@code [ 0; 75 )}
     */
    LOW("Low", "0 up to 75%", 0, "e05d44"),

    /**
     * the coverage-percentage is unknown or invalid
     */
    UNKNOWN("Unknown", "unknown range", -1, "9f9f9f");

    @Getter private final String displayName;
    @Getter private final String description;
    @Getter private final int percentageLimit;
    @Getter private final String hexValue;

    JacocoPercentageRange(
        String displayName,
        String description,
        int percentageLimit,
        String hexValue
    ) {
        this.displayName = displayName;
        this.description = description;
        this.percentageLimit = percentageLimit;
        this.hexValue = hexValue;
    }

    /**
     * @return {@link #hexValue} prepended by hash{@code '#'}-symbol
     */
    public String hexValueHash() {
        return '#' + hexValue;
    }

    /**
     * @param percValue the value of coverage-percentage
     * @return the detected percentage range as {@link JacocoPercentageRange}
     */
    public static JacocoPercentageRange fromPercentageValue(Double percValue) {
        if (percValue == null || percValue.isNaN() || percValue.isInfinite()
            || percValue < 0 || percValue > 100) {
            return UNKNOWN;
        }
        if (percValue >= GOOD.percentageLimit) {
            return GOOD;
        } else if (percValue >= ACCEPTABLE.percentageLimit) {
            return ACCEPTABLE;
        } else if (percValue >= MEDIUM.percentageLimit) {
            return MEDIUM;
        } else {
            return LOW;
        }
    }

    @Override
    @JsonValue
    public String toString() {
        return String.format("( %s: '%s' %s )", displayName, description, hexValueHash());
    }
}
