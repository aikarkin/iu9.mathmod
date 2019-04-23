package ru.bmstu.iu9.mathmod.commons;

import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;

public class RungeKuttaAlgo {
    public static List<RealVector> rungeKutta(ParamEq f, RungeKuttaPredicate exitPredicate, RealVector y0, double h) {
        RealVector k1, k2, k3, k4;
        ArrayList<RealVector> res = new ArrayList<>();
        double t;
        res.add(y0);


        int i = 0;

        do {
            i++;
            t = (i - 1) * h;
            RealVector prevRes = res.get(i - 1);

            k1 = f.apply(t, prevRes)
                    .mapMultiply(h);
            k2 = f.apply(t + 0.5 * h, prevRes.add(k1.mapMultiply(0.5)))
                    .mapMultiply(h);
            k3 = f.apply(t + 0.5 * h, prevRes.add(k2.mapMultiply(0.5)))
                    .mapMultiply(h);
            k4 = f.apply(t + h, prevRes.add(k3))
                    .mapMultiply(h);

            res.add(prevRes
                    .add((k1
                            .add(k2.mapMultiply(2.0))
                            .add(k3.mapMultiply(2.0))
                            .add(k4))
                            .mapMultiply(1.0 / 6.0)
                    )
            );
        } while (!exitPredicate.apply(t, res.get(i - 1), res.get(i)));

//        res.remove(i);

        return res;
    }
}
