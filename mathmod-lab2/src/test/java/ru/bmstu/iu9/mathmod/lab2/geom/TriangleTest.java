package ru.bmstu.iu9.mathmod.lab2.geom;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TriangleTest {
    @Test
    public void testHashCodeEquals() {
        Vector2D p1, p2, p3;
        p1 = new Vector2D(0.0, -0.5);
        p2 = new Vector2D(1.0, 1.5);
        p3 = new Vector2D(-3.0, 2);

        Triangle tr1 = new Triangle(p1, p2, p3);
        Triangle tr2 = new Triangle(p3, p2, p1);

        assertEquals(tr1, tr2);
        assertEquals(tr1.hashCode(), tr2.hashCode());
    }
}
