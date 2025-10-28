package org.krmdemo.techlabs.jacoco;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
            return total() == 0 ? "n/a" : String.format("%.2f", percentage());
        }
    }

    /**
     * Force using factory-method {@link #fromItems(Collection)}
     */
    private JacocoCounter() {
        super(JacocoCounterType.class);
    }

    public static JacocoCounter fromItems(Collection<Map<String, Object>> itemValuesList) {
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

    @Override
    public String toString() {
        return dumpAsYamlTxt(this);
    }
}
