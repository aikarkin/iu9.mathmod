package ru.bmstu.iu9.mathmod.lab2.geom;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Point2D extends ArrayRealVector {

    public Point2D() {
        super(2);
    }

    public Point2D(double x, double y) {
        super(new double[]{x, y});
    }

    public Point2D(RealVector vector) {
        this(vector.getEntry(0), vector.getEntry(1));
    }

    public double x() {
        return getEntry(0);
    }

    public double y() {
        return getEntry(1);
    }

    public void setX(double x) {
        this.setEntry(0, x);
    }

    public void setY(double y) {
        this.setEntry(1, y);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x(), y());
    }

}
