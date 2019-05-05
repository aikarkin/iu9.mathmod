package ru.bmstu.iu9.mathmod.lab2.geom;

import java.util.Objects;

public class AdjacentTriangles {

    private Triangle lhsTriangle;
    private Triangle rhsTriangle;
    private Edge commonEdge;

    public AdjacentTriangles(Triangle tr, Edge outerEdge) {
        this.lhsTriangle = tr;
        this.rhsTriangle = null;
        this.commonEdge = outerEdge;
    }

    public AdjacentTriangles(Triangle tr1, Triangle tr2) {
        if(tr1 == null || tr2 == null) {
            throw new IllegalArgumentException("Triangle cannot be null");
        }

        this.lhsTriangle = tr1;
        this.rhsTriangle = tr2;
        this.commonEdge = calcCommonEdge(tr1, tr2);
    }

    public Triangle getLhsTriangle() {
        return lhsTriangle;
    }

    public Triangle getRhsTriangle() {
        return rhsTriangle;
    }

    public Edge getCommonEdge() {
        return commonEdge;
    }

    public Vector2D[] points() {
        return new Vector2D[] {
                lhsTriangle.p1(),
                lhsTriangle.p2(),
                lhsTriangle.p3(),
                rhsTriangle.getOppositePoint(commonEdge)
        };
    }

    public Triangle adjacentTriangle(Triangle tr) {
        if(tr.equals(lhsTriangle)) {
            return rhsTriangle;
        }

        if(tr.equals(rhsTriangle)) {
            return lhsTriangle;
        }

        throw new IllegalArgumentException("Not adjacent triangles");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdjacentTriangles that = (AdjacentTriangles) o;
        return Objects.equals(lhsTriangle, that.lhsTriangle) &&
                Objects.equals(rhsTriangle, that.rhsTriangle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhsTriangle, rhsTriangle);
    }

    private static Edge calcCommonEdge(Triangle tr1, Triangle tr2) {
        for (Edge e1 : tr1.edges()) {
            for (Edge e2 : tr2.edges()) {
                if (e1.equals(e2)) {
                    return e1;
                }
            }
        }

        return null;
    }

}
