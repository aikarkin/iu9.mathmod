package ru.bmstu.iu9.mathmod.lab2.geom;

import java.util.Objects;

public class Edge {
    private Point2D firstPoint;
    private Point2D secondPoint;

    public Edge(Point2D first, Point2D second) {
        this.firstPoint = first;
        this.secondPoint = second;
    }

    public Point2D first() {
        return firstPoint;
    }

    public Point2D second() {
        return secondPoint;
    }

    public static Edge of(Point2D p1, Point2D p2) {
        return new Edge(p1, p2);
    }

    // (p1, p2) == (p2, p1)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge otherEdge = (Edge) o;

        Point2D cMinPt = minPoint(firstPoint, secondPoint);
        Point2D cMaxPt = maxPoint(firstPoint, secondPoint);
        Point2D oMinPt = minPoint(otherEdge.firstPoint, otherEdge.secondPoint);
        Point2D oMaxPt = maxPoint(otherEdge.firstPoint, otherEdge.secondPoint);

        return Objects.equals(cMinPt, oMinPt) && Objects.equals(cMaxPt, oMaxPt);
    }

    // hash(p1, p2) == hash(p2, p1)
    @Override
    public int hashCode() {
        Point2D minPoint = minPoint(firstPoint, secondPoint);
        Point2D maxPoint = maxPoint(firstPoint, secondPoint);
        return Objects.hash(minPoint, maxPoint);
    }

    @Override
    public String toString() {
        return String.format("%s -- %s", firstPoint, secondPoint);
    }

    private static Point2D minPoint(Point2D pt1, Point2D pt2) {
        if(pt1.x() < pt2.x()) {
            return pt1;
        } else if(pt1.x() > pt2.x()) {
            return pt2;
        }

        if(pt1.y() < pt2.y()) {
            return pt1;
        } else if(pt1.y() > pt2.y()) {
            return pt2;
        }

        return pt1;
    }

    private static Point2D maxPoint(Point2D pt1, Point2D pt2) {
        Point2D pt1st = minPoint(pt1, pt2);
        return pt1st.equals(pt1) ? pt2 : pt1;
    }

}
