package ru.bmstu.iu9.mathmod.lab2.geom;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class EdgeTest {
    private Edge e1, e2;

    @BeforeClass
    public void tearUp() {
        Vector2D p1, p2;
        p1 = new Vector2D(1, 1);
        p2 = new Vector2D(2, 2);
        e1 = new Edge(p1, p2);
        e2 = new Edge(p2, p1);
    }

    @Test
    public void testEquals() {
        assertEquals(e2, e1);
        assertEquals(e1, e2);
    }

    @Test
    public void testHashCode() {
        assertEquals(e1.hashCode(), e2.hashCode());
    }

}