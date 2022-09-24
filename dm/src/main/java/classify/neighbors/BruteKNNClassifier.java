package main.java.classify.neighbors;

import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.Instance;
import main.java.core.exception.EstimatorNotFittedException;
import main.java.metrics.distance.DistanceMetric;
import main.java.metrics.distance.SEuclideanDistance;

import java.util.*;

/**
 * KNN classifier using a brute-force search to compute the nearest neighbors.
 *
 * @author Cloudy1225
 */
public class BruteKNNClassifier extends KNeighborsClassifier{

    private DataSet training;

    /**
     * Constructs with default parameters:
     * {@code k = 5; weights = DISTANCE},
     * {@code SEuclideanDistance} is the metric to use for distance computation.
     */
    public BruteKNNClassifier() {
        super();
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
    public BruteKNNClassifier(int k, String weights) {
        super(k, weights);
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
    public BruteKNNClassifier(int k, String weights, DistanceMetric metric) {
        super(k, weights, metric);
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
    public Map<Instance, Double> kNeighbors(Instance instance, int k) {
        if (this.training == null) {
            throw new EstimatorNotFittedException("BruteKNNClassifier is not fitted yet.");
        }
        PriorityQueue<InDistance> maxHeap = new PriorityQueue<>(k);
        for (Instance neighbor: this.training) {
            double distance = metric.measure(instance, neighbor);
            if (maxHeap.size() < k) {
                maxHeap.add(new InDistance(neighbor, distance));
            } else {
                double maxDistance = maxHeap.peek().distance;
                if (maxDistance > distance) {
                    maxHeap.poll();
                    maxHeap.offer(new InDistance(neighbor, distance));
                }
            }
        }
        InDistance[] neighbors = new InDistance[maxHeap.size()];
        maxHeap.toArray(neighbors);
        Arrays.sort(neighbors, new Comparator<InDistance>() {
            @Override
            public int compare(InDistance o1, InDistance o2) {
                return Double.compare(o1.distance, o2.distance);
            }
        });
        LinkedHashMap<Instance, Double> kNeighbors = new LinkedHashMap<>(neighbors.length);
        for (InDistance neighbor: neighbors) {
            kNeighbors.put(neighbor.instance, neighbor.distance);
        }
        return kNeighbors;
    }
}
