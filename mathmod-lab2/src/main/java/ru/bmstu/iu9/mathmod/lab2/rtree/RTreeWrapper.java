package ru.bmstu.iu9.mathmod.lab2.rtree;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import ru.bmstu.iu9.mathmod.lab2.geom.Vector2D;
import ru.bmstu.iu9.mathmod.lab2.geom.Triangle;

import java.util.*;

import static ru.bmstu.iu9.mathmod.lab2.geom.GeometryUtils.pointInTriangle;

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

    public void removeTraingles(Triangle ...triangles) {
        for(Triangle tr : triangles) {
            removeTriangle(tr);
        }
    }

    public List<Triangle> findBoundingTriangles(Vector2D lookupDot) {
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

    public Optional<Triangle> findFirstBoundingTriangle(Vector2D lookupDot) {
        List<Triangle> foundTriangles = findBoundingTriangles(lookupDot);
        return foundTriangles.size() == 0 ? Optional.empty() : Optional.of(foundTriangles.get(0));
    }

    public int size() {
        return rTree.size();
    }

}