package xyz.ruhshan.pl.common.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CustomInstantSerializer extends StdSerializer<Instant> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

    public CustomInstantSerializer() {
        this(null);
    }

    protected CustomInstantSerializer(Class<Instant> t) {
        super(t);
    }

    @Override
    public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String str = formatter.format(instant);
        jsonGenerator.writeString(str);
    }
}