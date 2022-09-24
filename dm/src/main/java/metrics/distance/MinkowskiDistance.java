package main.java.metrics.distance;

import main.java.core.Instance;
import main.java.core.exception.DimensionNotMatchedException;

/**
 * Minkowski Distance: D(x, y) = [\sum_i w_i *|x_i - y_i|^p] ^ (1/p)
 *
 * @author Cloudy1225
 */
public class MinkowskiDistance implements DistanceMetric{

    /**
     * The order of the p-norm of the difference.
     */
    private double p;

    /**
     * Constructs {@code MinkowskiDistance} with default p=2.
     */
    public MinkowskiDistance() {
        this.p = 2;
    }

    /**
     * Constructs {@code MinkowskiDistance} with given p.
     *
     * @param p the order of the p-norm of the difference
     */
    public MinkowskiDistance(double p) {
        this.p = p;
    }

    @Override
    public double measure(Instance x, Instance y) {
        int nAttributes = x.dimensionality();
        if (nAttributes != y.dimensionality()) {
            throw new DimensionNotMatchedException("Both instances should contain the same number of attributes.");
        }
        double sum = 0;
        for (int i = 0; i < nAttributes; i++) {
            sum += Math.pow(Math.abs(x.attribute(i) - y.attribute(i)), p);
        }
        return Math.pow(sum, 1 / p);
    }
}
