package main.java.metrics.distance;

import main.java.core.Instance;
import main.java.core.exception.DimensionNotMatchedException;

/**
 * Standardized Euclidean Distance metric: D(x, y) = \sqrt{ \sum_i \frac{ (x_i - y_i) ^ 2}{V_i} }
 *
 * @author Cloudy1225
 */
public class SEuclideanDistance implements DistanceMetric{

    /**
     * Variance of each feature.
     */
    private double[] variance;

    /**
     * Constructs {@code SEuclideanDistance} with given variances.
     *
     * @param V variance of each feature.
     */
    public SEuclideanDistance(double[] V) {
        this.variance = V;
    }

    @Override
    public double measure(Instance x, Instance y) {
        int nAttributes = x.dimensionality();
        if (nAttributes != y.dimensionality()) {
            throw new DimensionNotMatchedException("Both instances should contain the same number of attributes.");
        }
        if (nAttributes != this.variance.length) {
            throw new DimensionNotMatchedException("Size of V does not match.");
        }
        double sum = 0;
        for (int i = 0; i < nAttributes; i++) {
            double d_i = x.attribute(i) - y.attribute(i);
            sum += d_i * d_i / variance[i];
        }
        return Math.sqrt(sum);
    }
}
