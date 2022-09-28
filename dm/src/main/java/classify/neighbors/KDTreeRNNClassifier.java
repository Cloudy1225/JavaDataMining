package main.java.classify.neighbors;

import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.Instance;
import main.java.core.exception.EstimatorNotFittedException;
import main.java.metrics.distance.DistanceMetric;
import main.java.metrics.distance.SEuclideanDistance;

import java.util.Map;

/**
 * Radius-NN classifier using k-d-tree-search to compute the nearest neighbors.
 *
 * @author Cloudy1225
 * @see KDTree
 */
public class KDTreeRNNClassifier extends RadiusNeighborsClassifier {

    private KDTree tree;

    /**
     * Constructs with default parameters:
     * {@code radius = 1.0; weights = DISTANCE},
     * {@code SEuclideanDistance} is the metric to use for distance computation.
     */
    public KDTreeRNNClassifier() {
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
    public KDTreeRNNClassifier(double radius, String weights) {
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
    public KDTreeRNNClassifier(double radius, String weights, DistanceMetric metric) {
        super(radius, weights, metric);
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
    public Map<Instance, Double> radiusNeighbors(Instance instance, double radius) {
        if (this.tree == null) {
            throw new EstimatorNotFittedException("KDTreeKNNClassifier is not fitted yet.");
        }
        return this.tree.queryRadius(instance, radius, this.metric);
    }
}

