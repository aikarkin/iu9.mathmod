package ru.bmstu.iu9.mathmod.lab2.delaunay;

import ru.bmstu.iu9.mathmod.lab2.geom.AdjacentTriangles;
import ru.bmstu.iu9.mathmod.lab2.geom.Vector2D;

import static ru.bmstu.iu9.mathmod.lab2.geom.GeometryUtils.pseudoScalar;
import static ru.bmstu.iu9.mathmod.lab2.geom.GeometryUtils.scalar;

public class DelaunayUtil {
    private DelaunayUtil() {
    }

    public static boolean satisfiesDelaunayCondition(AdjacentTriangles adjacentTriangles) {
        Vector2D[] points = adjacentTriangles.pointsSorted();

        Vector2D a = new Vector2D(points[2].subtract(points[1]));
        Vector2D b = new Vector2D(points[0].subtract(points[1]));
        Vector2D c = new Vector2D(points[0].subtract(points[3]));
        Vector2D d = new Vector2D(points[2].subtract(points[3]));

        return pseudoScalar(c, d) * scalar(a, b) + scalar(c, d) * pseudoScalar(a, b) >= 0;
    }

}
