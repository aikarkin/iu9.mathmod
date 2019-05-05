package ru.bmstu.iu9.mathmod.lab2.geom;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class GeometryUtils {

    public static <K extends BoundedGeometry> Rectangle getMbrOf(List<K> objects) {
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

        return new Rectangle(new Vector2D(minX, minY), maxX - minX, maxY - minY);
    }

    public static Vector2D vec2d(double x, double y) {
        return new Vector2D(x, y);
    }

    public static boolean isConvexPolygon(Vector2D... points) {
        if (points.length < 4)
            return true;

        int n = points.length;
        Vector2D[] edges = new Vector2D[n];
        edges[0] = new Vector2D(points[0].subtract(points[n - 1]));

        for (int i = 1; i < points.length; i++) {
            edges[i] = new Vector2D(points[i].subtract(points[i - 1]));
        }

        double prevSign, curSign = signum(pseudoScalar(edges[0], edges[1]));

        for (int i = 2; i < n; i++) {
            prevSign = curSign;
            curSign = signum(pseudoScalar(edges[i - 1], edges[i]));
            if (prevSign != curSign)
                return false;
        }

        return true;
    }

    public static Circle getCircumscribedCircle(Triangle tr) {
        Vector2D[] edges = Arrays.stream(tr.edges())
                .map(Edge::toVector)
                .toArray(Vector2D[]::new);

        double radius = edges[0].getNorm() * edges[1].getNorm() * edges[2].getNorm()
                / (2.0 * abs(GeometryUtils.pseudoScalar(edges[0], edges[1])));

        Vector2D dir, n, mid;
        double dist;

        if(pseudoScalar(edges[0], edges[1]) > 0 || scalar(edges[2], edges[0]) > 0) {
            dir = new Vector2D(edges[1].mapMultiply(-1));
        } else {
            dir = edges[1];
        }

        mid = new Vector2D(
                (tr.edges()[1].first().x() + tr.edges()[1].second().x()) / 2.0,
                (tr.edges()[1].first().y() + tr.edges()[1].second().y()) / 2.0
        );
        dist = sqrt(pow(radius, 2.0) - pow(mid.subtract(tr.p2()).getNorm(), 2.0));
        n = new Vector2D(new Vector2D(dir.y(), -dir.x()).unitVector());
        Vector2D center = new Vector2D(mid.add(n.mapMultiply(dist)));

        return new Circle(center, radius);
    }

    public static double scalar(Vector2D v1, Vector2D v2) {
        return v1.x() * v2.x() + v1.y() * v2.y();
    }

    public static double pseudoScalar(Vector2D v1, Vector2D v2) {
        return v1.x() * v2.y() - v1.y() * v2.x();
    }

}
