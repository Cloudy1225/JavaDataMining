package main.java.classify.decisionTree;

import main.java.core.*;
import main.java.utils.MathUtil;

import java.util.*;

/**
 * This class uses the ID3 algorithm to build a decision tree.
 *
 * @author Cloudy1225
 * @see DecisionTree
 */
public class ID3Tree extends DecisionTree {

    public ID3Tree() {
        super();
    }

    public ID3Tree(int maxDepth, int minSamplesSplit, int minSamplesLeaf, double minImpurityDecrease, double ccpAlpha) {
        super(maxDepth, minSamplesSplit, minSamplesLeaf, minImpurityDecrease, ccpAlpha);
    }

    @Override
    protected ID3Node buildTree(DataSet dataset, int depth) {
        ID3Node node = new ID3Node();
        node.depth = depth;
        node.impurity = this.computeImpurity(dataset); // 计算该节点的不纯度
        node.clazz = this.getDefaultClass(dataset); // 数据集中最多的类为默认类;
        node.nNodeSamples = dataset.size();
        node.weightedNNodeSamples = DataSets.sumWeight(dataset);
        if (depth >= this.maxDepth) { // 深度已达最大值
            node.feature = dataset.classInfo();
            return node;
        }
        Set<Double> classSet = dataset.classSet();
        if (classSet.size() == 1) { // 只有一个类
            node.impurity = 0;
            node.feature = dataset.classInfo();
            return node;
        }
        if (dataset.dimensionality() == 0) { // 属性用完了
            // 则node为叶节点，所属类为数据集中样本最多的类
            node.feature = dataset.classInfo();
            return node;
        }
        if (dataset.size() < this.minSamplesSplit) { // 数据集样本过少，小于最小分割值
            // 置node为叶节点
            node.feature = dataset.classInfo();
            return node;
        }
        InfoGain splitRecord = this.selectBestFeature(dataset); // 特征选择
        if (splitRecord.improvement < this.minImpurityDecrease) {  // 划分后不纯度降低过小时
            // 置node为叶节点
            node.feature = dataset.classInfo();
            return node;
        }
        // 划分数据集
        Map<Double, DataSet> subDataSets = this.split(dataset, splitRecord);
        for (Map.Entry<Double, DataSet> entry: subDataSets.entrySet()) {
            ID3Node child;
            DataSet subDataSet = entry.getValue();
            if (subDataSet.size() < minSamplesLeaf) { // 子集样本量过少，直接置为叶节点，不再继续分割
                child = new ID3Node();
                child.depth = depth + 1;
                child.clazz = this.getDefaultClass(subDataSet);
                child.feature = subDataSet.classInfo();
                child.nNodeSamples = subDataSet.size();
                child.weightedNNodeSamples = DataSets.sumWeight(subDataSet);
                child.impurity = this.computeImpurity(subDataSet);
            } else {
                child = this.buildTree(subDataSet, depth+1);
            }
            node.addChild(child);
            node.addEdgeValue(entry.getKey());
        }
        node.feature = dataset.attributeInfo(splitRecord.feature);
        return node;
    }

    private Map<Double, DataSet> split(DataSet dataset, InfoGain splitRecord) {
        TreeMap<Double, DataSet> subDataSets = new TreeMap<>();
        int attrIndex = splitRecord.feature; // Feature to split on.
        Map<Double, Double> thresholdImpurityMap  = splitRecord.thresholdImpurityMap;
        List<AttributeInfo> oldAttributeInfoList = dataset.attributeInfoList();
        List<AttributeInfo> attributeInfoList = new ArrayList<>();
        for (int i = 0; i < attrIndex; i++) {
            attributeInfoList.add(oldAttributeInfoList.get(i));
        }
        for (int i = attrIndex+1; i < oldAttributeInfoList.size(); i++) {
            attributeInfoList.add(oldAttributeInfoList.get(i));
        }
        for (Double threshold: thresholdImpurityMap.keySet()) {
            subDataSets.put(threshold, new StandardDataSet(attributeInfoList, dataset.classInfo()));
        }
        for (Instance oldInstance: dataset) {
            double threshold = oldInstance.attribute(attrIndex);
            DataSet subDataSet = subDataSets.get(threshold);
            subDataSet.add(oldInstance.deleteAttribute(attrIndex));
        }
        return subDataSets;
    }

    protected InfoGain selectBestFeature(DataSet dataset) {
        int attrIndex = -1; // 选中属性的索引
        double originEntropy = this.computeImpurity(dataset); // 未分割前的熵
        double minSplitEntropy = originEntropy; // 最小分割后子集的熵之和，与最大信息增益相对应
        for (int i = 0; i < dataset.dimensionality(); i++) {
            // 类相对于当前属性的条件分布
            TreeMap<Double, TreeMap<Double, Double>> conDistMap = DataSets.colConDistMap(dataset, i, -1);
            double totalWeightedNumber = 0; // 总样本大小
            double thisSplitEntropy = 0; // 按当前属性分割后的熵和
            for (TreeMap<Double, Double> distMap: conDistMap.values()) {
                double subWeightedNumber = 0; // 子集样本大小
                double subEntropy = 0; // 子集的熵
                for (double weightedNumber: distMap.values()) {
                    if (weightedNumber == 0) continue;
                    subWeightedNumber += weightedNumber;
                    subEntropy -= (weightedNumber * MathUtil.log2(weightedNumber));
                }
                subEntropy = subEntropy / subWeightedNumber + MathUtil.log2(subWeightedNumber);
                thisSplitEntropy += subWeightedNumber * subEntropy;
                totalWeightedNumber += subWeightedNumber;
            }
            thisSplitEntropy /= totalWeightedNumber;
            if (thisSplitEntropy <= minSplitEntropy) { // 当前属性更优
                minSplitEntropy = thisSplitEntropy;
                attrIndex = i;
            }
        }
        TreeMap<Double, Double> thresholdImpurityMap = new TreeMap<>();
        TreeMap<Double, TreeMap<Double, Double>> conDistMap = DataSets.colConDistMap(dataset, attrIndex, -1);
        double improvement = 0;
        double totalWeightedNumber = 0;
        for (Map.Entry<Double, TreeMap<Double, Double>> entry: conDistMap.entrySet()) {
            double threshold = entry.getKey();
            double subEntropy = 0;
            double subWeightedNumber = 0;
            TreeMap<Double, Double> distMap = entry.getValue();
            for (double weightedNumber: distMap.values()) {
                if (weightedNumber == 0) continue;
                subWeightedNumber += weightedNumber;
                subEntropy -= (weightedNumber * MathUtil.log2(weightedNumber));
            }
            subEntropy = subEntropy / subWeightedNumber + MathUtil.log2(subWeightedNumber);
            totalWeightedNumber += subWeightedNumber;
            improvement -= subWeightedNumber * subEntropy / this.weightedNSamples;
            thresholdImpurityMap.put(threshold, subEntropy);
        }
        improvement += totalWeightedNumber / this.weightedNSamples * originEntropy;
        return new InfoGain(attrIndex, originEntropy, improvement, thresholdImpurityMap);
    }

    private static class InfoGain extends SplitRecord {

        /**
         * A map holding threshold to split at and corresponding impurity of sub-dataset.
         */
        private final TreeMap<Double, Double> thresholdImpurityMap;


        public InfoGain(int feature, double impurity, double improvement, TreeMap<Double, Double> thresholdImpurityMap) {
            this.feature = feature;
            this.impurity = impurity;
            this.improvement = improvement;
            this.thresholdImpurityMap = thresholdImpurityMap;
        }
    }

    /**
     * Computes the entropy of given dataset.
     * ID3Tree use "entropy" to estimate the impurity.
     * <P>
     * This handles cases where the target is a classification taking values
     * 0, 1, ... K-2, K-1. If node m represents a region Rm with Nm observations,
     * then let
     *
     *         count_k = 1 / Nm \sum_{x_i in Rm} I(yi = k)
     *
     * be the proportion of class k observations in node m.
     *
     * The entropy is then defined as
     *
     *         entropy = -\sum_{k=0}^{K-1} count_k log(count_k)
     * </P>
     * @param dataset  a data set
     * @return the entropy of given dataset
     */
    @Override
    protected double computeImpurity(DataSet dataset) {
        Map<Double, Double> classDistMap = DataSets.columnDistMap(dataset, -1);
        double totalWeightedNumber = 0;
        double entropy = 0;
        for (double eachWeightedNumber: classDistMap.values()) {
            if (eachWeightedNumber == 0) continue;
            totalWeightedNumber += eachWeightedNumber;
            entropy -= eachWeightedNumber * MathUtil.log2(eachWeightedNumber);
        }
        entropy = entropy / totalWeightedNumber + MathUtil.log2(totalWeightedNumber);
        return entropy;
    }

    private static class ID3Node extends DTNode {

        private ArrayList<Double> edgeValues;

        private ArrayList<ID3Node> children;

        public ID3Node() {
            this.children = new ArrayList<>(2);
            this.edgeValues = new ArrayList<>(2);
        }

        private void addEdgeValue(double edgeValue) {
            this.edgeValues.add(edgeValue);
        }

        @Override
        protected boolean isLeaf() {
            return this.children.size() == 0;
        }

        @Override
        protected void toLeaf() {
            this.children.clear();
            this.edgeValues.clear();
        }

        @Override
        protected void addChild(DTNode child) {
            ID3Node node = (ID3Node) child;
            this.children.add(node);
        }

        @Override
        protected ID3Node matchChild(double value) {
            for (int i = 0; i < edgeValues.size(); i++) {
                if (edgeValues.get(i) == value) {
                    return children.get(i);
                }
            }
            return null;
        }

        @Override
        protected Iterator<ID3Node> children() {
            if (this.children == null) {
                return new Iterator<ID3Node>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public ID3Node next() {
                        return null;
                    }
                };
            }
            return this.children.iterator();
        }

        @Override
        protected String edgeString() {
            StringBuilder sb = new StringBuilder("[ ");
            for (Double edgeValue: edgeValues) {
                sb.append("[");
                sb.append(edgeValue);
                sb.append("] ");
            }
            sb.append("]");
            return sb.toString();
        }

        @Override
        public ID3Node copy() {
            ID3Node node = new ID3Node();
            node.feature = this.feature;
            node.clazz = this.clazz;
            node.depth = this.depth;
            node.impurity = this.impurity;
            node.nNodeSamples = this.nNodeSamples;
            node.weightedNNodeSamples = this.weightedNNodeSamples;
            node.edgeValues.addAll(this.edgeValues);
            return node;
        }
    }
}
