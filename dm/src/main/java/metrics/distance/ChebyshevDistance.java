package main.java.metrics.distance;

import main.java.core.Instance;
import main.java.core.exception.DimensionNotMatchedException;

/**
 * Chebyshev/Infinity Distance: D(x, y) = max_i (|x_i - y_i|)
 *
 * @author Cloudy1225
 */
public class ChebyshevDistance implements DistanceMetric{
    @Override
    public double measure(Instance x, Instance y) {
        int nAttributes = x.dimensionality();
        if (nAttributes != y.dimensionality()) {
            throw new DimensionNotMatchedException("Both instances should contain the same number of attributes.");
        }
        double distance = 0;
        for (int i = 0; i < nAttributes; i++) {
            double d_i = x.attribute(i) - y.attribute(i);
            if (d_i < 0) {
                d_i = -d_i;
            }
            if (d_i > distance) {
                distance = d_i;
            }
        }
        return distance;
    }
}
