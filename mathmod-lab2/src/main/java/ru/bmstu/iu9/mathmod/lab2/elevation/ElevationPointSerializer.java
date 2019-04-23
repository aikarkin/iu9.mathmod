package ru.bmstu.iu9.mathmod.lab2.elevation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ElevationPointSerializer extends JsonSerializer<ElevationPoint> {

    @Override
    public void serialize(ElevationPoint elevationPoint, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("x", elevationPoint.x());
        jsonGenerator.writeNumberField("y", elevationPoint.y());
        jsonGenerator.writeNumberField("h", elevationPoint.h());
        jsonGenerator.writeEndObject();
    }

}
