package ru.bmstu.iu9.mathmod.lab2.elevation;

import org.apache.commons.math3.linear.ArrayRealVector;
import ru.bmstu.iu9.mathmod.lab2.geom.Vector2D;

public class ElevationPoint extends ArrayRealVector {

    private static final int X_IDX = 0;
    private static final int Y_IDX = 1;
    private static final int H_IDX = 2;

    public ElevationPoint(Vector2D pt, double h) {
        this(pt.x(), pt.y(), h);
    }

    public ElevationPoint(double x, double y, double h) {
        super(3);
        this.setEntry(X_IDX, x);
        this.setEntry(Y_IDX, y);
        this.setEntry(H_IDX, h);
    }

    public double x() {
        return this.getEntry(X_IDX);
    }

    public double y() {
        return this.getEntry(Y_IDX);
    }

    public double h() {
        return this.getEntry(H_IDX);
    }

    public Vector2D xyProjection() {
        return new Vector2D(x(), y());
    }
}
