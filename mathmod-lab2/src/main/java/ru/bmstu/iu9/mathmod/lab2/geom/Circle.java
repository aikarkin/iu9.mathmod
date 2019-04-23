package ru.bmstu.iu9.mathmod.lab2.geom;

import org.apache.commons.math3.linear.RealVector;

public class Circle {
    private Point2D center;
    double r;

    public Circle(double radius, double x0, double y0) {
        this.center = new Point2D(x0, y0);
        this.r = radius;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getRadius() {
        return r;
    }

}
