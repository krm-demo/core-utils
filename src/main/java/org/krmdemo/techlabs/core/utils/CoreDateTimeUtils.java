package org.krmdemo.techlabs.core.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.zone.ZoneRules;

public class CoreDateTimeUtils {

    public static ZoneOffset systemZoneOffset() {
        return ZoneId.systemDefault().getRules().getOffset(Instant.now());
    }
}
