package cit.edu.furrevercare.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.cloud.Timestamp;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class TimestampSerializer extends StdSerializer<Timestamp> {

    // ISO 8601 format (e.g., "2023-10-26T10:15:30.000Z")
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    public TimestampSerializer() {
        this(null);
    }

    public TimestampSerializer(Class<Timestamp> t) {
        super(t);
    }

    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            // Convert Google Timestamp to java.time.Instant, then format
            Instant instant = Instant.ofEpochSecond(value.getSeconds(), value.getNanos());
            gen.writeString(ISO_FORMATTER.format(instant));
        }
    }
} 