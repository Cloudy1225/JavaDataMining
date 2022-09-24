package main.java.classify.neighbors;

import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.Instance;
import main.java.core.exception.EstimatorNotFittedException;
import main.java.metrics.distance.DistanceMetric;
import main.java.metrics.distance.SEuclideanDistance;

import java.util.Map;

/**
 * KNN classifier using k-d-tree-search to compute the nearest neighbors.
 *
 * @author Cloudy1225
 * @see KDTree
 */
public class KDTreeKNNClassifier extends KNeighborsClassifier {

    private KDTree tree;

    /**
     * Constructs with default parameters:
     * {@code k = 5; weights = DISTANCE},
     * {@code SEuclideanDistance} is the metric to use for distance computation.
     */
    public KDTreeKNNClassifier() {
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
    public KDTreeKNNClassifier(int k, String weights) {
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
    public KDTreeKNNClassifier(int k, String weights, DistanceMetric metric) {
        super(k, weights, metric);
    }

    @Override
    public void fit(DataSet dataset) {
        this.classSet = dataset.classSet();
        if (this.metric == null) {
            double[] var= DataSets.var(dataset);
            this.metric = new SEuclideanDistance(var);
        }
        this.tree = new KDTree();
        tree.buildTree(dataset);
    }

    @Override
    public Map<Instance, Double> kNeighbors(Instance instance, int k) {
        if (this.tree == null) {
            throw new EstimatorNotFittedException("KDTreeKNNClassifier is not fitted yet.");
        }
        return this.tree.query(instance, k, this.metric);
    }
}
