package ru.bmstu.iu9.mathmod.lab2.geom;

import java.util.*;

public class Triangle implements BoundedGeometry {

    private Vector2D[] points;
    private Edge[] edges;

    public Triangle() {
        this.points = new Vector2D[3];
        this.edges = new Edge[3];
    }

    public Triangle(Vector2D a, Vector2D b, Vector2D c) {
        this();
        this.points = new Vector2D[3];
        this.points[0] = a;
        this.points[1] = b;
        this.points[2] = c;
        this.calcEdges();
    }

    public Vector2D[] points() {
        return points;
    }

    public Edge[] edges() {
        return edges;
    }

    public Vector2D p1() {
        return points[0];
    }

    public Vector2D p2() {
        return points[1];
    }

    public Vector2D p3() {
        return points[2];
    }

    public Vector2D getOppositePoint(Edge edge) {
        for (Vector2D pt : points) {
            if(!pt.equals(edge.first()) && !pt.equals(edge.second()))
                return pt;
        }

        return null;
    }

    public void setPoint1(Vector2D point) {
        this.points[0] = point;
        calcEdges();
    }

    public void setPoint2(Vector2D point) {
        this.points[1] = point;
        calcEdges();
    }

    public void setPoint3(Vector2D point) {
        this.points[2] = point;
        calcEdges();
    }

    public void setPoint1(double x, double y) {
        setPoint1(new Vector2D(x, y));
    }

    public void setPoint2(double x, double y) {
        setPoint2(new Vector2D(x, y));
    }

    public void setPoint3(double x, double y) {
        setPoint3(new Vector2D(x, y));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triangle triangle = (Triangle) o;
        Set<Vector2D> curPoints = new HashSet<>();
        Set<Vector2D> otherPoints = new HashSet<>();
        Collections.addAll(curPoints, points);
        Collections.addAll(otherPoints, triangle.points);

        return curPoints.equals(otherPoints);
    }

    @Override
    public int hashCode() {
        return new HashSet<>(Arrays.asList(points)).hashCode();
    }

    @Override
    public Rectangle getMbr() {

        double minX, minY, maxX, maxY;
        Optional<Double> minXOpt, minYOpt, maxXOpt, maxYOpt;

        minXOpt = Arrays.stream(this.points).map(Vector2D::x).min(Double::compareTo);
        minYOpt = Arrays.stream(this.points).map(Vector2D::y).min(Double::compareTo);
        maxXOpt = Arrays.stream(this.points).map(Vector2D::x).max(Double::compareTo);
        maxYOpt = Arrays.stream(this.points).map(Vector2D::y).max(Double::compareTo);

        minX = minXOpt.orElseGet(() -> this.p1().x());
        maxX = maxXOpt.orElseGet(() -> this.p1().x());
        minY = minYOpt.orElseGet(() -> this.p1().y());
        maxY = maxYOpt.orElseGet(() -> this.p1().y());

        return new Rectangle(new Vector2D(minX, minY), maxX - minX, maxY - minY);
    }

    private void calcEdges() {
        this.edges[0] = new Edge(this.points[0], this.points[1]);
        this.edges[1] = new Edge(this.points[1], this.points[2]);
        this.edges[2] = new Edge(this.points[2], this.points[0]);
    }

    @Override
    public String toString() {
        return String.format("[ %s, %s, %s ]", p1(), p2(), p3());
    }

}
