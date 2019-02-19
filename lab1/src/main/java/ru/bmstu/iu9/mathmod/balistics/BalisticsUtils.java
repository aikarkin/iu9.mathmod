package ru.bmstu.iu9.mathmod.balistics;

import static java.lang.Math.*;

public class BalisticsUtils {
    public static final class Galilei {
        public static double flightDistance(double startSpeed, double startAngle, double gravity) {
            return tan(startAngle) * (2 * pow(startSpeed * cos(startAngle), 2.0)) / gravity;
        }

        public static double flightTime(double startSpeed, double startAngle, double gravity) {
            return tan(startAngle) * (2 * startSpeed * cos(startAngle)) / gravity;
        }
    }

    public static double flightDistance(double startSpeed, double startAngle, double flightTime) {
        return startSpeed * cos(startAngle) * flightTime;
    }
}
