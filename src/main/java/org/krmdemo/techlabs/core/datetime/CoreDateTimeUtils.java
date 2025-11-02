package org.krmdemo.techlabs.core.datetime;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class CoreDateTimeUtils {

    /**
     * Gets the current value of {@link ZoneOffset}, which depends on system settings of operational system
     * and on the current time (because of <a href="https://en.wikipedia.org/wiki/Daylight_saving_time">daylight saving time</a>).
     *
     * @return the current value of {@link ZoneOffset}
     */
    public static ZoneOffset systemZoneOffset() {
        return ZoneId.systemDefault().getRules().getOffset(Instant.now());
    }

    /**
     * Transform the {@link String}-representation of {@link Instant} into {@link DateTimeTriplet}
     *
     * @param instantStr string-representation of {@link Instant} in format like {@code 2007-12-03T10:15:30.00Z}.
     * @return the instance of {@link DateTimeTriplet}
     *
     * @see Instant#parse(CharSequence)
     */
    public static DateTimeTriplet dtt(String instantStr) {
        return new DateTimeTriplet(Instant.parse(instantStr));
    }
}
