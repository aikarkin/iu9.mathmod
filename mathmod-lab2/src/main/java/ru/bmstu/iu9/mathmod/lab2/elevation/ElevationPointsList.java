package ru.bmstu.iu9.mathmod.lab2.elevation;

import ru.bmstu.iu9.mathmod.lab2.geom.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ElevationPointsList extends ArrayList<ElevationPoint> {
    public List<Point2D> getXYProjections() {
        return this.stream()
                .map(ElevationPoint::xyProjection)
                .collect(Collectors.toList());
    }
}
