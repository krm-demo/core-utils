package org.krmdemo.techlabs.core.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.linkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStreamUtils.nameValue;

/**
 * An example of Java-record to demonstrate the usage of Jackson-annotations
 * <hr/>
 * The annotation {@link JsonPropertyOrder} is necessary to guarantee the same predefined dump,
 * which could be un-predictable at different version of JVM if not specified.
 *
 * @param degrees an integer value of angle in degrees
 * @param radians a floating-point value of angle in radians
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"degrees", "radians", "formula-sinus", "formula-cosinus", "formulas-result-map"})
public record Angle(int degrees, double radians) {

    /**
     * @param partOfCircle a part of circle (which is better to be a divisor of {@code 360})
     */
    public Angle(int partOfCircle) {
        this(360 / partOfCircle, 2 * Math.PI / partOfCircle);
    }

    public double sinusValue() {
        return cleanupPrecisionError(Math.sin(radians));
    }

    public double cosinusValue() {
        return cleanupPrecisionError(Math.cos(radians));
    }

    @JsonGetter("formula-sinus")
    public String formulaSinus() {
        return String.format("sin(%d°) = sin(~%.3f) = %.4f", degrees, radians, sinusValue());
    }

    @JsonGetter("formula-cosinus")
    public String formulaCosinus() {
        return String.format("cos(%d°) = cos(~%.3f) = %.4f", degrees, radians, cosinusValue());
    }

    @JsonGetter("formulas-result-map")
    public Map<String, Double> formulasMap() {
        return linkedMap(
            nameValue("sin", sinusValue()),
            nameValue("cos", cosinusValue())
        );
    }

    /**
     * @param originalValue original value to clean up the precision garbage of
     * @return the value without error (rounding to <b>{@code 12}</b> digits after floating-point)
     */
    private static double cleanupPrecisionError(double originalValue) {
        double factor = Math.pow(10, 12);
        return Math.round(originalValue * factor) / factor;
    }
}
