package main.java.classify.decisionTree;

import main.java.classify.Classifier;
import main.java.core.DataSet;
import main.java.core.Instance;

import java.util.Map;

/**
 * This class encapsulates three implementations of {@link DecisionTree} to implement {@link Classifier}.
 *
 * @author Cloudy1225
 * @see DecisionTree
 * @see Classifier
 */
public class DecisionTreeClassifier implements Classifier {

    private final DecisionTree tree;

    /**
     * Constructs a decision tree classifier using Cart algorithm with default constraint conditions.
     */
    public DecisionTreeClassifier() {
        this.tree = new CartTree();
    }

    /**
     * Constructs a decision tree classifier using Cart algorithm with given constraint conditions.
     *
     * @param maxDepth the maximum depth of the tree
     * @param minSamplesSplit the minimum number of samples required to split an internal node
     * @param minSamplesLeaf the minimum number of samples required to be at a leaf node
     * @param minImpurityDecrease a node will be split if this split induces a decrease of the impurity greater than or equal to this value.
     * @param ccpAlpha Complexity parameter used for Minimal Cost-Complexity Pruning
     */
    public DecisionTreeClassifier(int maxDepth, int minSamplesSplit, int minSamplesLeaf, double minImpurityDecrease, double ccpAlpha) {
        this.tree = new CartTree(maxDepth, minSamplesSplit, minSamplesLeaf, minImpurityDecrease, ccpAlpha);
    }

    /**
     * Constructs a decision tree classifier with default constraint conditions.
     * The decision tree algorithm is up to given criterion.
     * <p>
     *     "infoGain": ID3;<br>
     *     "gainRatio": C4.5;<br>
     *     "gini": Cart
     * </p>
     *
     * @param criterion criterion to measure the quality of a split("infoGain","gainRatio","gini")
     * @throws IllegalArgumentException if criterion is not one of (infoGain, gainRatio, gini)
     */
    public DecisionTreeClassifier(String criterion) {
        switch (criterion) {
            case "gini":{
                this.tree = new CartTree();
                break;
            }
            case "gainRatio":{
                this.tree = new C45Tree();
                break;
            }
            case "infoGain":{
                this.tree = new ID3Tree();
                break;
            }
            default:{
                throw new IllegalArgumentException("criterion must be one of (infoGain, gainRatio, gini)");
            }
        }
    }

    /**
     * Constructs a decision tree classifier with given constraint conditions.
     * The decision tree algorithm is up to given criterion.
     * <p>
     *     "infoGain": ID3;<br>
     *     "gainRatio": C4.5;<br>
     *     "gini": Cart
     * </p>
     *
     * @param criterion criterion to measure the quality of a split("infoGain","gainRatio","gini")
     * @param maxDepth the maximum depth of the tree
     * @param minSamplesSplit the minimum number of samples required to split an internal node
     * @param minSamplesLeaf the minimum number of samples required to be at a leaf node
     * @param minImpurityDecrease a node will be split if this split induces a decrease of the impurity greater than or equal to this value.
     * @param ccpAlpha Complexity parameter used for Minimal Cost-Complexity Pruning
     * @throws IllegalArgumentException if criterion is not one of (infoGain, gainRatio, gini) or one of constraint conditions is invalid
     */
    public DecisionTreeClassifier(String criterion, int maxDepth, int minSamplesSplit, int minSamplesLeaf, double minImpurityDecrease, double ccpAlpha) {
        switch (criterion) {
            case "gini":{
                this.tree = new CartTree(maxDepth, minSamplesSplit, minSamplesLeaf, minImpurityDecrease, ccpAlpha);
                break;
            }
            case "gainRatio":{
                this.tree = new C45Tree(maxDepth, minSamplesSplit, minSamplesLeaf, minImpurityDecrease, ccpAlpha);
                break;
            }
            case "infoGain":{
                this.tree = new ID3Tree(maxDepth, minSamplesSplit, minSamplesLeaf, minImpurityDecrease, ccpAlpha);
                break;
            }
            default:{
                throw new IllegalArgumentException("criterion must be one of (infoGain, gainRatio, gini)");
            }
        }
    }

    @Override
    public void fit(DataSet dataset) {
        this.tree.fit(dataset);
    }

    @Override
    public void fit(DataSet dataset, Map<Double, Double> classWeight, double[] sampleWeight) {
        this.tree.fit(dataset, classWeight, sampleWeight);
    }

    @Override
    public double classify(Instance instance) {
        return this.tree.predict(instance);
    }
}
