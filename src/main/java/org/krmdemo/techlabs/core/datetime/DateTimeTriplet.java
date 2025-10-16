package org.krmdemo.techlabs.core.datetime;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * This class is used to represent the date-time types as a triplet of parts,
 * which could be displayed in three separate columns with natural chronological order.
 * This is just a simple wrapper over standard {@link LocalDateTime} from JDK.
 * <hr/>
 * For a date-time that could be received from terminal in following way <pre>{@code
 *     ...> date -u
 *     Wed Oct 15 06:03:38 UTC 2025}</pre>
 * the properties of this class will be:
 * <table>
 *     <tr><th>method</th><th>value</th></tr>
 *     <tr><td>{@link #getYearAndMonth()}</td><td>{@code "2025-10"}</td></tr>
 *     <tr><td>{@link #getDayOfMonthAndWeek()}</td><td>{@code "-15 (Wed) "}</td></tr>
 *     <tr><td>{@link #getHoursMinutes()}</td><td>{@code "06:03"}</td></tr>
 *     <tr><td>{@link #dumpNoWeek()}</td><td>{@code "2025-10-15 06:03"}</td></tr>
 *     <tr><td>{@link #dump()}</td><td>{@code "2025-10-15 (Wed) 06:03"}</td></tr>
 * </table>
 * <hr/>
 * As far as <b>{@code git}</b>-repository provides the commit-time in UTC time-zone - this class also represents
 * all it's parts for UTC time-zone and day of week corresponds to {@link Locale#US}
 * ({@code Mon}, {@code Tue}, {@code Wed}, {@code Thu}, {@code Fri}, {@code Sat}, {@code Sun}) by default .
 */
public class DateTimeTriplet {

    private final LocalDateTime localDateTimeUTC;

    /**
     * @param epochSeconds the number of seconds from the epoch of {@code 1970-01-01T00:00:00Z}
     */
    public DateTimeTriplet(long epochSeconds) {
        this(epochSeconds, 0);
    }

    /**
     * @param epochSeconds the number of seconds from the epoch of {@code 1970-01-01T00:00:00Z}
     * @param nanoOfSecond the nanosecond within the second, from 0 to 999,999,999
     */
    public DateTimeTriplet(long epochSeconds, int nanoOfSecond) {
        this(LocalDateTime.ofEpochSecond(epochSeconds, nanoOfSecond, ZoneOffset.UTC));
    }

    /**
     * @param instant a standard representation of date-time instant in Java as {@link Instant}
     */
    public DateTimeTriplet(Instant instant) {
        this(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
    }

    /**
     * @param localDateTimeUTC a date-time in {@link ZoneOffset#UTC} time-zone as {@link LocalDateTime}
     */
    public DateTimeTriplet(LocalDateTime localDateTimeUTC) {
        this.localDateTimeUTC = Objects.requireNonNull(localDateTimeUTC);
    }

    /**
     * This provider could be helpful in unit-tests that allows to avoid an unnecessary mocking
     */
    public static Supplier<Instant> NOW_PROVIDER = Instant::now;

    /**
     * The default format of the day of the week is English USA three letters
     * (like {@code Mon}, {@code Tue}, {@code Wed}, {@code Thu}, {@code Fri}, {@code Sat}, {@code Sun})
     */
    public static Supplier<TextStyle> DAY_OF_WEEK__TEXT_STYLE = () -> TextStyle.SHORT;

    /**
     * The default names of days of the week correspond to English USA locale ({@link Locale#US})
     */
    public static Supplier<Locale> DAY_OF_WEEK__LOCALE = () -> Locale.US;

    /**
     * @return an instance of {@link DateTimeTriplet} that corresponds to current date-time
     */
    public static DateTimeTriplet now() {
        return new DateTimeTriplet(NOW_PROVIDER.get());
    }

    /**
     * @return underlying value of standard {@link LocalDateTime} from JDK
     */
    public LocalDateTime getLocalDateTime() {
        return localDateTimeUTC;
    }

    /**
     * @return integer value of year
     */
    public int getYear() {
        return localDateTimeUTC.getYear();
    }

    /**
     * @return a value of month as enumeration {@link Month}
     */
    public Month getMonth() {
        return localDateTimeUTC.getMonth();
    }

    /**
     * @return <b>the first part</b> of date-time triplet that contains dash-separated
     *          {@link #getYear() year} and {@link #getMonth() month}
     */
    public String getYearAndMonth() {
        return String.format("%04d-%02d", this.getYear(), this.getMonth().getValue());
    }

    /**
     * @return an ordinal number of day within the month (from {@code 1} to {@code 31})
     */
    public int getDayOfMoth() {
        return localDateTimeUTC.getDayOfMonth();
    }

    /**
     * @return a day of week as enumartion {@link DayOfWeek}
     */
    public DayOfWeek getDayOfWeek() {
        return localDateTimeUTC.getDayOfWeek();
    }

    /**
     * @return <b>the second part</b> of date-time triplet that contains {@link #getDayOfMoth()} the day of month}
     *          and {@link #getDayOfWeek() the day of week as 3-letters acronym}
     *          in a format described in the declaration of {@link DateTimeTriplet}
     */
    public String getDayOfMonthAndWeek() {
        String dayOfWeekStr = this.getDayOfWeek().getDisplayName(
            DAY_OF_WEEK__TEXT_STYLE.get(),
            DAY_OF_WEEK__LOCALE.get()
        );
        return String.format("-%02d (%s) ", this.getDayOfMoth(), dayOfWeekStr);
    }

    /**
     * @return a value of <i>hours</i> in the half-open range {@code [0..24)}
     */
    public int getHours() {
        return localDateTimeUTC.getHour();
    }

    /**
     * @return a value of <i>minutes</i> in the half-open range {@code [0..60)}
     */
    public int getMinutes() {
        return localDateTimeUTC.getMinute();
    }

    /**
     * @return <b>the third part</b> of date-time triplet that contains colon(':')-separated
     *          {@link #getHours() hours} and {@link #getMinutes() minutes}
     */
    public String getHoursMinutes() {
        return String.format("%02d:%02d", this.getHours(), this.getMinutes());
    }

    /**
     * @return concatenation of 3 parts: {@link #getYearAndMonth()} + {@link #getDayOfMonthAndWeek()} + {@link #getHoursMinutes()}
     */
    public String dump() {
        return getYearAndMonth() + getDayOfMonthAndWeek() + getHoursMinutes();
    }

    /**
     * @return the same as {@link #dump()}, but without day of week
     */
    public String dumpNoWeek() {
        return String.format("%s-%02d %s", getYearAndMonth(), getDayOfMoth(), getHoursMinutes());
    }

    @Override
    public boolean equals(Object thatObj) {
        if (thatObj instanceof DateTimeTriplet that) {
            return this.localDateTimeUTC.equals(that.localDateTimeUTC);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.localDateTimeUTC.hashCode();
    }

    @Override
    public String toString() {
        return this.dump();
    }
}
