package ru.bmstu.iu9.mathmod.lab2.geom;

import com.github.davidmoten.rtree.geometry.Geometries;

public class Rectangle {

    private static final int LBP_IDX = 0;
    private static final int LTP_IDX = 1;
    private static final int RTP_IDX = 2;
    private static final int RBP_IDX = 3;
    private Vector2D[] points;
    private double width;
    private double height;

    public Rectangle() {

    }

    public Rectangle(Vector2D lbPoint, double width, double height) {
        this.points = new Vector2D[4];
        this.points[LBP_IDX] = lbPoint;
        this.width = width;
        this.height = height;
        calcPoints();
    }

    public void set(Vector2D lbPoint, double width, double height) {
        this.points[LBP_IDX] = lbPoint;
        this.width = width;
        this.height = height;
        this.calcPoints();
    }

    public Vector2D getLeftTopPoint() {
        return points[LTP_IDX];
    }

    public void setLeftTopPoint(Vector2D ltPoint) {
        this.points[LTP_IDX] = ltPoint;
        calcPoints();
    }

    public Vector2D[] points() {
        return this.points;
    }

    public Vector2D getLeftBottomPoint() {
        return points[LBP_IDX];
    }

    public Vector2D getRightTopPoint() {
        return points[RTP_IDX];
    }

    public Vector2D getRightBottomPoint() {
        return points[RBP_IDX];
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        calcPoints();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        calcPoints();
    }
    public com.github.davidmoten.rtree.geometry.Rectangle toRTreeRectangle() {
        return Geometries.rectangle(
                getLeftBottomPoint().x(),
                getLeftBottomPoint().y(),
                getRightTopPoint().x(),
                getRightTopPoint().y()
        );
    }

    public Edge[] edges() {
        return new Edge[] {
                Edge.of(getLeftBottomPoint(), getLeftTopPoint()),
                Edge.of(getLeftTopPoint(), getRightTopPoint()),
                Edge.of(getRightTopPoint(), getRightBottomPoint()),
                Edge.of(getRightBottomPoint(), getLeftBottomPoint()),
        };
    }

    private void calcPoints() {
        Vector2D leftBottomPoint = this.points[LBP_IDX];
        this.points[LTP_IDX] = new Vector2D(leftBottomPoint.x(), leftBottomPoint.y() + height);
        this.points[RTP_IDX] = new Vector2D(leftBottomPoint.x() + width, leftBottomPoint.y() + height);
        this.points[RBP_IDX] = new Vector2D(leftBottomPoint.x() + width, leftBottomPoint.y());
    }


}
