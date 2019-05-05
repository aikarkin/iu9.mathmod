package ru.bmstu.iu9.mathmod.lab2.delaunay;

import org.testng.annotations.Test;
import ru.bmstu.iu9.mathmod.lab2.geom.AdjacentTriangles;
import ru.bmstu.iu9.mathmod.lab2.geom.Triangle;
import ru.bmstu.iu9.mathmod.lab2.geom.Vector2D;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static ru.bmstu.iu9.mathmod.lab2.geom.GeometryUtils.vec2d;

public class DelaunayUtilTest {

    @Test
    public void testCheckDelaunayCondition() {
        Triangle tr1 = new Triangle(vec2d(2, 432), vec2d(100, 225), vec2d(22, 300));
        Triangle tr2 = new Triangle(vec2d(245, 335), vec2d(2, 432), vec2d(100, 225));
        AdjacentTriangles adjacentTriangles = new AdjacentTriangles(tr1, tr2);
        assertFalse(DelaunayUtil.satisfiesDelaunayCondition(adjacentTriangles));

        tr1 = new Triangle(vec2d(22, 300), vec2d(2, 432), vec2d(245, 335));
        tr2 = new Triangle(vec2d(22, 300), vec2d(100, 225), vec2d(245, 335));
        adjacentTriangles = new AdjacentTriangles(tr1, tr2);
        assertTrue(DelaunayUtil.satisfiesDelaunayCondition(adjacentTriangles));


        tr1 = new Triangle(vec2d(386, 412), vec2d(522, 432), vec2d(412, 287));
        tr2 = new Triangle(vec2d(412, 287), vec2d(522, 432), vec2d(522, 33));
        adjacentTriangles = new AdjacentTriangles(tr1, tr2);
        assertTrue(DelaunayUtil.satisfiesDelaunayCondition(adjacentTriangles));

        tr1 = new Triangle(vec2d(386, 412), vec2d(412, 287), vec2d(522, 33));
        tr2 = new Triangle(vec2d(386, 412), vec2d(522, 432), vec2d(522, 33));
        adjacentTriangles = new AdjacentTriangles(tr1, tr2);
        assertFalse(DelaunayUtil.satisfiesDelaunayCondition(adjacentTriangles));

    }

}