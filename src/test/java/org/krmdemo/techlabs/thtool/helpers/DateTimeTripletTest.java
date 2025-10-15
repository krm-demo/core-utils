package org.krmdemo.techlabs.thtool.helpers;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.dump.DumpUtils.dumpAsJsonTxt;

/**
 * A unit-test for {@link DateTimeTriplet}
 */
public class DateTimeTripletTest {

    @Test
    void testAgainst_DateTimeFormatter() {
        DateTimeTriplet dttNow = DateTimeTriplet.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd (EEE) HH:mm");
        assertThat(dttNow.dump()).isEqualTo(dtf.format(dttNow.getLocalDateTime()));
        DateTimeFormatter dtfNoWeek = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        assertThat(dttNow.dumpNoWeek()).isEqualTo(dtfNoWeek.format(dttNow.getLocalDateTime()));
    }

    @Test
    void testAgainst_String_format() {
        DateTimeTriplet dttNow = DateTimeTriplet.now();
        LocalDateTime ldt = dttNow.getLocalDateTime();
//        System.out.printf("date: %tY--%tm-%td;%n", ldt, ldt, ldt);
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
    void testDumpJson() {
        DateTimeTriplet dttNow = DateTimeTriplet.now();
//        System.out.println("ddtNow --(as JSON)--> " + dumpAsJsonTxt(dttNow));
        assertThat(dumpAsJsonTxt(dttNow))
            .isNotBlank()
            .contains("yearAndMonth")
            .contains("dayOfMonthAndWeek")
            .contains("hoursMinutes");
    }
}
