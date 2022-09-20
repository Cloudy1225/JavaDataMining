package main.java.classify.decisionTree;

import main.java.classify.evaluation.Evaluatable;
import main.java.classify.evaluation.PerformanceMeasure;
import main.java.core.*;
import main.java.core.exception.EstimatorNotFittedException;
import main.java.preprocessing.weight.ClassWeightUtil;

import java.util.*;

/**
 * This is an abstract base class for implementing decision tree.
 * Different subclasses should implement different algorithms to build a decision tree.
 *
 * @author Cloudy1225
 * @see Evaluatable
 * @see DecisionTreeClassifier
 */
public abstract class DecisionTree implements Evaluatable, WeightHandler {

    /**
     * Constructs a decision tree classifier with default constraint conditions.
     */
    public DecisionTree() {
        this.maxDepth = Integer.MAX_VALUE;
        this.minSamplesSplit = 2;
        this.minSamplesLeaf = 1;
        this.minImpurityDecrease = 0.0;
        this.ccpAlpha = 0;
    }

    /**
     * Constructs a decision tree with given constraint conditions.
     *
     * @param maxDepth the maximum depth of the tree
     * @param minSamplesSplit the minimum number of samples required to split an internal node
     * @param minSamplesLeaf the minimum number of samples required to be at a leaf node
     * @param minImpurityDecrease a node will be split if this split induces a decrease of the impurity greater than or equal to this value.
     * @param ccpAlpha Complexity parameter used for Minimal Cost-Complexity Pruning
     */
    public DecisionTree(int maxDepth, int minSamplesSplit, int minSamplesLeaf, double minImpurityDecrease, double ccpAlpha) {
        this.maxDepth = maxDepth;
        this.minSamplesSplit = minSamplesSplit;
        this.minSamplesLeaf = minSamplesLeaf;
        this.minImpurityDecrease = minImpurityDecrease;
        this.ccpAlpha = ccpAlpha;
    }


    /**
     * The maximum depth of the tree.
     * If not set, then nodes are expanded until all leaves are pure
     * or until all leaves contain less than min_samples_split samples.
     */
    protected int maxDepth;

    /**
     * The minimum number of samples required to split an internal node.
     */
    protected int minSamplesSplit;

    /**
     * The minimum number of samples required to be at a leaf node.
     * A split point at any depth will only be considered
     * if it leaves at least <tt>minSamplesLeaf</tt> training samples in each of the left and right branches.
     * This may have the effect of smoothing the model, especially in regression.
     */
    protected int minSamplesLeaf;

    /**
     * A node will be split if this split induces a decrease
     * of the impurity greater than or equal to this value.
     *
     * The weighted impurity decrease equation is the following:
     * <p>
     *     N_t / N * (impurity - N_t_R / N_t * right_impurity
     *     - N_t_L / N_t * left_impurity)
     * </p>
     * <p>
     *      where N is the total number of samples, N_t is the number of samples at the current node,
     *      N_t_L is the number of samples in the left child,
     *      and N_t_R is the number of samples in the right child.
     *      N, N_t, N_t_R and N_t_L all refer to the weighted sum, if sample_weight is passed.
     * </p>
     */
    protected double minImpurityDecrease;

    /**
     * Complexity parameter used for Minimal Cost-Complexity Pruning.
     * The subtree with the largest cost complexity that is smaller than ccp_alpha will be chosen.
     * By default, no pruning is performed.
     */
    protected double ccpAlpha;



    /**
     * The total weighted number of given dataset.
     * It is used to compute the improvement in impurity when a split occurs.
     * It will be initialized only when preprocessing.
     */
    protected double weightedNSamples;

    /**
     * The root of tree fitted with given dataset.
     */
    protected DTNode root;

    private void check() {
        if (minSamplesLeaf < 1) {
            throw new IllegalArgumentException("minSamplesLeaf must be at least 1");
        }
        if (minSamplesSplit < 2) {
            throw new IllegalArgumentException("minSamplesSplit must be at least 2");
        }
        if (maxDepth <= 0) {
            throw new IllegalArgumentException("maxDepth must be greater than zero. ");
        }
        if (minImpurityDecrease < 0) {
            throw new IllegalArgumentException("minImpurityDecrease must be greater than or equal to 0");
        }
        if (ccpAlpha < 0.0) {
            throw new IllegalArgumentException("ccpAlpha must be greater than or equal to 0");
        }
        if (this.minSamplesSplit < minSamplesLeaf * 2) {
            this.minSamplesSplit = minSamplesLeaf * 2;
        }
    }

    /**
     * Builds a decision tree from the training set.
     *
     * @param dataset training set
     */
    public void fit(DataSet dataset) {
        this.preprocess(dataset, null, null); // 所有instance的权重将被设置为1
        this.root = this.buildTree(dataset, 1);
        this.prune();
    }

    /**
     * Builds a decision tree from the training set with given classWeight and sampleWeight.
     *
     * @param dataset training set
     * @param classWeight class weights
     * @param sampleWeight sample weights
     * @deprecated It is advised that set weights use {@link ClassWeightUtil#setWeight(DataSet, Map, double[])} before this.
     */
    public void fit(DataSet dataset, Map<Double, Double> classWeight, double[] sampleWeight) {
        this.preprocess(dataset, classWeight, sampleWeight); // instanceWeight = classWeight * sampleWeight
        this.root = this.buildTree(dataset, 1);
        this.prune();
    }

    /**
     * Predicts the class value for given instance.
     *
     * @param instance the specified instance
     * @return class value for given instance
     */
    public double predict(Instance instance) {
        if (this.root == null) {
            throw new EstimatorNotFittedException("This decision tree is not fitted yet.");
        }
        return this.predictWalk(this.root, instance);

    }

    private double predictWalk(DTNode node, Instance instance) {
        if (!node.isLeaf()) { // node不是叶节点
            int attrIndex = node.feature.index;
            double attrValue = instance.attribute(attrIndex);
            DTNode child = node.matchChild(attrValue);
            if (child != null) {
                return predictWalk(child, instance);
            }
        }
        // 1. child==null: instance中有未知的离散特征值时，返回该非叶节点的默认class
        // 2. node为叶节点，返回叶节点的class
        return node.clazz;
    }

    /**
     * Prints this fitted decision tree.
     *
     * @throws EstimatorNotFittedException if this decision tree is not fitted yet
     */
    public void print() {
        if (this.root == null) {
            throw new EstimatorNotFittedException("This decision tree is not fitted yet.");
        }
        this.root.print();
    }

    /**
     * Returns a sequence of effective alphas and a sequence of pruned tree correspondingly.
     *
     * @return a sequence of effective alphas and a sequence of pruned tree correspondingly
     * @throws EstimatorNotFittedException if this decision tree is not fitted yet
     */
    public Map<Double, DTNode> prunedSubTrees() {
        if (this.root == null) {
            throw new EstimatorNotFittedException("This decision tree is not fitted yet.");
        }
        return this.subTrees;
    }

    @Override
    public Map<Double, PerformanceMeasure> crossValidation(DataSet dataset, int k) {
        Map<Double, PerformanceMeasure> res = new TreeMap<>();
        for (double clazz: dataset.classSet()) {
            res.put(clazz, new PerformanceMeasure());
        }
        DataSet[] folds = DataSets.folds(dataset, k);
        List<AttributeInfo> attributeInfoList = dataset.attributeInfoList();
        AttributeInfo classInfo = dataset.classInfo();
        for (int i = 0; i < k; i++) {
            DataSet validation = folds[i]; // 测试集
            DataSet training = new StandardDataSet(attributeInfoList, classInfo); // 训练集
            for (int j = 0; j < k; j++) {
                if (j != i) {
                    for (Instance instance : folds[i]) {
                        training.add(instance);
                    }
                }
            }
            this.fit(training);
            for (Instance instance: validation) {
                double prediction = this.predict(instance);
                double actual = instance.classValue();
                double weight = instance.getWeight();
                if (prediction == actual) { // 预测正确
                    for (double clazz: res.keySet()) {
                        if (clazz == actual) { // 本来为正，预测为正
                            res.get(clazz).TP += weight;
                        } else { // 本来为负，预测为负
                            res.get(clazz).TN += weight;
                        }
                    }
                } else {
                    for (double clazz: res.keySet()) {
                        if (clazz == actual) { // 本来为正，预测成负
                            res.get(clazz).FN += weight;
                        } else if (clazz == prediction) { // 本来为负，预测为正
                            res.get(clazz).FP += weight;
                        } else { // 本来为负，预测为负
                            res.get(clazz).TN += weight;
                        }
                    }
                }
            }
        }
        return res;
    }

    protected abstract DTNode buildTree(DataSet dataset, int depth);

    protected abstract SplitRecord selectBestFeature(DataSet dataset);

    /**
     * Evaluates the impurity of given dataset.
     * The smaller the impurity the better.
     *
     * @return impurity of given dataset
     */
    protected abstract double computeImpurity(DataSet dataset);

    /**
     * Gets default class for a data set.
     * The default class is the one with the largest weighted number.
     *
     * @param dataset given data set
     * @return the default class
     */
    protected double getDefaultClass(DataSet dataset) {
        Map<Double, Double> classDistMap = DataSets.columnDistMap(dataset, -1); // 获取每个类的加权数量分布
        double maxWeightedNumber = -1;
        double defaultClass = Double.NaN;
        for (Map.Entry<Double, Double> entry: classDistMap.entrySet()) {
            double thisNumber = entry.getValue(); // 当前类的数量
            if (thisNumber > maxWeightedNumber) {
                maxWeightedNumber = thisNumber;
                defaultClass = entry.getKey();
            }
        }
        return defaultClass;
    }

    /**
     * Preprocess: set each instance's weight.
     * <p>
     *     instanceWeight = classWeight * sampleWeight
     * </p>
     * <p>
     * If given classWeight is null, weight of each class will be set 1.
     * If given sampleWeight is null, weight of each sample will be set 1.
     * </p>
     *
     * @param dataset the data set to fit
     * @param classWeight a map contains weights associated with classes
     * @param sampleWeight an array containing sample weights
     * @throws IllegalArgumentException if the length of sampleWeight != the size of dataset
     */
    private void preprocess(DataSet dataset, Map<Double, Double> classWeight, double[] sampleWeight) {
        // 设置每个instance的权重
        ClassWeightUtil.setWeight(dataset, classWeight, sampleWeight);
        this.weightedNSamples = DataSets.sumWeight(dataset);
    }

    private TreeMap<Double, DTNode> subTrees;

    private void prune() {
        CCPPrune prune = new CCPPrune(this);
        this.subTrees = prune.pruneAll();
        this.root = prune.pruneWithAlpha(this.ccpAlpha);
    }
}
