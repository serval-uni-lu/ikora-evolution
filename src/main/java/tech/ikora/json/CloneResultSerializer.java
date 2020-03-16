package tech.ikora.json;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import tech.ikora.analytics.Clones;

import java.io.IOException;

public class CloneResultSerializer extends StdSerializer<Clones> {
    public CloneResultSerializer() {
        this(null);
    }

    public CloneResultSerializer(Class<Clones> t) {
        super(t);
    }

    @Override
    public void serialize(Clones clones, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeArrayFieldStart("clones");

        jsonGenerator.writeEndObject();
    }
}
