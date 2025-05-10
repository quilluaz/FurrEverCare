package cit.edu.furrevercare.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.cloud.Timestamp;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class TimestampDeserializer extends StdDeserializer<Timestamp> {

    public TimestampDeserializer() {
        this(null);
    }

    public TimestampDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Timestamp deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String dateStr = jp.getText();
        try {
            // Attempt to parse as ISO 8601 string (e.g., "2023-10-26T10:15:30.000Z")
            Instant instant = OffsetDateTime.parse(dateStr).toInstant();
            return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSecond(), instant.getNano());
        } catch (DateTimeParseException e) {
            // If ISO parsing fails, try to parse as epoch milliseconds if it's a number
            if (dateStr.matches("^\\d+$")) {
                try {
                    long epochMillis = Long.parseLong(dateStr);
                    // Convert milliseconds to seconds and nanoseconds
                    return Timestamp.ofTimeSecondsAndNanos(epochMillis / 1000, (int) ((epochMillis % 1000) * 1000000));
                } catch (NumberFormatException nfe) {
                    throw new IOException("Failed to parse timestamp from number: " + dateStr, nfe);
                }
            }
            // Add more parsing strategies if needed or re-throw with a more specific message
            throw new IOException("Failed to parse timestamp: '" + dateStr + "'. Expected ISO 8601 format (e.g., '2023-10-26T10:15:30.000Z') or epoch milliseconds.", e);
        }
    }
} 