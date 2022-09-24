package main.java.classify.neighbors;

import main.java.core.Instance;

/**
 * This is just a wrapper of instance and distance for querying the k nearest neighbors.
 * This is just used in {@code PriorityQueue<InDistance>},
 * where the greater the distance, the lower the priority.
 *
 * @author Cloudy1225
 */
public class InDistance implements Comparable<InDistance>{
    /**
     * Instance as a candidate of k nearest neighbors.
     */
    public final Instance instance;

    /**
     * Distance between this and instance to query.
     */
    public final double distance;

    InDistance(Instance instance, double distance) {
        this.instance = instance;
        this.distance = distance;
    }

    /**
     * The greater the distance, the lower the priority.
     *
     * @return {@code Double.compare(that.distance, this.distance)}
     */
    @Override
    public int compareTo(InDistance that) {
        return Double.compare(that.distance, this.distance);
    }
}