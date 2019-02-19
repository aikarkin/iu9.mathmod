package ru.bmstu.iu9.mathmod.commons;

import org.apache.commons.math3.linear.RealVector;

@FunctionalInterface
public interface RungeKuttaPredicate {
    boolean apply(double time, RealVector prevVec, RealVector curVec);
}
