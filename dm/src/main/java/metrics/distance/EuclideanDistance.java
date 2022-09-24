package main.java.metrics.distance;

import main.java.core.Instance;
import main.java.core.exception.DimensionNotMatchedException;

/**
 * Euclidean Distance: D(x, y) = \sqrt{ \sum_i (x_i - y_i) ^ 2 }
 *
 * @author Cloudy1225
 */
public class EuclideanDistance implements DistanceMetric{
    @Override
    public double measure(Instance x, Instance y) {
        int nAttributes = x.dimensionality();
        if (nAttributes != y.dimensionality()) {
            throw new DimensionNotMatchedException("Both instances should contain the same number of attributes.");
        }
        double sum = 0;
        for (int i = 0; i < nAttributes; i++) {
            double d_i = x.attribute(i) - y.attribute(i);
            sum += d_i * d_i;
        }
        return Math.sqrt(sum);
    }
}
