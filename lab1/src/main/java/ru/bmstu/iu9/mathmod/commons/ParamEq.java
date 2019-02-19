package ru.bmstu.iu9.mathmod.commons;

import org.apache.commons.math3.linear.RealVector;

import java.util.function.BiFunction;

/**
 * Represents parametric function multi-dimensional parametric equation of type z = f(t, y)
 * first param - t (in R)
 * second param - y (in R^n)
 * return type - result vector f(t, y)
 *
 * */
public interface ParamEq extends BiFunction<Double, RealVector, RealVector> { }