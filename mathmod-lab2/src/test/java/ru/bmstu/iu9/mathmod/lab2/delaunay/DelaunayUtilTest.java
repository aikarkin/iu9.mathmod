package ru.bmstu.iu9.mathmod.lab2.delaunay;

import org.testng.annotations.Test;
import ru.bmstu.iu9.mathmod.lab2.geom.Triangle;
import ru.bmstu.iu9.mathmod.lab2.geom.Vector2D;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static ru.bmstu.iu9.mathmod.lab2.geom.GeometryUtils.vec2d;

public class DelaunayUtilTest {

    @Test
    public void testCheckDelaunayCondition() {
        Triangle tr1 = new Triangle(vec2d(215, 145), vec2d(412, 43), vec2d(522, 33));
        Vector2D pt1 = vec2d(300, 200);
        assertFalse(DelaunayUtil.checkDelaunayCondition(tr1, pt1));

        Triangle tr2 = new Triangle(vec2d(2, 8), vec2d(8, 8), vec2d(1, 5));
        Vector2D pt2 = vec2d(1, 1);
        DelaunayUtil.checkDelaunayCondition(tr2, pt2);
        assertTrue(DelaunayUtil.checkDelaunayCondition(tr2, pt2));
    }

}