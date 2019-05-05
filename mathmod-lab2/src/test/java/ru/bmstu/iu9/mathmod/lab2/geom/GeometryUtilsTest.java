package ru.bmstu.iu9.mathmod.lab2.geom;

import org.testng.annotations.Test;

import static java.lang.Math.pow;
import static org.testng.Assert.*;
import static ru.bmstu.iu9.mathmod.lab2.geom.GeometryUtils.*;

public class GeometryUtilsTest {

    @Test
    public void testIsConvexPolygon() {
        assertTrue(isConvexPolygon());
        assertTrue(isConvexPolygon(vec2d(0, 1)));
        assertTrue(isConvexPolygon(vec2d(2, 14), vec2d(8, 254)));
        assertTrue(isConvexPolygon(vec2d(2, 14), vec2d(8, 254), vec2d(-5, 142)));

        Triangle tr1 = new Triangle(vec2d(2, 24), vec2d(25, 115), vec2d(200, 85));
        Triangle tr2 = new Triangle(vec2d(2, 24), vec2d(200, 85), vec2d(150, 10));
        Triangle tr3 = new Triangle(vec2d(2, 24), vec2d(200, 85), vec2d(380, 100));
        AdjacentTriangles convexTriangles = new AdjacentTriangles(tr1, tr2);
        AdjacentTriangles nonConvexTriangles = new AdjacentTriangles(tr1, tr3);

        assertTrue(isConvexPolygon(convexTriangles.points()));
        assertFalse(isConvexPolygon(nonConvexTriangles.points()));
    }

    @Test
    public void testGetCircumscribedCircle() {
//        (50.00, 80.00), (412.00, 287.00), (12.00, 33.00)
        Triangle tr1 = new Triangle(vec2d(50, 80), vec2d(412, 287), vec2d(12, 33));
        Circle circle = getCircumscribedCircle(tr1);
        Vector2D center = circle.getCenter();
        double r = circle.getRadius();

        for(Vector2D pt : tr1.points()) {
            assertEquals(pow(r, 2.0), pow(pt.x() - center.x(), 2.0) + pow(pt.y() - center.y(), 2.0), 0.0001);
        }
    }

}