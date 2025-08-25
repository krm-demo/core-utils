package org.krmdemo.techlabs.stream;

import java.util.NavigableMap;
import java.util.TreeMap;

import static org.krmdemo.techlabs.stream.TechlabsCollectors.toPropValue;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.propValue;
import static org.krmdemo.techlabs.stream.TechlabsStreamUtils.sortedMap;

public class SysDumpUtils {

    /**
     * @return Java system-properties as a sorted-map
     */
    public static NavigableMap<String,String> dumpSysProps() {
        return sortedMap(System.getProperties().entrySet().stream().map(toPropValue()));
    }

    /**
     * @return environment variables as a sorted-map
     */
    public static NavigableMap<String,String> dumpEnvVars() {
        return new TreeMap<>(System.getenv());
    }
}
