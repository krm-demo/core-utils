package org.krmdemo.techlabs.ghapi;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.krmdemo.techlabs.core.datetime.CoreDateTimeUtils;
import org.krmdemo.techlabs.core.dump.DumpUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class to provide {@link #feignEncoder()} and {@link Decoder #feignEncoder()}
 */
public class FeignCoders {

    private static final JacksonEncoder jacksonEncoder =
        new JacksonEncoder(List.of(CoreDateTimeUtils.jacksonModuleDTT()));
    private static final JacksonDecoder jacksonDecoder =
        new JacksonDecoder(List.of(CoreDateTimeUtils.jacksonModuleDTT()));

    /**
     * Getting the feign-encoder for GitHub-API
     *
     * @return feign-encoder as {@link Encoder}, which is just a {@link JacksonEncoder} here
     */
    public static Encoder feignEncoder() {
        return jacksonEncoder;
    }

    /**
     * Getting the feign-decoder for GitHub-API
     *
     * @return feign-encoder as {@link Encoder}, which is custom wrapper over {@link JacksonDecoder} here
     */
    public static Decoder feignDecoder() {
        return PagingResult.feignDecoder(jacksonDecoder);
    }
}
