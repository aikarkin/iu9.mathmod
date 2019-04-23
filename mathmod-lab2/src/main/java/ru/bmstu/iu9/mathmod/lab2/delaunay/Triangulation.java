package ru.bmstu.iu9.mathmod.lab2.delaunay;

import ru.bmstu.iu9.mathmod.lab2.geom.*;
import ru.bmstu.iu9.mathmod.lab2.rtree.RTreeWrapper;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.util.Arrays.asList;
import static ru.bmstu.iu9.mathmod.lab2.delaunay.DelaunayUtil.checkDelaunayCondition;

public class Triangulation {

    private static final double POINT_EPS = 0.001;
    private static final double EDGE_EPS = 0.001;
    private static final double RECT_EPS = 20.0;
    private RTreeWrapper rTree = new RTreeWrapper();
    private Map<Edge, AdjacentTriangles> adjacentTrianglesMap = new HashMap<>();
    private Rectangle superRect;
    private Set<Point2D> pointsSet;

    public Triangulation(List<Point2D> points) {
        if (points.size() == 0)
            return;

        pointsSet = new HashSet<>(points);

        this.superRect = getSuperRectangle(points);
        addInitialTriangles(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            addPoint(points.get(i));
        }
    }


    public Rectangle getSuperRectangle() {
        return superRect;
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(adjacentTrianglesMap.keySet());
    }

    public List<Triangle> getTriangles() {

        return adjacentTrianglesMap.values()
                .stream()
                .map(AdjacentTriangles::getRhsTriangle)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Triangle> findBoundingTriangle(Point2D pt) {
        Optional<Triangle> foundTrOpt = rTree.findFirstBoundingTriangle(pt);
        Set<Edge> superStructEdges = new HashSet<>(asList(getSuperRectangle().edges()));
        Set<Point2D> superStructPoints = new HashSet<>(asList(getSuperRectangle().points()));

        if (foundTrOpt.isPresent()) {
            for (Edge e : foundTrOpt.get().edges()) {
                if (superStructEdges.contains(e)) {
                    return Optional.empty();
                }
            }

            for (Point2D trPt : foundTrOpt.get().points()) {
                if (superStructPoints.contains(trPt)) {
                    return Optional.empty();
                }
            }
        }

        return foundTrOpt;
    }

    private void addInitialTriangles(Point2D p) {
        Point2D[] rectPts = superRect.points();
        int n = rectPts.length;
        Triangle curTr = null, firstTr = new Triangle(rectPts[n - 1], rectPts[0], p), prevTr;
        prevTr = firstTr;
        rTree.addTriangle(prevTr);
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

    private void addPoint(Point2D p) {
        Optional<Triangle> boundTrOpt = rTree.findFirstBoundingTriangle(p);
        System.out.println("Add point: " + p);

        if (!boundTrOpt.isPresent()) {
            throw new IllegalStateException("Point out of super structure");
        }

        Triangle boundTr = boundTrOpt.get();
        Triangle[] splitedTrs = splitTriangles(boundTr, p);
        rTree.addTriangles(splitedTrs);
        rebuildTriangulation(splitedTrs);
    }

    private void rebuildTriangulation(Triangle[] splitedTrs) {
        Queue<Triangle> queue = new LinkedList<>();
        Set<Edge> used = new HashSet<>(findCommonEdges(splitedTrs));
        Collections.addAll(queue, splitedTrs);

        while (!queue.isEmpty()) {
            Triangle tr = queue.poll();

            for (Edge edge : tr.edges()) {
                if (!used.contains(edge)) {
                    AdjacentTriangles adjacentTriangles = adjacentTrianglesMap.get(edge);
                    Triangle adjacentTr = adjacentTriangles.adjacentTriangle(tr);
                    if (adjacentTr == null) {
                        continue;
                    }
                    Point2D oppositePoint = adjacentTr.getOppositePoint(edge);
                    if (!checkDelaunayCondition(tr, oppositePoint)) {
                        Triangle[] flipped = flipTriangles(adjacentTriangles);
                        Collections.addAll(queue, flipped);
                    }
                    used.add(edge);
                }
            }
        }
    }

    private Triangle[] flipTriangles(AdjacentTriangles adjacentTriangles) {
        Edge commonEdge = adjacentTriangles.getCommonEdge();
        Triangle lhsTr, rhsTr;
        Point2D pt1, pt2, pt3, pt4;
        lhsTr = adjacentTriangles.getLhsTriangle();
        rhsTr = adjacentTriangles.getRhsTriangle();
        pt1 = commonEdge.first();
        pt2 = commonEdge.second();
        pt3 = lhsTr.getOppositePoint(commonEdge);
        pt4 = rhsTr.getOppositePoint(commonEdge);

        adjacentTrianglesMap.remove(commonEdge);
        rTree.removeTriangle(lhsTr);
        rTree.removeTriangle(rhsTr);

        lhsTr = new Triangle(pt4, pt1, pt3);
        rhsTr = new Triangle(pt3, pt2, pt4);
        commonEdge = new Edge(pt4, pt3);

        adjacentTrianglesMap.put(commonEdge, new AdjacentTriangles(lhsTr, rhsTr));
        rTree.addTriangles(lhsTr, rhsTr);

        return new Triangle[]{lhsTr, rhsTr};
    }

    private Triangle[] splitTriangles(Triangle boundTr, Point2D p) {
        rTree.removeTriangle(boundTr);
        for (Point2D trPnt : boundTr.points()) {
            // point near triangle vertex
            if (trPnt.getDistance(p) < POINT_EPS) {
                return new Triangle[]{boundTr};
            }
        }

        for (int i = 0; i < 3; i++) {
            Edge splitedEdge = boundTr.edges()[i];
            Edge nextEdge = boundTr.edges()[(i + 1) % 3];
            Point2D projection = new Point2D(p.projection(splitedEdge.second().subtract(splitedEdge.first())));
            double dist = p.subtract(projection).getNorm();
            // point near edge
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
            Triangle trAdjacent = adjacentTrianglesMap.get(tr.edges()[1]).adjacentTriangle(boundTr);
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

    private static Rectangle getSuperRectangle(List<Point2D> points) {
        Point2D p1 = points.get(0);
        Optional<Double> minX, minY, maxX, maxY;
        double x1, y1, x2, y2, w, h;
        minX = points.stream().map(Point2D::x).min(Double::compareTo);
        minY = points.stream().map(Point2D::y).min(Double::compareTo);
        maxX = points.stream().map(Point2D::x).max(Double::compareTo);
        maxY = points.stream().map(Point2D::y).max(Double::compareTo);

        x1 = minX.orElseGet(p1::x) - RECT_EPS;
        y1 = minY.orElseGet(p1::y) - RECT_EPS;
        x2 = maxX.orElseGet(p1::x) + RECT_EPS;
        y2 = maxY.orElseGet(p1::y) + RECT_EPS;

        w = abs(x2 - x1);
        h = abs(y2 - y1);
        return new Rectangle(
                new Point2D(x1, y1),
                w,
                h
        );
    }

}
