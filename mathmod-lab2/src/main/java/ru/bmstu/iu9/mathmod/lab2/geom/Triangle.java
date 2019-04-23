package ru.bmstu.iu9.mathmod.lab2.geom;

import java.util.*;

public class Triangle implements BoundedGeometry {

    private Point2D[] points;
    private Edge[] edges;

    public Triangle() {
        this.points = new Point2D[3];
        this.edges = new Edge[3];
    }

    public Triangle(Point2D a, Point2D b, Point2D c) {
        this();
        this.points = new Point2D[3];
        this.points[0] = a;
        this.points[1] = b;
        this.points[2] = c;
        this.calcEdges();
    }

    public Point2D[] points() {
        return points;
    }

    public Edge[] edges() {
        return edges;
    }

    public Point2D p1() {
        return points[0];
    }

    public Point2D p2() {
        return points[1];
    }

    public Point2D p3() {
        return points[2];
    }

    public Point2D getOppositePoint(Edge edge) {
        for (Point2D pt : points) {
            if(!pt.equals(edge.first()) && !pt.equals(edge.second()))
                return pt;
        }

        return null;
    }

    public void setPoint1(Point2D point) {
        this.points[0] = point;
        calcEdges();
    }

    public void setPoint2(Point2D point) {
        this.points[1] = point;
        calcEdges();
    }

    public void setPoint3(Point2D point) {
        this.points[2] = point;
        calcEdges();
    }

    public void setPoint1(double x, double y) {
        setPoint1(new Point2D(x, y));
    }

    public void setPoint2(double x, double y) {
        setPoint2(new Point2D(x, y));
    }

    public void setPoint3(double x, double y) {
        setPoint3(new Point2D(x, y));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triangle triangle = (Triangle) o;
        Set<Point2D> curPoints = new HashSet<>();
        Set<Point2D> otherPoints = new HashSet<>();
        Collections.addAll(curPoints, points);
        Collections.addAll(otherPoints, triangle.points);

        return curPoints.equals(otherPoints);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(points);
    }

    @Override
    public Rectangle getMbr() {

        double minX, minY, maxX, maxY;
        Optional<Double> minXOpt, minYOpt, maxXOpt, maxYOpt;

        minXOpt = Arrays.stream(this.points).map(Point2D::x).min(Double::compareTo);
        minYOpt = Arrays.stream(this.points).map(Point2D::y).min(Double::compareTo);
        maxXOpt = Arrays.stream(this.points).map(Point2D::x).max(Double::compareTo);
        maxYOpt = Arrays.stream(this.points).map(Point2D::y).max(Double::compareTo);

        minX = minXOpt.orElseGet(() -> this.p1().x());
        maxX = maxXOpt.orElseGet(() -> this.p1().x());
        minY = minYOpt.orElseGet(() -> this.p1().y());
        maxY = maxYOpt.orElseGet(() -> this.p1().y());

        return new Rectangle(new Point2D(minX, minY), maxX - minX, maxY - minY);
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
