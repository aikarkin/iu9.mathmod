package ru.bmstu.iu9.mathmod.lab2.rtree;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.bmstu.iu9.mathmod.lab2.geom.Vector2D;
import ru.bmstu.iu9.mathmod.lab2.geom.Triangle;

import java.util.Optional;

import static org.testng.Assert.*;
import static ru.bmstu.iu9.mathmod.lab2.geom.GeometryUtils.vec2d;

public class RTreeWrapperTest {
    private RTreeWrapper rTree = new RTreeWrapper();
    private Triangle expectedBoundingTr = new Triangle(vec2d(-1, 0), vec2d(1, 0), vec2d(0, 1));

    @BeforeClass
    public void init() {
        rTree.addTriangle(expectedBoundingTr);
        rTree.addTriangle(new Triangle(vec2d(-1, 0), vec2d(1, 0), vec2d(0, -1)));
        rTree.addTriangle(new Triangle(vec2d(-2, -2), vec2d(2, -2), vec2d(0, -1)));
    }


    @Test(priority = 3)
    public void testRemoveTriangle() {
        rTree.removeTriangle(expectedBoundingTr);
        assertEquals(rTree.size(), 2);
        assertEquals(rTree.findBoundingTriangles(vec2d(0, 0.5)).size(), 0);
    }

    @Test(priority = 1)
    public void testFindBoundingTriangle() {
        Optional<Triangle> trOpt = rTree.findFirstBoundingTriangle(vec2d(0, 0.5));

        assertTrue(trOpt.isPresent());
        assertEquals(expectedBoundingTr, trOpt.get());
    }

    @Test
    public void pointInTriangle() {
        Vector2D p1, p2, p3, pPos, pNeg;
        p1 = new Vector2D(-1, 0);
        p2 = new Vector2D(1, 0);
        p3 = new Vector2D(0, 1);
        pPos = new Vector2D(0, 0.5);
        pNeg = new Vector2D(0, -0.5);


        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p3, p2, p1), pPos));
        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p1, p2, p3), pPos));

        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p2, p1, p3), pPos));
        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p2, p3, p1), pPos));
        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p2, p3, p1), pPos));


        assertFalse(RTreeWrapper.pointInTriangle(new Triangle(p1, p2, p3), pNeg));
        assertFalse(RTreeWrapper.pointInTriangle(new Triangle(p3, p2, p1), pNeg));
    }

}