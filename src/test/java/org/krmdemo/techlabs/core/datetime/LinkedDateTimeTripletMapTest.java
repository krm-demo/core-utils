package org.krmdemo.techlabs.core.datetime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krmdemo.techlabs.core.datetime.LinkedDateTimeTripletMap.LinkedTriplet;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.SequencedMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.krmdemo.techlabs.core.utils.CoreCollectors.toLinkedMap;
import static org.krmdemo.techlabs.core.utils.CoreStringUtils.multiLine;

/**
 * A unit-test for {@link LinkedDateTimeTripletMap}
 */
public class LinkedDateTimeTripletMapTest {

    private static int eventCount = 0;

    @BeforeEach
    void beforeEach() {
        eventCount = 0;
    }

    private record Event(
        int epochSeconds,
        int eventNum,
        String subject
    ) {
        public Event(int year, int month, int day, int hours, int minutes, String subject) {
            this(epochSecondsUTC(year, month, day, hours, minutes), ++eventCount, subject);
        }
        public DateTimeTriplet timeTriplet() {
            return new DateTimeTriplet(epochSeconds);
        }
        public String scheduleLine() {
            return String.format("|%3d) %s | %s", eventNum, timeTriplet().dump(), subject);
        }
        private static int epochSecondsUTC(int year, int month, int day, int hours, int minutes) {
            LocalDateTime ldt = LocalDateTime.of(year, month, day, hours, minutes);
            long epochSeconds = ldt.toInstant(ZoneOffset.UTC).getEpochSecond();
            return Math.toIntExact(epochSeconds);
        }
    }

    // --------------------------------------------------------------------------------------------

    @Test
    void testEventsForTwoDays() {
        SequencedMap<Integer, Event> eventsMapForTwoDays = Stream.of(
            new Event(2025, 10, 15, 7, 40, "alarm-clock is ringing"),
            new Event(2025, 10, 15, 7, 40, "wake up today!"),
            new Event(2025, 10, 15, 8, 0, "breakfast"),
            new Event(2025, 10, 15, 12, 25, "lunch"),
            new Event(2025, 10, 15, 18, 45, "happy hours"),
            new Event(2025, 10, 16, 0, 0, "midnight"),
            new Event(2025, 10, 16, 8, 53, "wake up tomorrow !!!")
        ).collect(toLinkedMap(Event::eventNum, Function.identity()));

//        System.out.println("eventsMapForTwoDays --> " + dumpAsJsonTxt(eventsMapForTwoDays));
        System.out.println("---- original schedule of events for two days: ----");
        System.out.println(buildSchedule(eventsMapForTwoDays));
        System.out.println("---- schedule of events for two days with linked date-time-triplets: ----");
        System.out.println(buildScheduleLinked(eventsMapForTwoDays));
        System.out.println("---- reversed schedule of events for two days with reversed linked date-time-triplets: ----");
        System.out.println(buildScheduleLinkedReversed(eventsMapForTwoDays));
        System.out.println();

        assertThat(buildSchedule(eventsMapForTwoDays)).isEqualTo("""
            |  1) 2025-10-15 (Wed) 07:40 | alarm-clock is ringing
            |  2) 2025-10-15 (Wed) 07:40 | wake up today!
            |  3) 2025-10-15 (Wed) 08:00 | breakfast
            |  4) 2025-10-15 (Wed) 12:25 | lunch
            |  5) 2025-10-15 (Wed) 18:45 | happy hours
            |  6) 2025-10-16 (Thu) 00:00 | midnight
            |  7) 2025-10-16 (Thu) 08:53 | wake up tomorrow !!!""");
        assertThat(buildScheduleLinked(eventsMapForTwoDays)).isEqualTo("""
            |  1) 2025-10-15 (Wed) 07:40 | alarm-clock is ringing
            |  2)                  --:-- | wake up today!
            |  3)                  08:00 | breakfast
            |  4)                  12:25 | lunch
            |  5)                  18:45 | happy hours
            |  6)         16 (Thu) 00:00 | midnight
            |  7)                  08:53 | wake up tomorrow !!!""");
        assertThat(buildScheduleLinkedReversed(eventsMapForTwoDays)).isEqualTo("""
            |  7) 2025-10-16 (Thu) 08:53 | wake up tomorrow !!!
            |  6)                  00:00 | midnight
            |  5)         15 (Wed) 18:45 | happy hours
            |  4)                  12:25 | lunch
            |  3)                  08:00 | breakfast
            |  2)                  07:40 | wake up today!
            |  1)                  --:-- | alarm-clock is ringing""");
    }

    // --------------------------------------------------------------------------------------------

    @Test
    void testEventsForVacation() {
        SequencedMap<Integer, Event> eventsMapForVacation = Stream.of(
            new Event(2024, 10, 15, 17, 40, "book a hotel"),
            new Event(2024, 10, 15, 19, 30, "book a flight"),
            new Event(2024, 12, 20, 18, 0, "vacation begin"),
            new Event(2024, 12, 21, 10, 25, "take off"),
            new Event(2024, 12, 26, 0, 0, "celebrating X-Mass"),
            new Event(2024, 12, 31, 21, 40, "start celebrating New Year"),
            new Event(2025, 1, 1, 0, 0, "Happy New Year !!!"),
            new Event(2025, 1, 1, 6, 25, "finish celebrating New Year"),
            new Event(2025, 1, 7, 11, 45, "take off home"),
            new Event(2025, 1, 8, 9, 0, "vacation end")
        ).collect(toLinkedMap(Event::eventNum, Function.identity()));

//        System.out.println("eventsMapForVacation --> " + dumpAsJsonTxt(eventsMapForVacation));
        System.out.println("---- original schedule of events for vacation: ----");
        System.out.println(buildSchedule(eventsMapForVacation));
        System.out.println("---- schedule of events for vacation with linked date-time-triplets: ----");
        System.out.println(buildScheduleLinked(eventsMapForVacation));
        System.out.println("---- reversed schedule of events for vacation with reversed linked date-time-triplets: ----");
        System.out.println(buildScheduleLinkedReversed(eventsMapForVacation));
        System.out.println();

        assertThat(buildSchedule(eventsMapForVacation)).isEqualTo("""
            |  1) 2024-10-15 (Tue) 17:40 | book a hotel
            |  2) 2024-10-15 (Tue) 19:30 | book a flight
            |  3) 2024-12-20 (Fri) 18:00 | vacation begin
            |  4) 2024-12-21 (Sat) 10:25 | take off
            |  5) 2024-12-26 (Thu) 00:00 | celebrating X-Mass
            |  6) 2024-12-31 (Tue) 21:40 | start celebrating New Year
            |  7) 2025-01-01 (Wed) 00:00 | Happy New Year !!!
            |  8) 2025-01-01 (Wed) 06:25 | finish celebrating New Year
            |  9) 2025-01-07 (Tue) 11:45 | take off home
            | 10) 2025-01-08 (Wed) 09:00 | vacation end""");
        assertThat(buildScheduleLinked(eventsMapForVacation)).isEqualTo("""
            |  1) 2024-10-15 (Tue) 17:40 | book a hotel
            |  2)                  19:30 | book a flight
            |  3) 2024-12-20 (Fri) 18:00 | vacation begin
            |  4)         21 (Sat) 10:25 | take off
            |  5)         26 (Thu) 00:00 | celebrating X-Mass
            |  6)         31 (Tue) 21:40 | start celebrating New Year
            |  7) 2025-01-01 (Wed) 00:00 | Happy New Year !!!
            |  8)                  06:25 | finish celebrating New Year
            |  9)         07 (Tue) 11:45 | take off home
            | 10)         08 (Wed) 09:00 | vacation end""");
        assertThat(buildScheduleLinkedReversed(eventsMapForVacation)).isEqualTo("""
            | 10) 2025-01-08 (Wed) 09:00 | vacation end
            |  9)         07 (Tue) 11:45 | take off home
            |  8)         01 (Wed) 06:25 | finish celebrating New Year
            |  7)                  00:00 | Happy New Year !!!
            |  6) 2024-12-31 (Tue) 21:40 | start celebrating New Year
            |  5)         26 (Thu) 00:00 | celebrating X-Mass
            |  4)         21 (Sat) 10:25 | take off
            |  3)         20 (Fri) 18:00 | vacation begin
            |  2) 2024-10-15 (Tue) 19:30 | book a flight
            |  1)                  17:40 | book a hotel""");
    }

    // --------------------------------------------------------------------------------------------

    private String buildSchedule(SequencedMap<Integer, Event> eventsMap) {
        return eventsMap.values().stream()
            .map(Event::scheduleLine)
            .collect(multiLine());
    }

    private String buildScheduleLinked(SequencedMap<Integer, Event> eventsMap) {
        SequencedMap<Integer, LinkedTriplet> linkedTripletsMap =
            LinkedDateTimeTripletMap.createByEpochSecondsInt(eventsMap, Event::epochSeconds);
        assertThat(linkedTripletsMap).hasSize(eventsMap.size());
        assertThat(linkedTripletsMap.keySet()).isEqualTo(eventsMap.keySet());
        return eventsMap.keySet().stream().map(eventNum -> {
            Event event = eventsMap.get(eventNum);
            LinkedTriplet linkedTriplet = linkedTripletsMap.get(eventNum);
            return String.format("|%3d) %s | %s", eventNum, linkedTriplet.dumpLinked(), event.subject);
        }).collect(multiLine());
    }

    private String buildScheduleLinkedReversed(SequencedMap<Integer, Event> eventsMap) {
        SequencedMap<Integer, LinkedTriplet> linkedTripletsMap =
            LinkedDateTimeTripletMap.createByEpochSecondsInt(eventsMap, Event::epochSeconds);
        assertThat(linkedTripletsMap).hasSize(eventsMap.size());
        assertThat(linkedTripletsMap.keySet()).isEqualTo(eventsMap.keySet());
        return eventsMap.reversed().keySet().stream().map(eventNum -> {
            Event event = eventsMap.get(eventNum);
            LinkedTriplet linkedTriplet = linkedTripletsMap.get(eventNum);
            return String.format("|%3d) %s | %s", eventNum, linkedTriplet.dumpLinkedReversed(), event.subject);
        }).collect(multiLine());
    }
}
