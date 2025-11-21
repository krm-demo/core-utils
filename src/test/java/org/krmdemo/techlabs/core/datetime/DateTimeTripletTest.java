package org.krmdemo.techlabs.core.datetime;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils.parseAny;
import static org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils.parseEpochSeconds;
import static org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils.parseIsoInstant;
import static org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils.parseNatural;
import static org.krmdemo.techlabs.core.dump.DumpUtils.dumpAsJsonTxt;

/**
 * A unit-test for {@link DateTimeTriplet}
 */
public class DateTimeTripletTest {

    final DateTimeTriplet dttNow = DateTimeTriplet.now();

    @Test
    void testAgainst_DateTimeFormatter() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd (EEE) HH:mm");
        assertThat(dttNow.dump()).isEqualTo(dtf.format(dttNow.getLocalDateTime()));
        DateTimeFormatter dtfNoWeek = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        assertThat(dttNow.dumpNoWeek()).isEqualTo(dtfNoWeek.format(dttNow.getLocalDateTime()));
    }

    @Test
    void testAgainst_String_format() {
        LocalDateTime ldt = dttNow.getLocalDateTime();
//        System.out.printf("date: %tY-%tm-%td;%n", ldt, ldt, ldt);
//        System.out.printf("day of week: '%ta';%n", ldt);
//        System.out.printf("time: %tH:%tM:%tS;%n", ldt, ldt, ldt);
        assertThat(dttNow.dump()).isEqualTo(
            String.format("%1$tY-%1$tm-%1$td (%1$ta) %1$tH:%1$tM", ldt)
        );
        assertThat(dttNow.dumpNoWeek()).isEqualTo(
            String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", ldt)
        );
    }

    @Test
    void testIgnoreSeconds() {
        DateTimeTriplet dttNow_zeroSec = new DateTimeTriplet(dttNow.getLocalDateTime().withSecond(0));
        DateTimeTriplet dttNow_twelveSec = new DateTimeTriplet(dttNow.getLocalDateTime().withSecond(12));
        DateTimeTriplet dttNow_fortySec = new DateTimeTriplet(dttNow.getLocalDateTime().withSecond(40));
        assertThat(dttNow_zeroSec.getLocalDateTime().getSecond()).isZero();
        assertThat(dttNow_twelveSec.getLocalDateTime().getSecond()).isZero();
        assertThat(dttNow_fortySec.getLocalDateTime().getSecond()).isZero();
        assertThat(dttNow.dump()).isEqualTo(dttNow_zeroSec.dump());
        assertThat(dttNow.dump()).isEqualTo(dttNow_twelveSec.dump());
        assertThat(dttNow.dump()).isEqualTo(dttNow_fortySec.dump());
    }

    @Test
    void testParseEpochSeconds() {
        DateTimeTriplet dttParsed = parseEpochSeconds("" + dttNow.getEpochSeconds());
        assertThat(dttParsed.getLocalDateTime()).isEqualTo(dttNow.getLocalDateTime());
        assertThat(dttParsed).isEqualTo(dttNow);
    }

    @Test
    void testParseNatural() {
        DateTimeTriplet dttParsed = parseNatural(dttNow.dump());
        DateTimeTriplet dttParsedNoWeek = parseNatural(dttNow.dumpNoWeek());
        assertThat(dttParsed).isEqualTo(dttNow);
        assertThat(dttParsedNoWeek).isEqualTo(dttNow);
    }

    @Test
    void testParseIsoInstant() {
        DateTimeTriplet dttParsed = parseIsoInstant(dttNow.dumpIsoInstant());
        assertThat(dttParsed).isEqualTo(dttNow);
    }

    @Test
    void testParseAny() {
        List<String> dttNowStrList = Stream.of(
            dttNow.dump(),
            dttNow.dumpNoWeek(),
            "" + dttNow.getEpochSeconds(),
            dttNow.dumpIsoInstant()
        ).toList();
        List<DateTimeTriplet> dttNowList = dttNowStrList.stream()
            .map(CoreDateTimeUtils::parseAny)
            .toList();
        assertThat(dttNowList).hasSize(dttNowStrList.size());
        Set<DateTimeTriplet> dttNowHashSet = new HashSet<>(dttNowList);
        assertThat(dttNowHashSet).hasSize(1).containsExactly(dttNow);

        assertThatIllegalArgumentException().isThrownBy(
            () -> parseAny("la-la-la")
        ).withMessage("could not parse the string 'la-la-la' as DateTimeTriplet with any of supported ways");
    }
}
