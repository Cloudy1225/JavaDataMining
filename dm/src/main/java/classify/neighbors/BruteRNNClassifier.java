package main.java.classify.neighbors;

import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.Instance;
import main.java.core.exception.EstimatorNotFittedException;
import main.java.metrics.distance.DistanceMetric;
import main.java.metrics.distance.SEuclideanDistance;

import java.util.*;

/**
 * Radius-NN classifier using a brute-force search to compute the nearest neighbors.
 *
 * @author Cloudy1225
 */
public class BruteRNNClassifier extends RadiusNeighborsClassifier{

    private DataSet training;

    /**
     * Constructs with default parameters:
     * {@code radius = 1.0; weights = DISTANCE},
     * {@code SEuclideanDistance} is the metric to use for distance computation.
     */
    public BruteRNNClassifier() {
        super();
    }

    /**
     * Constructs with given parameters.
     * {@code SEuclideanDistance} is the metric to use for distance computation.
     *
     * @param radius range of parameter space to use by default for radius_neighbors queries.
     * @param weights weight function used in prediction. Possible values:
     *                "uniform": All points in each neighborhood are weighted equally.
     *                "distance": Weight points by the inverse of their distance.
     *                "gaussian": Weight points by standard normal distribution.
     *                "custom": weight points by the weight of each instance.
     */
    public BruteRNNClassifier(double radius, String weights) {
        super(radius, weights);
    }

    /**
     * Constructs with given parameters.
     *
     * @param radius range of parameter space to use by default for radius_neighbors queries.
     * @param weights weight function used in prediction. Possible values:
     *                "uniform": All points in each neighborhood are weighted equally.
     *                "distance": Weight points by the inverse of their distance.
     *                "gaussian": Weight points by standard normal distribution.
     *                "custom": weight points by the weight of each instance.
     * @param metric metric to use for distance computation
     */
    public BruteRNNClassifier(double radius, String weights, DistanceMetric metric) {
        super(radius, weights, metric);
    }


    @Override
    public void fit(DataSet dataset) {
        this.training = dataset;
        this.classSet = dataset.classSet();
        if (this.metric == null) {
            double[] var= DataSets.var(this.training);
            this.metric = new SEuclideanDistance(var);
        }
    }

    @Override
    public Map<Instance, Double> radiusNeighbors(Instance instance, double radius) {
        if (this.training == null) {
            throw new EstimatorNotFittedException("BruteKNNClassifier is not fitted yet.");
        }
        ArrayList<InDistance> neighborBall = new ArrayList<>();
        for (Instance neighbor: this.training) {
            double distance = metric.measure(instance, neighbor);
            if (distance <= radius) {
                neighborBall.add(new InDistance(neighbor, distance));
            }
        }
        neighborBall.sort(new Comparator<InDistance>() {
            @Override
            public int compare(InDistance o1, InDistance o2) {
                return Double.compare(o1.distance, o2.distance);
            }
        });
        LinkedHashMap<Instance, Double> kNeighbors = new LinkedHashMap<>(neighborBall.size());
        for (InDistance neighbor: neighborBall) {
            kNeighbors.put(neighbor.instance, neighbor.distance);
        }
        return kNeighbors;
    }
}
