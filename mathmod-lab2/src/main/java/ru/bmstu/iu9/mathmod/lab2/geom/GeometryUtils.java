package ru.bmstu.iu9.mathmod.lab2.geom;

import java.util.List;
import java.util.Optional;

public class GeometryUtils {
    public static<K extends BoundedGeometry> Rectangle getMbrOf(List<K> objects) {
        if (objects.isEmpty())
            return null;

        Rectangle firstMbr = objects.get(0).getMbr();
        Optional<Double> minXOpt, minYOpt, maxXOpt, maxYOpt;
        double minX, minY, maxX, maxY;

        minXOpt = objects.stream().map(entry -> entry.getMbr().getLeftTopPoint().x()).min(Double::compareTo);
        minYOpt = objects.stream().map(entry -> entry.getMbr().getLeftTopPoint().y()).min(Double::compareTo);
        maxXOpt = objects.stream().map(entry -> entry.getMbr().getRightBottomPoint().x()).min(Double::compareTo);
        maxYOpt = objects.stream().map(entry -> entry.getMbr().getRightBottomPoint().y()).min(Double::compareTo);

        minX = minXOpt.orElseGet(() -> firstMbr.getLeftTopPoint().x());
        minY = minYOpt.orElseGet(() -> firstMbr.getLeftTopPoint().y());
        maxX = maxXOpt.orElseGet(() -> firstMbr.getRightBottomPoint().y());
        maxY = maxYOpt.orElseGet(() -> firstMbr.getRightBottomPoint().y());

        return new Rectangle(new Point2D(minX, minY), maxX - minX, maxY - minY);
    }

    public static Point2D point(double x, double y) {
        return new Point2D(x, y);
    }
}
