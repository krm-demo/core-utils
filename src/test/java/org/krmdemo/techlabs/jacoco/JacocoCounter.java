package org.krmdemo.techlabs.jacoco;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import static org.krmdemo.techlabs.core.dump.DumpUtils.dumpAsYamlTxt;

/**
 * {@link EnumMap EnumMap&lt;JacocoCounterType,JacocoCounter.Item&gt;} wrapper
 * over un-typed serialized sequence of {@link String}-values
 * to represent <b>JaCoCo</b>-counter per each {@link JacocoCounterType tyoe}.
 */
public class JacocoCounter extends EnumMap<JacocoCounterType, JacocoCounter.Item> {

    @Serial
    private static final long serialVersionUID = 123456789_003L;

    /**
     * Empty counter (used as null-safe placeholder to display {@value NO_VALUE_STR})
     */
    public final static JacocoCounter EMPTY_COUNTER = new JacocoCounter();

    /**
     * A badge-value to display the absence of information about the test-coverage
     */
    public final static String NO_VALUE_STR = "n/a";

    public record Item(
        int missed,
        int covered
    ) {
        @JsonGetter
        public int total() {
            return missed + covered;
        }
        @JsonGetter
        public double percentage() {
            return total() == 0 ? Double.NaN : 100.0 * covered() / total();
        }
        @JsonGetter
        public String percentageStr() {
            return total() == 0 ? NO_VALUE_STR : String.format("%.2f", percentage());
        }
    }

    /**
     * Force using factory-method {@link #fromItems(Collection)}
     */
    private JacocoCounter() {
        super(JacocoCounterType.class);
    }

    public static JacocoCounter fromItems(Collection<Map<String, Object>> itemValuesList) {
        if (itemValuesList == null || itemValuesList.isEmpty()) {
            return EMPTY_COUNTER;
        }
        JacocoCounter counter = new JacocoCounter();
        for (Map<String, Object> itemValues : itemValuesList) {
            JacocoCounterType counterType = JacocoCounterType.valueOf(itemValues.get("type"));
            counter.put(counterType, fromItemValues(itemValues));
        }
        return counter;
    }

    public static Item fromItemValues(Map<String, Object> itemValues) {
        int missed = Integer.parseInt("" + itemValues.get("missed"));
        int covered = Integer.parseInt("" + itemValues.get("covered"));
        return new Item(missed, covered);
    }

    /**
     * At the moment the badge-value corresponds to percentage-value of type {@link JacocoCounterType#LINE}
     *
     * @return a coverage percentage to be displayed on the right side of the coverage-badge
     */
    public String getBadgeValue() {
        Item counterForLines = get(JacocoCounterType.LINE);
        return counterForLines == null ? NO_VALUE_STR : counterForLines.percentageStr();
    }

    /**
     * @return a tooltip, that explains the value of {@link #getBadgeValue()}
     */
    public String getBadgeTooltip() {
        Item counterForLines = get(JacocoCounterType.LINE);
        return counterForLines == null ? "" : String.format("JaCoCo %s: 100 * %d / (%d + %d) = %f | %s",
            JacocoCounterType.LINE,
            counterForLines.covered(), counterForLines.covered(), counterForLines.missed(),
            counterForLines.percentage(),
            getBadgeValueRange().name());
    }

    /**
     * @return a range, that correspond to background-color of badge-value
     */
    public JacocoPercentageRange getBadgeValueRange() {
        Item counterForLines = get(JacocoCounterType.LINE);
        return counterForLines == null ? JacocoPercentageRange.UNKNOWN :
            JacocoPercentageRange.fromPercentageValue(counterForLines.percentage());
    }

    @Override
    public String toString() {
        return isEmpty() ? NO_VALUE_STR : dumpAsYamlTxt(this);
    }
}
