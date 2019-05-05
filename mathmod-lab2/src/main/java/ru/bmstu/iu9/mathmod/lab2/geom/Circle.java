package ru.bmstu.iu9.mathmod.lab2.geom;

public class Circle {
    private Vector2D center;
    double r;

    public Circle(double x0, double y0, double radius) {
        this(new Vector2D(x0, y0), radius);
    }

    public Circle(Vector2D center, double r) {
        this.center = center;
        this.r = r;
    }

    public Vector2D getCenter() {
        return center;
    }

    public double getRadius() {
        return r;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "center=" + center +
                ", r=" + r +
                '}';
    }

}
