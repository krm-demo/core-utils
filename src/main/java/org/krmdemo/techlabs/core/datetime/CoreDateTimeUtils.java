package org.krmdemo.techlabs.core.datetime;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class CoreDateTimeUtils {

    public static ZoneOffset systemZoneOffset() {
        return ZoneId.systemDefault().getRules().getOffset(Instant.now());
    }
}
