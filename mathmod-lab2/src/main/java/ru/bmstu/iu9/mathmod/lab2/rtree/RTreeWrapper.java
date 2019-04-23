package ru.bmstu.iu9.mathmod.lab2.rtree;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import ru.bmstu.iu9.mathmod.lab2.geom.Point2D;
import ru.bmstu.iu9.mathmod.lab2.geom.Triangle;

import java.util.*;

public class RTreeWrapper {

    private static final int MIN_ENTRIES = 2;
    private static final int MAX_ENTRIES = 6;

    private RTree<Integer, Rectangle> rTree;

    private Map<Triangle, Integer> triangleToId = new HashMap<>();
    private Map<Integer, Triangle> idToTriangle = new HashMap<>();
    private int index = 0;

    public RTreeWrapper() {
        rTree = RTree
                .minChildren(MIN_ENTRIES)
                .maxChildren(MAX_ENTRIES)
                .create();
    }

    public void addTriangles(Iterable<Triangle> triangles) {
        for (Triangle tr : triangles) {
            this.addTriangle(tr);
        }
    }

    public void addTriangles(Triangle ...triangles) {
        for (Triangle tr : triangles) {
            this.addTriangle(tr);
        }
    }

    public void addTriangle(Triangle triangle) {
        if (triangleToId.containsKey(triangle))
            return;

        triangleToId.put(triangle, index);
        idToTriangle.put(index, triangle);
        this.rTree = rTree.add(index, triangle.getMbr().toRTreeRectangle());

        index++;
    }

    public void removeTriangle(Triangle triangle) {
        if (!triangleToId.containsKey(triangle))
            return;

        rTree = rTree.delete(triangleToId.get(triangle), triangle.getMbr().toRTreeRectangle());
    }

    public List<Triangle> findBoundingTriangles(Point2D lookupDot) {
        List<Triangle> foundTriangles = new ArrayList<>();
        Iterable<Entry<Integer, Rectangle>> entries = rTree.search(Geometries.point(lookupDot.x(), lookupDot.y()))
                .toBlocking()
                .toIterable();

        for(Entry<Integer, Rectangle> entry : entries) {
            Triangle tr = idToTriangle.get(entry.value());
            if(pointInTriangle(tr, lookupDot)) {
                foundTriangles.add(tr);
            }
        }

        return foundTriangles;
    }

    public Optional<Triangle> findFirstBoundingTriangle(Point2D lookupDot) {
        List<Triangle> foundTriangles = findBoundingTriangles(lookupDot);
        return foundTriangles.size() == 0 ? Optional.empty() : Optional.of(foundTriangles.get(0));
    }

    public int size() {
        return rTree.size();
    }

    public static boolean pointInTriangle(Triangle tr, Point2D dot) {
        boolean s1, s2, s3;

        s1 = pointsSign(dot, tr.p1(), tr.p2()) <= 0.0;
        s2 = pointsSign(dot, tr.p2(), tr.p3()) <= 0.0;
        s3 = pointsSign(dot, tr.p3(), tr.p1()) <= 0.0;

        return ((s1 == s2) && (s2 == s3));
    }

    private static double pointsSign(Point2D p, Point2D p1, Point2D p2) {
        return (p1.x() - p.x()) * (p2.y() - p.y()) - (p1.y() - p.y()) * (p2.x() - p.x());
    }

}