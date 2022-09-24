package main.java.metrics.distance;

import main.java.core.Instance;
import main.java.core.exception.DimensionNotMatchedException;

/**
 * This class provides a uniform interface to fast distance metric functions.
 *
 * @author Cloudy1225
 */
public interface DistanceMetric {

    /**
     * Calculates the distance between two instances.
     *
     * @param x the first instance
     * @param y the second instance
     * @return the distance between the two instances
     * @throws DimensionNotMatchedException Both instances should contain the same number of attributes.
     */
    double measure(Instance x, Instance y);
}
