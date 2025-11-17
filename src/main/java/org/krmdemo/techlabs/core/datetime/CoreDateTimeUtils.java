package org.krmdemo.techlabs.core.datetime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
     * Transform the string-representation of {@link Instant}
     * in {@link DateTimeFormatter#ISO_INSTANT ISO_INSTANT}-format
     * into {@link DateTimeTriplet}
     *
     * @param instantStr string-representation of {@link Instant} in format like {@code 2007-12-03T10:15:30.00Z}.
     * @return the instance of {@link DateTimeTriplet}
     * @throws IllegalArgumentException in case of {@code null}, blank or any invalid string
     *
     * @see Instant#parse(CharSequence)
     */
    public static DateTimeTriplet parseIsoInstant(String instantStr) {
        return new DateTimeTriplet(Instant.parse(instantStr));
    }

    /**
     * Transform the string-representation of epoch-seconds into {@link DateTimeTriplet}
     *
     * @param epochSecondsStr string-representation of epoch-seconds (that is a standard {@code long})
     * @return the instance of {@link DateTimeTriplet}
     * @throws IllegalArgumentException in case of {@code null}, blank or any invalid string
     */
    public static DateTimeTriplet parseEpochSeconds(String epochSecondsStr) {
        long epochSeconds = Long.parseLong(epochSecondsStr);
        return new DateTimeTriplet(epochSeconds);
    }

    /**
     * Parse the result of {@link DateTimeTriplet#dump()} or {@link DateTimeTriplet#dumpNoWeek()}
     * back to original instance of {@link DateTimeTriplet}.
     *
     * @param dttStr the string with data-time in format {@value #dttNaturalFmtPattern}
     * @return the instance of {@link DateTimeTriplet}
     * @throws IllegalArgumentException in case of {@code null}, blank or any invalid string
     */
    public static DateTimeTriplet parseNatural(String dttStr) {
        LocalDateTime localDateTimeUTC = LocalDateTime.parse(dttStr, dttNaturalFormatter);
        return new DateTimeTriplet(localDateTimeUTC);
    }

    private static final String dttNaturalFmtPattern = "yyyy-MM-dd ['('EEE') ']HH:mm";
    private static final DateTimeFormatter dttNaturalFormatter = DateTimeFormatter.ofPattern(dttNaturalFmtPattern);

    /**
     * Performs the subsequent attempts to parse the passed string {@code dttStr} with:<ul>
     *     <li>{@link #parseIsoInstant(String)}</li>
     *     <li>{@link #parseEpochSeconds(String)}</li>
     *     <li>{@link #parseNatural(String)}</li>
     * </ul>
     * ... and returns either an instance of {@link DateTimeTriplet} or {@code null} if the input string is blank.
     *
     * @param dttStr the string in any supported formats (ISO_INSTANT, epoch-seconds, natural)
     * @return the instance of {@link DateTimeTriplet}
     * @throws IllegalArgumentException in case of the passed string is not blank and could not be parsed
     */
    public static DateTimeTriplet parseAny(String dttStr) {
        if (StringUtils.isBlank(dttStr)) {
            return null;
        }
        return parseFuncChain.stream()
            .map(optFunc -> optFunc.apply(dttStr))
            .flatMap(Optional::stream)
            .findFirst().orElseThrow(() ->
                new IllegalArgumentException(String.format(
                    "could not parse the string '%s' as DateTimeTriplet with any of supported ways", dttStr)
            ));
    }

    private static final List<Function<String, Optional<DateTimeTriplet>>> parseFuncChain = List.of(
        parseOptional(CoreDateTimeUtils::parseIsoInstant),
        parseOptional(CoreDateTimeUtils::parseEpochSeconds),
        parseOptional(CoreDateTimeUtils::parseNatural)
    );

    private static
    Function<String, Optional<DateTimeTriplet>>
    parseOptional(Function<String, DateTimeTriplet> parseFunc) {
        return str -> {
            try {
                return Optional.of(parseFunc.apply(str));
            } catch (Exception _ignored) {
                return Optional.empty();
            }
        };
    }

    /**
     * Jackson-Serializer for {@link DateTimeTriplet}
     */
    public static final JsonDeserializer<DateTimeTriplet> DTT_DESERIALIZER = new StdDeserializer<>(DateTimeTriplet.class) {
        @Override
        public DateTimeTriplet deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            return parseAny(parser.getText());
        }
    };

    /**
     * Jackson-Deserializer for {@link DateTimeTriplet}
     */
    public static final JsonSerializer<DateTimeTriplet> DTT_SERIALIZER = new StdSerializer<>(DateTimeTriplet.class) {
        @Override
        public void serialize(DateTimeTriplet dtt, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(dtt.dump());
        }
    };

    /**
     * Custom Jackson-Module that provides serialization and deserialization for {@link DateTimeTriplet},
     * which is useful to apply into {@link com.fasterxml.jackson.databind.ObjectMapper}
     *
     * @return an instance of {@link SimpleModule} that provides serialization and deserialization for {@link DateTimeTriplet}
     */
    public static SimpleModule jacksonModuleDTT() {
        SimpleModule jacksonModule = new SimpleModule();
        jacksonModule.addDeserializer(DateTimeTriplet.class, DTT_DESERIALIZER);
        jacksonModule.addSerializer(DTT_SERIALIZER);
        return jacksonModule;
    }
}
