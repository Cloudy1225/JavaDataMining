package main.java.classify.neighbors;

import main.java.classify.Classifier;
import main.java.core.Instance;
import main.java.core.WeightHandler;
import main.java.metrics.distance.DistanceMetric;
import main.java.metrics.distance.SEuclideanDistance;

import java.util.*;

/**
 * Abstract base class for classifiers implementing the k-nearest neighbors vote.
 *
 * @author Cloudy1225
 * @see Classifier
 */
public abstract class KNeighborsClassifier implements Classifier, WeightHandler {

    /**
     * Uniform weights. All points in each neighborhood are weighted equally.
     */
    public static final String UNIFORM = "uniform";

    /**
     * Weight points by the inverse of their distance.
     * In this case, closer neighbors of a query point will have a greater influence
     * than neighbors which are further away.
     */
    public static final String DISTANCE = "distance";

    /**
     * Weight points by standard normal distribution.
     * In this case, when distance is 0, weight will be 1.
     * And closer neighbors of a query point will have a greater influence
     * than neighbors which are further away.
     */
    public static final String GAUSSIAN = "gaussian";

    /**
     * Weight points by the weight of each instance.
     */
    public static final String CUSTOM = "custom";

    /**
     * Number of neighbors to use by default for kNeighbors queries.
     */
    protected int k;

    /**
     * Weight function used in prediction. Possible values:
     * <p>
     *     "uniform" : uniform weights. All points in each neighborhood are weighted equally.
     * </p>
     * <p>
     *     "distance" : weight points by the inverse of their distance.
     *     In this case, closer neighbors of a query point will have a greater influence
     *     than neighbors which are further away.
     * </p>
     * <p>
     *     "gaussian": weight points by standard normal distribution.
     *     In this case, when distance is 0, weight will be 1.
     *     And closer neighbors of a query point will have a greater influence
     *     than neighbors which are further away.
     * </p>
     * <p>
     *     "custom": weight points by the weight of each instance.
     * </p>
     */
    protected String weights;

    /**
     * Metric to use for distance computation.
     * Default is “minkowski”({@link SEuclideanDistance}), which results in the standard Euclidean distance when p = 2.
     */
    protected DistanceMetric metric;

    /**
     * Class labels known to the classifier.
     */
    protected SortedSet<Double> classSet;

    /**
     * Constructs with default parameters:
     * {@code k = 5; weights = DISTANCE},
     * {@code SEuclideanDistance} is the metric to use for distance computation.
     */
    public KNeighborsClassifier() {
        this.k = 5;
        this.weights = DISTANCE;
    }

    /**
     * Constructs with given parameters.
     * {@code SEuclideanDistance} is the metric to use for distance computation.
     *
     * @param k number of neighbors to use by default for kNeighbors queries.
     * @param weights weight function used in prediction. Possible values:
     *                "uniform": All points in each neighborhood are weighted equally.
     *                "distance": Weight points by the inverse of their distance.
     *                "gaussian": Weight points by standard normal distribution.
     *                "custom": weight points by the weight of each instance.
     */
    public KNeighborsClassifier(int k, String weights) {
        this.k = k;
        this.weights = weights;
    }

    /**
     * Constructs with given parameters.
     *
     * @param k number of neighbors to use by default for kNeighbors queries.
     * @param weights weight function used in prediction. Possible values:
     *                "uniform": All points in each neighborhood are weighted equally.
     *                "distance": Weight points by the inverse of their distance.
     *                "gaussian": Weight points by standard normal distribution.
     *                "custom": weight points by the weight of each instance.
     * @param metric metric to use for distance computation
     */
    public KNeighborsClassifier(int k, String weights, DistanceMetric metric) {
        this.k = k;
        this.weights = weights;
        this.metric = metric;
    }


    @Override
    public double classify(Instance instance) {
        return this.predict(instance);
    }

    /**
     * Predicts the class value for given instance.
     *
     * @param instance the specified instance
     * @return class value for given instance
     */
    public double predict(Instance instance) {
        Map<Instance, Double> neighbors = this.kNeighbors(instance, this.k);
        Map<Double, Double> classDistribution = new HashMap<>();
        for (Double clazz: this.classSet) {
            classDistribution.put(clazz, 0.0);
        }
        switch (this.weights) {
            case UNIFORM: {
                for (Instance neighbor: neighbors.keySet()) {
                    double clazz = neighbor.classValue();
                    double count = classDistribution.get(clazz) + 1;
                    classDistribution.put(clazz, count);
                }
                break;
            }
            case GAUSSIAN: {
                double coefficient = 1 / Math.sqrt(Math.PI + Math.PI);
                for (Map.Entry<Instance, Double> entry: neighbors.entrySet()) {
                    double clazz = entry.getKey().classValue();
                    double distance = entry.getValue();
                    double count = classDistribution.get(clazz) +  coefficient * Math.pow(Math.E, -distance * distance / 2);
                    classDistribution.put(clazz, count);
                }
                break;
            }
            case CUSTOM: {
                for (Instance neighbor: neighbors.keySet()) {
                    double clazz = neighbor.classValue();
                    double count = classDistribution.get(clazz) + neighbor.getWeight();
                    classDistribution.put(clazz, count);
                }
                break;
            }
            default: { // DISTANCE
                for (Map.Entry<Instance, Double> entry: neighbors.entrySet()) {
                    double clazz = entry.getKey().classValue();
                    double distance = entry.getValue();
                    double count = classDistribution.get(clazz) + 1 / distance;
                    classDistribution.put(clazz, count);
                }
                break;
            }
        }
        Double res = Double.NaN;
        double maxCount = -1;
        for (Map.Entry<Double, Double> distribution: classDistribution.entrySet()) {
            double count = distribution.getValue();
            if (count > maxCount) {
                maxCount = count;
                res = distribution.getKey();
            }
        }
        return res;
    }

    /**
     * Finds the K-neighbors of given instance.
     *
     * @param instance given instance
     * @param k number of neighbors to find
     * @return the K-neighbors: instance and distance
     * @throws main.java.core.exception.EstimatorNotFittedException if this is not fitted yet
     */
    public abstract Map<Instance, Double> kNeighbors(Instance instance, int k);

}
