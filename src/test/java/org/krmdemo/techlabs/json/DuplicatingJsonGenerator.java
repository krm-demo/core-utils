package org.krmdemo.techlabs.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.JsonGeneratorDelegate;
import org.krmdemo.techlabs.core.dump.StringBuilderOut;

import java.io.IOException;
import java.io.OutputStream;

/**
 * NOT WORKING !!!
 */
public class DuplicatingJsonGenerator extends JsonGeneratorDelegate {

    private final StringBuilderOut primaryStream;
    private final StringBuilderOut secondaryStream;

    private DuplicatingJsonGenerator(StringBuilderOut primaryStream, StringBuilderOut secondaryStream) {
        super(underlyingGenerator(primaryStream), true);
        this.primaryStream = primaryStream;
        this.secondaryStream = secondaryStream;
    }

    public static DuplicatingJsonGenerator create() {
        StringBuilderOut primaryStream = StringBuilderOut.create();
        StringBuilderOut secondaryStream = StringBuilderOut.create();
        return new DuplicatingJsonGenerator(primaryStream, secondaryStream);
    }

    private static final JsonFactory defaultJsonFactory = new JsonFactory();
    private static JsonGenerator underlyingGenerator(OutputStream outputStream) {
        try {
            return defaultJsonFactory.createGenerator(outputStream);
        } catch (IOException ioEx) {
            throw new IllegalArgumentException("could not create underlying JsonGenerator", ioEx);
        }
    }

    public StringBuilderOut primary() {
        return primaryStream;
    }

    public StringBuilderOut secondary() {
        return secondaryStream;
    }

    @Override
    public void writeStartArray() throws IOException {

        super.writeStartArray();
    }

    @Override
    public void writeStartArray(Object forValue) throws IOException {
        super.writeStartArray(forValue);
    }

    @Override
    public void writeStartArray(Object forValue, int size) throws IOException {
        super.writeStartArray(forValue, size);
    }

    @Override
    public void writeEndArray() throws IOException {
        super.writeEndArray();
    }

    @Override
    public void writeStartObject() throws IOException {
        super.writeStartObject();
    }

    @Override
    public void writeEndObject() throws IOException {
        super.writeEndObject();
    }
}
