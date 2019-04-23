package ru.bmstu.iu9.mathmod.lab2.delaunay;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import ru.bmstu.iu9.mathmod.lab2.geom.Circle;
import ru.bmstu.iu9.mathmod.lab2.geom.Point2D;
import ru.bmstu.iu9.mathmod.lab2.geom.Triangle;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;
import static org.apache.commons.math3.linear.MatrixUtils.createRealMatrix;

public class DelaunayUtil {

    private static final int A_IDX = 0, B_IDX = 1, C_IDX = 2, D_IDX = 3;
    private static Map<Triangle, double[]> triangleParamsCache = new HashMap<>();

    private DelaunayUtil() {
    }

    public static boolean checkDelaunayCondition(Triangle triangle, Point2D point) {
        double x0, y0, a, b, c, d;
        double[] trParams = getTriangleParams(triangle);
        x0 = point.x();
        y0 = point.y();

        a = trParams[A_IDX];
        b = trParams[B_IDX];
        c = trParams[C_IDX];
        d = trParams[D_IDX];

        return (a * (pow(x0, 2.0) + pow(y0, 20)) - b * x0 + c * y0 - d) * signum(a) >= 0;
    }

    public static Circle getCircumcircleOfTriangle(Triangle tr) {
        double r, x0, y0;
        double[] ps = getTriangleParams(tr);
        double x1 = tr.p1().x(), y1 = tr.p1().y();
        double d = 2 * ps[A_IDX];

        x0 = ps[B_IDX] / d;
        y0 = -ps[C_IDX] / d;
        r = sqrt(pow(x1 - x0, 2.0) + pow(y1 - y0, 2.0));

        return new Circle(r, x0, y0);
    }

    private static double[] getTriangleParams(Triangle tr) {
        if (!triangleParamsCache.containsKey(tr)) {
            triangleParamsCache.put(tr, calcTriangleParams(tr));
        }

        return triangleParamsCache.get(tr);
    }

    @SuppressWarnings("Duplicates")
    private static double[] calcTriangleParams(Triangle triangle) {
        double x1, y1, x2, y2, x3, y3,
                squaresSum1, squaresSum2, squaresSum3;
        double[] paramsData = new double[4];
        RealMatrix matrixA, matrixB, matrixC, matrixD;
        x1 = triangle.p1().x();
        y1 = triangle.p1().y();
        x2 = triangle.p2().x();
        y2 = triangle.p2().y();
        x3 = triangle.p3().x();
        y3 = triangle.p3().y();
        squaresSum1 = pow(x1, 2.0) + pow(y1, 2.0);
        squaresSum2 = pow(x2, 2.0) + pow(y2, 2.0);
        squaresSum3 = pow(x3, 2.0) + pow(y3, 2.0);


        matrixA = createRealMatrix(new double[][]{
                {x1, y1, 1},
                {x2, y2, 1},
                {x3, y3, 1}
        });
        matrixB = createRealMatrix(new double[][]{
                {squaresSum1, y1, 1},
                {squaresSum2, y2, 1},
                {squaresSum3, y3, 1}

        });
        matrixC = createRealMatrix(new double[][]{
                {squaresSum1, x1, 1},
                {squaresSum2, x2, 1},
                {squaresSum3, x3, 1}

        });
        matrixD = createRealMatrix(new double[][]{
                {squaresSum1, x1, y1},
                {squaresSum2, x2, y2},
                {squaresSum3, x3, y3}
        });

        paramsData[A_IDX] = new LUDecomposition(matrixA).getDeterminant();
        paramsData[B_IDX] = new LUDecomposition(matrixB).getDeterminant();
        paramsData[C_IDX] = new LUDecomposition(matrixC).getDeterminant();
        paramsData[D_IDX] = new LUDecomposition(matrixD).getDeterminant();

        return paramsData;
    }

}
