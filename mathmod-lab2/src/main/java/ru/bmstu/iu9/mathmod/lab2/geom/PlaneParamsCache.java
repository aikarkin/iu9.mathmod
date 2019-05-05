package ru.bmstu.iu9.mathmod.lab2.geom;

import java.util.HashMap;
import java.util.Map;

public class PlaneParamsCache {

    private Map<Plane, PlaneCoefficients> coefficientsMap = new HashMap<>();

    public PlaneParamsCache() {
    }

    public PlaneCoefficients get(Plane plane) {
        if(!coefficientsMap.containsKey(plane)) {
            PlaneCoefficients coefficients = new PlaneCoefficients(
                    plane.detA(),
                    plane.detB(),
                    plane.detC(),
                    plane.detD()
            );
            coefficientsMap.put(plane, coefficients);
        }

        return coefficientsMap.get(plane);
    }

    public void clear() {
        coefficientsMap.clear();
    }

    public static class PlaneCoefficients {
        private double detA;
        private double detB;
        private double detC;
        private double detD;

        PlaneCoefficients(double detA, double detB, double detC, double detD) {
            this.detA = detA;
            this.detB = detB;
            this.detC = detC;
            this.detD = detD;
        }

        public double getDetA() {
            return detA;
        }

        public double getDetB() {
            return detB;
        }

        public double getDetC() {
            return detC;
        }

        public double getDetD() {
            return detD;
        }

        public double distanceToPoint(Vector2D pt) {
            return (detD + detB * pt.y() - detA * pt.x()) / detC;
        }
    }
}
