package ru.bmstu.iu9.mathmod.lab2;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ru.bmstu.iu9.mathmod.lab2.elevation.ElevationPoint;
import ru.bmstu.iu9.mathmod.lab2.elevation.ElevationPointSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateRandomElevationMap {

    private static final int width = 500;
    private static final int length = 300;
    private static final int height = 200;

    public static void main(String[] args) throws IOException {
        if(args.length < 2) {
            System.err.println("Invalid number of arguments");
            return;
        }

        File outFile = new File(args[0]);

        int pointsCount = Integer.valueOf(args[1]);
        List<ElevationPoint> ptsArr = new ArrayList<>();

        SimpleModule module = new SimpleModule();
        module.addSerializer(ElevationPoint.class, new ElevationPointSerializer());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

        for (int i = 0; i < pointsCount; i++) {
            ptsArr.add(new ElevationPoint(randInRange(80, width), randInRange(80, length), randInRange(0, height)));
        }

        writer.writeValue(outFile, ptsArr);
    }


    private static int randInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
