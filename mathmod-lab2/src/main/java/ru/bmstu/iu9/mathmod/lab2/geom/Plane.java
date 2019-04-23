package ru.bmstu.iu9.mathmod.lab2.geom;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import ru.bmstu.iu9.mathmod.lab2.elevation.ElevationPoint;

import java.util.Arrays;
import java.util.Objects;

public class Plane {

    private ElevationPoint pt1;
    private ElevationPoint pt2;
    private ElevationPoint pt3;

    private ElevationPoint[] ptsSorted;

    public Plane(ElevationPoint point1, ElevationPoint point2, ElevationPoint point3) {
        this.pt1 = point1;
        this.pt2 = point2;
        this.pt3 = point3;

        sortPoints(pt1, pt2, pt3);
    }

    private void sortPoints(ElevationPoint ...pts) {
        ptsSorted = Arrays.copyOf(pts, pts.length);

        Arrays.sort(
                pts,
                (pt1, pt2) -> {
                    if(pt1.x() < pt2.x())
                        return 1;
                    else if(pt1.x() > pt2.x())
                        return -1;

                    if(pt1.y() < pt2.y())
                        return 1;
                    else if(pt1.y() > pt2.y())
                        return -1;

                    if(pt1.h() < pt2.h())
                        return 1;
                    else if (pt1.h() > pt2.h())
                        return -1;

                    return 0;
                }
        );
    }

    public double detA() {
        return det(new double[][]{
                {pt1.y(), pt1.h(), 1},
                {pt2.y(), pt2.h(), 1},
                {pt3.y(), pt3.h(), 1}
        });
    }

    public double detB() {
        return det(new double[][]{
                {pt1.x(), pt1.h(), 1},
                {pt2.x(), pt2.h(), 1},
                {pt3.x(), pt3.h(), 1}
        });
    }

    public double detC() {
        return det(new double[][]{
                {pt1.x(), pt1.y(), 1},
                {pt2.x(), pt2.y(), 1},
                {pt3.x(), pt3.y(), 1}
        });
    }

    public double detD() {
        return det(new double[][]{
                {pt1.x(), pt1.y(), pt1.h()},
                {pt2.x(), pt2.y(), pt2.h()},
                {pt3.x(), pt3.y(), pt3.h()}
        });
    }

    private static double det(double[][] matRaw) {
        return new LUDecomposition(MatrixUtils.createRealMatrix(matRaw)).getDeterminant();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plane plane = (Plane) o;
        return Objects.equals(ptsSorted[0], plane.ptsSorted[0]) &&
                Objects.equals(ptsSorted[1], plane.ptsSorted[1]) &&
                Objects.equals(ptsSorted[2], plane.ptsSorted[2]);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ptsSorted[0], ptsSorted[1], ptsSorted[2]);
    }

}
