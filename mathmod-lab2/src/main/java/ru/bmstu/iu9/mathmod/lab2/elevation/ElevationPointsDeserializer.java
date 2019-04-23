package ru.bmstu.iu9.mathmod.lab2.elevation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;

public class ElevationPointsDeserializer extends JsonDeserializer<ElevationPointsList> {

    @Override
    public ElevationPointsList deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectCodec codec = jsonParser.getCodec();
        ArrayNode pointsArr = codec.readTree(jsonParser);
        ElevationPointsList elevationPoints = new ElevationPointsList();
        double x, y, h;

        for (int i = 0; i < pointsArr.size(); i++) {
            JsonNode jsonPoint = pointsArr.get(i);
            x = jsonPoint.get("x").asDouble();
            y = jsonPoint.get("y").asDouble();
            h = jsonPoint.get("h").asDouble();
            elevationPoints.add(new ElevationPoint(x, y, h));
        }

        return elevationPoints;
    }

}
