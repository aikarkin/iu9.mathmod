package ru.bmstu.iu9.mathmod.lab2.delaunay;

import ru.bmstu.iu9.mathmod.lab2.geom.*;
import ru.bmstu.iu9.mathmod.lab2.rtree.RTreeWrapper;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static ru.bmstu.iu9.mathmod.lab2.delaunay.DelaunayUtil.satisfiesDelaunayCondition;
import static ru.bmstu.iu9.mathmod.lab2.geom.GeometryUtils.isConvexPolygon;

public class Triangulation {

    private static final double POINT_EPS = 0.001;
    private static final double EDGE_EPS = 0.001;
    private static final double RECT_EPS = 10.0;
    private RTreeWrapper rTree = new RTreeWrapper();
    private Map<Edge, AdjacentTriangles> adjacentTrianglesMap = new HashMap<>();
    private Rectangle superRect;

    public Triangulation(List<Vector2D> points) {
        if (points.size() == 0)
            return;

        this.superRect = getSuperRectangle(points);
        addInitialTriangles(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            addPoint(points.get(i));
        }
    }

    public Rectangle getSuperRectangle() {
        return superRect;
    }

    public List<Triangle> getTriangles() {
        return adjacentTrianglesMap.values()
                .stream()
                .map(AdjacentTriangles::getRhsTriangle)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void addInitialTriangles(Vector2D p) {
        Vector2D[] rectPts = superRect.points();
        int n = rectPts.length;
        Triangle curTr = null, firstTr = new Triangle(rectPts[n - 1], rectPts[0], p), prevTr;
        prevTr = firstTr;
        rTree.addTriangle(firstTr);
        adjacentTrianglesMap.put(firstTr.edges()[0], new AdjacentTriangles(firstTr, firstTr.edges()[0]));

        for (int i = 1; i < n; i++) {
            curTr = new Triangle(rectPts[i - 1], rectPts[i], p);
            adjacentTrianglesMap.put(curTr.edges()[0], new AdjacentTriangles(curTr, prevTr.edges()[0]));
            adjacentTrianglesMap.put(prevTr.edges()[1], new AdjacentTriangles(prevTr, curTr));
            rTree.addTriangle(curTr);
            prevTr = curTr;
        }
        if (curTr != null) {
            adjacentTrianglesMap.put(curTr.edges()[1], new AdjacentTriangles(curTr, firstTr));
        }
    }

    private void addPoint(Vector2D p) {
        Optional<Triangle> boundTrOpt = rTree.findFirstBoundingTriangle(p);

        if (!boundTrOpt.isPresent()) {
            throw new IllegalStateException("Point out of super structure");
        }

        Triangle boundTr = boundTrOpt.get();
        rTree.removeTriangle(boundTr);
        Triangle[] splitedTrs = splitTriangles(boundTr, p);
        rTree.addTriangles(splitedTrs);
        rebuildTriangulation(splitedTrs);
    }

    private void rebuildTriangulation(Triangle[] splitedTrs) {
        Queue<Edge> queue = new LinkedList<>();
        Set<Edge> used = new HashSet<>(findCommonEdges(splitedTrs));
        Arrays.stream(splitedTrs)
                .flatMap(tr -> Arrays.stream(tr.edges()))
                .filter(e -> !used.contains(e))
                .forEach(queue::add);

        while (!queue.isEmpty()) {
            Edge polledEdge = queue.poll();
            used.add(polledEdge);

            AdjacentTriangles adjacentTriangles = adjacentTrianglesMap.get(polledEdge);

            if (adjacentTriangles == null) {
                throw new RuntimeException("Adjacent triangles are null.");
            }

            Triangle lhsTr = adjacentTriangles.getLhsTriangle();
            Triangle rhsTr = adjacentTriangles.getRhsTriangle();

            if (lhsTr == null || rhsTr == null)
                continue;


            if (isConvexPolygon(adjacentTriangles.pointsSorted()) && !satisfiesDelaunayCondition(adjacentTriangles)) {
                Triangle[] flippedTriangles = flipTriangles(adjacentTriangles);

                for (Triangle tr : flippedTriangles) {
                    for (Edge e : tr.edges()) {
                        if (!used.contains(e)) {
                            queue.add(e);
                        }
                    }
                }
            }
        }
    }


    private Triangle[] flipTriangles(AdjacentTriangles adjacentTriangles) {
        Edge commonEdge = adjacentTriangles.getCommonEdge();
        Triangle oldLhsTr, oldRhsTr;
        Vector2D pt1, pt2, pt3, pt4;
        oldLhsTr = adjacentTriangles.getLhsTriangle();
        oldRhsTr = adjacentTriangles.getRhsTriangle();
        pt1 = adjacentTriangles.pointsSorted()[0];
        pt2 = adjacentTriangles.pointsSorted()[1];
        pt3 = adjacentTriangles.pointsSorted()[2];
        pt4 = adjacentTriangles.pointsSorted()[3];

        adjacentTrianglesMap.remove(commonEdge);
        rTree.removeTraingles(oldLhsTr, oldRhsTr);
        Map<Edge, Triangle> oldAdjacentTriangles = new HashMap<>();

        for (Triangle tr : new Triangle[]{oldLhsTr, oldRhsTr}) {
            for (Edge e : tr.edges()) {
                if (!e.equals(commonEdge)) {
                    oldAdjacentTriangles.put(e, adjacentTrianglesMap.get(e).adjacentTriangle(tr));
                }
            }
        }

        Triangle newLhsTr = new Triangle(pt2, pt3, pt4);
        Triangle newRhsTr = new Triangle(pt4, pt1, pt2);
        AdjacentTriangles flippedAdjacentTrs = new AdjacentTriangles(newLhsTr, newRhsTr);

        for (Triangle tr : new Triangle[]{newLhsTr, newRhsTr}) {
            for (Edge e : tr.edges()) {
                if (!e.equals(flippedAdjacentTrs.getCommonEdge())) {
                    if (oldAdjacentTriangles.get(e) == null) {
                        adjacentTrianglesMap.put(e, new AdjacentTriangles(tr, e));
                    } else {
                        adjacentTrianglesMap.put(e, new AdjacentTriangles(tr, oldAdjacentTriangles.get(e)));
                    }
                }
            }
        }

        adjacentTrianglesMap.put(flippedAdjacentTrs.getCommonEdge(), flippedAdjacentTrs);
        rTree.addTriangles(newLhsTr, newRhsTr);

        return new Triangle[]{oldLhsTr, oldRhsTr};
    }

    private Triangle[] splitTriangles(Triangle boundTr, Vector2D p) {
        for (Vector2D trPnt : boundTr.points()) {
            // vec2d near triangle vertex
            if (trPnt.getDistance(p) < POINT_EPS) {
                return new Triangle[]{boundTr};
            }
        }

        for (int i = 0; i < 3; i++) {
            Edge splitedEdge = boundTr.edges()[i];
            Edge nextEdge = boundTr.edges()[(i + 1) % 3];
            Vector2D projection = new Vector2D(p.projection(splitedEdge.second().subtract(splitedEdge.first())));
            double dist = p.subtract(projection).getNorm();
            // vec2d near edge
            if (dist < EDGE_EPS) {
                Triangle tr1 = new Triangle(nextEdge.second(), splitedEdge.first(), projection);
                Triangle tr2 = new Triangle(nextEdge.second(), projection, splitedEdge.second());

                AdjacentTriangles oldAdjacentTrs = adjacentTrianglesMap.get(splitedEdge);
                Edge adjacentEdge = new Edge(nextEdge.second(), projection);
                Edge e1 = new Edge(splitedEdge.first(), projection);
                Edge e2 = new Edge(projection, splitedEdge.second());

                adjacentTrianglesMap.put(e1, new AdjacentTriangles(tr1, oldAdjacentTrs.adjacentTriangle(boundTr)));
                adjacentTrianglesMap.put(e2, new AdjacentTriangles(tr2, oldAdjacentTrs.adjacentTriangle(boundTr)));
                adjacentTrianglesMap.put(adjacentEdge, new AdjacentTriangles(tr1, tr2));
                adjacentTrianglesMap.remove(splitedEdge);

                return new Triangle[]{tr1, tr2};
            }
        }

        Triangle tr1 = new Triangle(p, boundTr.p1(), boundTr.p2());
        Triangle tr2 = new Triangle(p, boundTr.p2(), boundTr.p3());
        Triangle tr3 = new Triangle(p, boundTr.p3(), boundTr.p1());

        // inner triangles:
        adjacentTrianglesMap.put(tr1.edges()[0], new AdjacentTriangles(tr3, tr1));
        adjacentTrianglesMap.put(tr2.edges()[0], new AdjacentTriangles(tr1, tr2));
        adjacentTrianglesMap.put(tr3.edges()[0], new AdjacentTriangles(tr2, tr3));

        // overwrite outer triangles:
        for (Triangle tr : new Triangle[]{tr1, tr2, tr3}) {
            // tr.edges()[1] - outer edge
            Triangle trAdjacent;

            trAdjacent = adjacentTrianglesMap.get(tr.edges()[1]).adjacentTriangle(boundTr);

            AdjacentTriangles adjacentTriangles = (trAdjacent == null)
                    ? new AdjacentTriangles(tr, tr.edges()[1])
                    : new AdjacentTriangles(tr, trAdjacent);

            adjacentTrianglesMap.put(tr.edges()[1], adjacentTriangles);
        }

        return new Triangle[]{tr1, tr2, tr3};
    }

    private List<Edge> findCommonEdges(Triangle... triangles) {
        List<Edge> commonEdges = new ArrayList<>();
        Set<Triangle> trianglesSet = new HashSet<>();
        Collections.addAll(trianglesSet, triangles);

        for (Triangle tr : triangles) {
            for (Edge edge : tr.edges()) {
                AdjacentTriangles adjacent = adjacentTrianglesMap.get(edge);
                if (adjacent != null && trianglesSet.contains(adjacent.adjacentTriangle(tr))) {
                    commonEdges.add(edge);
                }
            }
        }

        return commonEdges;
    }

    private static Rectangle getSuperRectangle(List<Vector2D> points) {
        Vector2D p1 = points.get(0);
        Optional<Double> minX, minY, maxX, maxY;
        double x1, y1, x2, y2, w, h;
        minX = points.stream().map(Vector2D::x).min(Double::compareTo);
        minY = points.stream().map(Vector2D::y).min(Double::compareTo);
        maxX = points.stream().map(Vector2D::x).max(Double::compareTo);
        maxY = points.stream().map(Vector2D::y).max(Double::compareTo);

        x1 = minX.orElseGet(p1::x);
        y1 = minY.orElseGet(p1::y);
        x2 = maxX.orElseGet(p1::x);
        y2 = maxY.orElseGet(p1::y);

        w = abs(x2 - x1);
        h = abs(y2 - y1);
        return new Rectangle(
                new Vector2D(x1 - RECT_EPS, y1 - RECT_EPS),
                w + 2.0 * RECT_EPS,
                h + 2.0 * RECT_EPS
        );
    }

}