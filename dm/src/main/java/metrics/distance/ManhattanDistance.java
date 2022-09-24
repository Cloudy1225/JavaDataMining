package main.java.metrics.distance;

import main.java.core.Instance;
import main.java.core.exception.DimensionNotMatchedException;

/**
 * Manhattan/City-block Distance: D(x, y) = \sum_i |x_i - y_i|
 *
 * @author Cloudy1225
 */
public class ManhattanDistance implements DistanceMetric{
    @Override
    public double measure(Instance x, Instance y) {
        int nAttributes = x.dimensionality();
        if (nAttributes != y.dimensionality()) {
            throw new DimensionNotMatchedException("Both instances should contain the same number of attributes.");
        }
        double distance = 0;
        for (int i = 0; i < nAttributes; i++) {
            double d_i = x.attribute(i) - y.attribute(i);
            distance += (d_i < 0 ? -d_i : d_i);
        }
        return distance;
    }
}
