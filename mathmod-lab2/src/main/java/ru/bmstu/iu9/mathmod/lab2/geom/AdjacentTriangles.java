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

    public Vector2D[] pointsSorted() {
        Edge splitEdge = commonEdge;
        Vector2D pt1 = commonEdge.first();
        Vector2D pt2 = lhsTriangle.getOppositePoint(commonEdge);
        Vector2D pt3 = commonEdge.second();
        Vector2D pt4 = rhsTriangle.getOppositePoint(commonEdge);

        if(GeometryUtils.pseudoScalar(splitEdge.toVector(), new Vector2D(pt2.subtract(pt1))) > 0) {
            pt1 = commonEdge.second();
            pt3 = commonEdge.first();
        }

        return new Vector2D[] {pt1, pt2, pt3, pt4};
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

    public boolean contains(Triangle tr) {
        return (lhsTriangle != null && lhsTriangle.equals(tr) ) || (rhsTriangle != null && rhsTriangle.equals(tr));
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

    @Override
    public String toString() {
        return "{" +
                "lhsTr=" + lhsTriangle +
                ", rhsTr=" + rhsTriangle +
                '}';
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
