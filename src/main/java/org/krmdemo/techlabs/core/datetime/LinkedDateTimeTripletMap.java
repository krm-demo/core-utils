package org.krmdemo.techlabs.core.datetime;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.function.Function;

import static org.krmdemo.techlabs.core.utils.CorePropsUtils.equalProps;

/**
 * This class is used as a parallel data-structure over the existing linked-map.
 * To create the instance of that class - use any of factory-methods like {@link #create(SequencedMap, Function)},
 * {@link #createByEpochSeconds(SequencedMap, Function)} or {@link #createByEpochSecondsInt(SequencedMap, Function)}
 * <hr/>
 * TODO: think about making it immutable somehow
 *
 * @param <Key> a type of original map's key
 */
public class LinkedDateTimeTripletMap<Key> extends LinkedHashMap<Key, LinkedDateTimeTripletMap.LinkedTriplet> {

    @Serial
    private static final long serialVersionUID = 123456789_001L;

    /**
     * Extension to {@link DateTimeTriplet} that allows to link that triplet
     * with sequentially previous and next triplets in corresponding collection or map.
     */
    public static class LinkedTriplet extends DateTimeTriplet {
        private LinkedTriplet prev;
        private LinkedTriplet next;

        void linkPrev(LinkedTriplet prev) {
            this.prev = prev;
            prev.next = this;
        }

        void linkNext(LinkedTriplet next) {
            this.next = next;
            next.prev = this;
        }

        public LinkedTriplet(long epochSeconds) {
            super(epochSeconds);
        }

        public LinkedTriplet(long epochSeconds, int nanoOfSecond) {
            super(epochSeconds, nanoOfSecond);
        }

        public LinkedTriplet(Instant instant) {
            super(instant);
        }

        public LinkedTriplet(LocalDateTime localDateTimeUTC) {
            super(localDateTimeUTC);
        }

        public boolean isTheSameAsPrev() {
            return Objects.equals(this, prev);
        }

        public boolean isTheSameAsNext() {
            return Objects.equals(this, next);
        }

        /**
         * Comparing on equality on <b>the first part</b> of triplet with <b>the previous</b> linked value
         *
         * @return {@code true} if <b>the previous</b> item exists and has the same property {@link #getYearAndMonth}
         *          (and {@code false} - otherwise)
         */
        public boolean isYearAnMonthTheSameAsPrev() {
            return equalProps(this, prev, DateTimeTriplet::getYearAndMonth);
        }

        /**
         * Comparing on equality on <b>the first part</b> of triplet with <b>the next</b> linked value
         *
         * @return {@code true} if <b>the next</b> item exists and has the same property {@link #getYearAndMonth}
         *          (and {@code false} - otherwise)
         */
        public boolean isYearAnMonthTheSameAsNext() {
            return equalProps(this, next, DateTimeTriplet::getYearAndMonth);
        }

        /**
         * Comparing on equality on <b>the second part</b> of triplet with <b>the previous</b> linked value
         *
         * @return {@code true} if <b>the previous</b> item exists and has the same property {@link #getDayOfMonthAndWeek}
         *          (and {@code false} - otherwise)
         */
        public boolean isDayOfMonthAndWeekTheSameAsPrev() {
            return equalProps(this, prev, DateTimeTriplet::getDayOfMonthAndWeek);
        }

        /**
         * Comparing on equality on <b>the second part</b> of triplet with <b>the next</b> linked value
         *
         * @return {@code true} if <b>the next</b> item exists and has the same property {@link #getDayOfMonthAndWeek}
         *          (and {@code false} - otherwise)
         */
        public boolean isDayOfMonthAndWeekTheSameAsNext() {
            return equalProps(this, next, DateTimeTriplet::getDayOfMonthAndWeek);
        }

        /**
         * Comparing on equality on <b>the third part</b> of triplet with <b>the previous</b> linked value
         *
         * @return {@code true} if t<b>he previous</b> item exists and has the same property {@link #getHoursMinutes}
         *          (and {@code false} - otherwise)
         */
        public boolean isHoursMinutesTheSameAsPrev() {
            return equalProps(this, prev, DateTimeTriplet::getHoursMinutes);
        }

        /**
         * Comparing on equality on <b>the third part</b> of triplet with <b>the next</b> linked value
         *
         * @return {@code true} if <b>the next</b> item exists and has the same property {@link #getHoursMinutes}
         *          (and {@code false} - otherwise)
         */
        public boolean isHoursMinutesTheSameAsNext() {
            return equalProps(this, next, DateTimeTriplet::getHoursMinutes);
        }

        /**
         * @return the same as inherited {@link #dump()}, but skip displaying the values of parts,
         *          if they repeat the same of the <b>previous</b> item in sequence
         */
        public String dumpLinked() {
            return String.format("%8s%8s%6s",
                this.isYearAnMonthTheSameAsPrev() ? "" : this.getYearAndMonth() + "-",
                this.isDayOfMonthAndWeekTheSameAsPrev() ? "" : this.getDayOfMonthAndWeek(),
                this.isHoursMinutesTheSameAsPrev() ? "--:--" : this.getHoursMinutes());
        }

        /**
         * @return the same as inherited {@link #dump()}, but skip displaying the values of parts,
         *          if they repeat the same of the <b>next</b> item in sequence
         */
        public String dumpLinkedReversed() {
            return String.format("%8s%8s%6s",
                this.isYearAnMonthTheSameAsNext() ? "" : this.getYearAndMonth() + "-",
                this.isDayOfMonthAndWeekTheSameAsNext() ? "" : this.getDayOfMonthAndWeek(),
                this.isHoursMinutesTheSameAsNext() ? "--:--" : this.getHoursMinutes());
        }
    }

    /**
     * Private-constructor to force using factory-methods
     */
    private LinkedDateTimeTripletMap() {
    }

    /**
     * Builds a chronological sequence of {@link LinkedTriplet}s over the original sequence map,
     * where the passed {@code instantFunc} is used to extract that date-time chronological property
     *
     * @param originalMap an original map over any values that has chronological property
     * @param instantFunc a reference to getter that is used to extract that date-time chronological property
     * @return the linked-map with the same keys and sequence, but with {@link LinkedTriplet} as value
     * @param <Key> a type of original map's key
     * @param <Value> a type of original map's value
     */
    public static <Key, Value>
    LinkedDateTimeTripletMap<Key>
    create(SequencedMap<Key,Value> originalMap, Function<Value, Instant> instantFunc) {
        LinkedDateTimeTripletMap<Key> dttMap = new LinkedDateTimeTripletMap<>();
        originalMap.forEach((Key key, Value value) -> {
            Instant instant = instantFunc.apply(value);
            LinkedTriplet dtt = new LinkedTriplet(instant);
            if (!dttMap.isEmpty()) {
                dtt.linkPrev(dttMap.lastEntry().getValue());
            }
            dttMap.putLast(key, dtt);
        });
        return dttMap;
    }

    /**
     * The same as {@link #create(SequencedMap, Function)},
     * but the getter-function {@code epochSecondsFunc} extracts epoch-seconds as {@link Long}
     *
     * @param originalMap an original map over any values that has chronological property
     * @param epochSecondsFunc a reference to getter that is used to extract epoch-seconds as {@link Long}
     * @return the linked-map with the same keys and sequence, but with {@link LinkedTriplet} as value
     * @param <Key> a type of original map's key
     * @param <Value> a type of original map's value
     */
    public static <Key, Value>
    LinkedDateTimeTripletMap<Key>
    createByEpochSeconds(SequencedMap<Key,Value> originalMap, Function<Value, Long> epochSecondsFunc) {
        Function<Value, Instant> instantFunc = (Value value) -> {
            Long epochSeconds = epochSecondsFunc.apply(value);
            return Instant.ofEpochSecond(epochSeconds);
        };
        return create(originalMap, instantFunc);
    }

    /**
     * The same as {@link #createByEpochSeconds(SequencedMap, Function)},
     * but the function that extracts epoch-seconds returns {@link Integer} (not {@link Long})
     *
     * @param originalMap an original map over any values that has chronological property
     * @param epochSecondsIntFunc a reference to getter that is used to extract epoch-seconds as {@link Integer}
     * @return the linked-map with the same keys and sequence, but with {@link LinkedTriplet} as value
     * @param <Key> a type of original map's key
     * @param <Value> a type of original map's value
     */
    public static <Key, Value>
    LinkedDateTimeTripletMap<Key>
    createByEpochSecondsInt(SequencedMap<Key,Value> originalMap, Function<Value, Integer> epochSecondsIntFunc) {
        Function<Value, Long> epochSecondsFunc = (Value value) -> epochSecondsIntFunc.apply(value).longValue();
        return createByEpochSeconds(originalMap, epochSecondsFunc);
    }
}
