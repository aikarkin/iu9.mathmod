package ru.bmstu.iu9.mathmod.lab2.rtree;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.bmstu.iu9.mathmod.lab2.geom.Point2D;
import ru.bmstu.iu9.mathmod.lab2.geom.Triangle;

import java.util.Optional;

import static org.testng.Assert.*;
import static ru.bmstu.iu9.mathmod.lab2.geom.GeometryUtils.point;

public class RTreeWrapperTest {
    private RTreeWrapper rTree = new RTreeWrapper();
    private Triangle expectedBoundingTr = new Triangle(point(-1, 0), point(1, 0), point(0, 1));

    @BeforeClass
    public void init() {
        rTree.addTriangle(expectedBoundingTr);
        rTree.addTriangle(new Triangle(point(-1, 0), point(1, 0), point(0, -1)));
        rTree.addTriangle(new Triangle(point(-2, -2), point(2, -2), point(0, -1)));
    }


    @Test(priority = 2)
    public void testRemoveTriangle() {
        rTree.removeTriangle(expectedBoundingTr);
        assertEquals(rTree.size(), 2);
        assertEquals(rTree.findBoundingTriangles(point(0, 0.5)).size(), 0);
    }

    @Test(priority = 1)
    public void testFindBoundingTriangle() {
        Optional<Triangle> trOpt = rTree.findFirstBoundingTriangle(point(0, 0.5));

        assertTrue(trOpt.isPresent());
        assertEquals(expectedBoundingTr, trOpt.get());
    }

    @Test
    public void pointInTriangle() {
        Point2D p1, p2, p3, pPos, pNeg;
        p1 = new Point2D(-1, 0);
        p2 = new Point2D(1, 0);
        p3 = new Point2D(0, 1);
        pPos = new Point2D(0, 0.5);
        pNeg = new Point2D(0, -0.5);


        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p3, p2, p1), pPos));
        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p1, p2, p3), pPos));

        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p2, p1, p3), pPos));
        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p2, p3, p1), pPos));
        assertTrue(RTreeWrapper.pointInTriangle(new Triangle(p2, p3, p1), pPos));


        assertFalse(RTreeWrapper.pointInTriangle(new Triangle(p1, p2, p3), pNeg));
        assertFalse(RTreeWrapper.pointInTriangle(new Triangle(p3, p2, p1), pNeg));
    }

}