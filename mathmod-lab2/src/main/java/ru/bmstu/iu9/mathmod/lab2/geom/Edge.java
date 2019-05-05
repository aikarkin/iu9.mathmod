package ru.bmstu.iu9.mathmod.lab2.geom;

import java.util.Objects;

public class Edge {

    private Vector2D firstPoint;
    private Vector2D secondPoint;

    public Edge(Vector2D first, Vector2D second) {
        this.firstPoint = first;
        this.secondPoint = second;
    }

    public static Edge of(Vector2D p1, Vector2D p2) {
        return new Edge(p1, p2);
    }

    public Vector2D first() {
        return firstPoint;
    }

    public Vector2D second() {
        return secondPoint;
    }

    // (p1, p2) == (p2, p1)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge otherEdge = (Edge) o;

        Vector2D cMinPt = minPoint(firstPoint, secondPoint);
        Vector2D cMaxPt = maxPoint(firstPoint, secondPoint);
        Vector2D oMinPt = minPoint(otherEdge.firstPoint, otherEdge.secondPoint);
        Vector2D oMaxPt = maxPoint(otherEdge.firstPoint, otherEdge.secondPoint);

        return Objects.equals(cMinPt, oMinPt) && Objects.equals(cMaxPt, oMaxPt);
    }

    // hash(p1, p2) == hash(p2, p1)
    @Override
    public int hashCode() {
        Vector2D minPoint = minPoint(firstPoint, secondPoint);
        Vector2D maxPoint = maxPoint(firstPoint, secondPoint);
        return Objects.hash(minPoint, maxPoint);
    }

    @Override
    public String toString() {
        return String.format("%s -- %s", firstPoint, secondPoint);
    }

    public Vector2D toVector() {
        return new Vector2D(second().x() - first().x(), second().y() - first().y());
    }

    private static Vector2D minPoint(Vector2D pt1, Vector2D pt2) {
        if (pt1.x() < pt2.x()) {
            return pt1;
        } else if (pt1.x() > pt2.x()) {
            return pt2;
        }

        if (pt1.y() < pt2.y()) {
            return pt1;
        } else if (pt1.y() > pt2.y()) {
            return pt2;
        }

        return pt1;
    }

    private static Vector2D maxPoint(Vector2D pt1, Vector2D pt2) {
        Vector2D pt1st = minPoint(pt1, pt2);
        return pt1st.equals(pt1) ? pt2 : pt1;
    }

}
