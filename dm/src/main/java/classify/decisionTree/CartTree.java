package main.java.classify.decisionTree;

import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.Instance;
import main.java.core.StandardDataSet;
import main.java.utils.SetUtil;

import java.util.*;

/**
 * This class uses the CART algorithm to build a decision tree.
 *
 * @author Cloudy1225
 * @see DecisionTree
 */
public class CartTree extends DecisionTree {

    public CartTree() {
        super();
    }

    public CartTree(int maxDepth, int minSamplesSplit, int minSamplesLeaf, double minImpurityDecrease, double ccpAlpha) {
        super(maxDepth, minSamplesSplit, minSamplesLeaf, minImpurityDecrease, ccpAlpha);
    }

    @Override
    protected CartNode buildTree(DataSet dataset, int depth) {
        CartNode node;
        if (depth >= this.maxDepth) {
            node = new DiscreteCartNode(null, null);
            node.feature = dataset.classInfo();
            node.depth = depth;
            node.impurity = this.computeImpurity(dataset); // 计算该节点的不纯度
            node.clazz = this.getDefaultClass(dataset); // 数据集中最多的类为默认类;
            node.nNodeSamples = dataset.size();
            node.weightedNNodeSamples = DataSets.sumWeight(dataset);
            return node;
        }
        Set<Double> classSet = dataset.classSet();
        if (classSet.size() == 1) { // 只有一个类
            node = new DiscreteCartNode(null, null);
            node.feature = dataset.classInfo();
            node.depth = depth;
            node.impurity = 0;
            node.clazz = classSet.iterator().next();
            node.nNodeSamples = dataset.size();
            node.weightedNNodeSamples = DataSets.sumWeight(dataset);
            return node;
        }
        if (dataset.dimensionality() == 0) { // 属性用完了
            // 则node为叶节点，所属类为数据集中样本最多的类
            node = new DiscreteCartNode(null, null);
            node.feature = dataset.classInfo();
            node.depth = depth;
            node.impurity = this.computeImpurity(dataset); // 计算该节点的不纯度
            node.clazz = this.getDefaultClass(dataset); // 数据集中最多的类为默认类;
            node.nNodeSamples = dataset.size();
            node.weightedNNodeSamples = DataSets.sumWeight(dataset);
            return node;
        }
        if (dataset.size() < this.minSamplesSplit) {
            node = new DiscreteCartNode(null, null);
            node.feature = dataset.classInfo();
            node.depth = depth;
            node.impurity = this.computeImpurity(dataset); // 计算该节点的不纯度
            node.clazz = this.getDefaultClass(dataset); // 数据集中最多的类为默认类;
            node.nNodeSamples = dataset.size();
            node.weightedNNodeSamples = DataSets.sumWeight(dataset);
            return node;
        }
        GiniIndex splitRecord = this.selectBestFeature(dataset); // 特征选择
        if (splitRecord.improvement < this.minImpurityDecrease) {  // 划分后不纯度降低过小时
            // 置node为叶节点
            node = new DiscreteCartNode(null, null);
            node.feature = dataset.classInfo();
            node.depth = depth;
            node.impurity = splitRecord.impurity;
            node.clazz = this.getDefaultClass(dataset); // 数据集中最多的类为默认类;
            node.nNodeSamples = dataset.size();
            node.weightedNNodeSamples = DataSets.sumWeight(dataset);
            return node;
        }
        // 划分数据集
        Map<Double, DataSet> subDataSets = this.split(dataset, splitRecord);
        {
            if (dataset.attributeInfo(splitRecord.feature).continuous()) {
                node = new ContinuousCartNode(splitRecord.splitPoint);
            } else {
                node = new DiscreteCartNode(splitRecord.leftSplitSet, splitRecord.rightSplitSet);
            }
            CartNode leftChild;
            DataSet leftDataSet = subDataSets.get(-1.0);
            if (leftDataSet.size() < minSamplesLeaf) { // 子集样本量过少，直接置为叶节点，不再继续分割
                leftChild = new DiscreteCartNode(null, null);
                leftChild.depth = depth + 1;
                leftChild.clazz = this.getDefaultClass(leftDataSet);
                leftChild.feature = leftDataSet.classInfo();
                leftChild.nNodeSamples = leftDataSet.size();
                leftChild.weightedNNodeSamples = DataSets.sumWeight(leftDataSet);
                leftChild.impurity = this.computeImpurity(leftDataSet);
            } else {
                leftChild = this.buildTree(leftDataSet, depth+1);
            }
            CartNode rightChild;
            DataSet rightDataSet = subDataSets.get(1.0);
            if (rightDataSet.size() < minSamplesLeaf) { // 子集样本量过少，直接置为叶节点，不再继续分割
                rightChild = new DiscreteCartNode(null, null);
                rightChild.depth = depth + 1;
                rightChild.clazz = this.getDefaultClass(rightDataSet);
                rightChild.feature = rightDataSet.classInfo();
                rightChild.nNodeSamples = rightDataSet.size();
                rightChild.weightedNNodeSamples = DataSets.sumWeight(rightDataSet);
                rightChild.impurity = this.computeImpurity(rightDataSet);
            } else {
                rightChild = this.buildTree(rightDataSet, depth+1);
            }
            node.addChild(leftChild);
            node.addChild(rightChild);
        }
        node.feature = dataset.attributeInfo(splitRecord.feature);
        node.depth = depth;
        node.impurity = splitRecord.impurity; // 计算该节点的不纯度
        node.clazz = this.getDefaultClass(dataset); // 数据集中最多的类为默认类;
        node.nNodeSamples = dataset.size();
        node.weightedNNodeSamples = DataSets.sumWeight(dataset);
        return node;
    }

    private Map<Double, DataSet> split(DataSet dataset, GiniIndex splitRecord) {
        int attrIndex = splitRecord.feature; // Feature to split on.
        StandardDataSet leftDataSet = new StandardDataSet(dataset.attributeInfoList(), dataset.classInfo());
        StandardDataSet rightDataSet = new StandardDataSet(dataset.attributeInfoList(), dataset.classInfo());
        if (dataset.attributeInfo(attrIndex).continuous()) {
            double splitPoint = splitRecord.splitPoint;
            for (Instance instance: dataset) {
                if (instance.attribute(attrIndex) <= splitPoint) {
                    leftDataSet.add(instance);
                } else {
                    rightDataSet.add(instance);
                }
            }
        } else {
            Set<Double> leftSplitSet = splitRecord.leftSplitSet;
            for (Instance instance: dataset) {
                if (leftSplitSet.contains(instance.attribute(attrIndex))) {
                    leftDataSet.add(instance);
                } else {
                    rightDataSet.add(instance);
                }
            }
        }
        TreeMap<Double, DataSet> subDataSets = new TreeMap<>();
        subDataSets.put(-1.0, leftDataSet);
        subDataSets.put(1.0, rightDataSet);
        return subDataSets;
    }

    @Override
    protected GiniIndex selectBestFeature(DataSet dataset) {
        GiniIndex res = new GiniIndex();
        double classGini = this.computeImpurity(dataset);
        double sumWeightedNumber = DataSets.sumWeight(dataset);

        TreeMap<Double, Double> classDistMap = DataSets.columnDistMap(dataset, -1); // 类的分布

        double minGini = 2; // 最小基尼系数
        double leftImpurity = 2;
        double rightImpurity = 2;
        int attrIndex = -1;
        for (int i = 0; i < dataset.dimensionality(); i++) {
            // 类相对于当前属性的条件分布
            TreeMap<Double, TreeMap<Double, Double>> conDistMap = DataSets.colConDistMap(dataset, i, -1);
            if (dataset.attributeInfo(i).continuous()) { // 当前属性为连续时
                // 寻找最优划分点
                double bestSplitPoint = 0;
                double thisMinGini = 2; // 最优划分点对应的基尼系数
                double thisImprovement = 0;
                double ltImpurity = 2; // 属性值小于最优划分点的子集不确定性
                double gtImpurity = 2; // 属性值大于最优划分点的子集不确定性
                if (conDistMap.size() == 1) { // 连续属性的取值却只有1个
                    bestSplitPoint = conDistMap.keySet().iterator().next();
                    // 此时基尼指数就是类的基尼指数
                    thisMinGini = classGini;
                    ltImpurity = classGini;
                    gtImpurity = 1;
                } else {
                    Map<Double, Double> ltConDistMap = new TreeMap<>(); // 属性值小于最优划分点的条件下class分布
                    Map<Double, Double> gtConDistMap = new TreeMap<>(); // 属性值大于最优划分点的条件下class分布
                    // 初始化 ltConDistMap, gtConDistMap
                    for (Double clazz: classDistMap.keySet()) {
                        ltConDistMap.put(clazz, 0.0);
                        gtConDistMap.put(clazz, 0.0);
                    }
                    // 遍历 N-1 个可能的划分点
                    Iterator<Map.Entry<Double, TreeMap<Double,Double>>> iter1 = conDistMap.entrySet().iterator();
                    Iterator<Map.Entry<Double, TreeMap<Double,Double>>> iter2 = conDistMap.entrySet().iterator();
                    iter2.next();
                    for (int j = 0; j < conDistMap.size()-1; j++) {
                        Map.Entry<Double, TreeMap<Double, Double>> entry = iter1.next();
                        double a1 = entry.getKey();
                        double a2 = iter2.next().getKey();
                        double splitPoint = a1/2 + a2/2; // 划分点
                        for (Map.Entry<Double, Double> classDist: entry.getValue().entrySet()) {
                            Double classValue = classDist.getKey();
                            Double weightedNumber = ltConDistMap.get(classValue) + classDist.getValue();
                            ltConDistMap.put(classValue, weightedNumber);
                            gtConDistMap.put(classValue, classDistMap.get(classValue) - weightedNumber);
                        }
                        double ltWeightedNumber = 0;
                        double ltGini = 0;
                        for (double weightedNumber: ltConDistMap.values()) {
                            ltGini -= weightedNumber * weightedNumber;
                            ltWeightedNumber += weightedNumber;
                        }
                        ltGini = ltGini / (ltWeightedNumber*ltWeightedNumber) + 1;
                        double gtWeightedNumber = 0;
                        double gtGini = 0;
                        for (double weightedNumber: gtConDistMap.values()) {
                            gtGini -= weightedNumber * weightedNumber;
                            gtWeightedNumber += weightedNumber;
                        }
                        gtGini = gtGini / (gtWeightedNumber*gtWeightedNumber) + 1;
                        double gini = ltWeightedNumber*ltGini/sumWeightedNumber + gtWeightedNumber*gtGini/sumWeightedNumber;
                        if (gini <= thisMinGini) {
                            thisMinGini = gini;
                            bestSplitPoint = splitPoint;
                            thisImprovement = (sumWeightedNumber*classGini - ltWeightedNumber*ltGini - gtWeightedNumber*gtGini)/this.weightedNSamples;
                            ltImpurity = ltGini;
                            gtImpurity = gtGini;
                        }
                    }
                }
                if (thisMinGini <= minGini) {
                    minGini = thisMinGini;
                    attrIndex = i;
                    leftImpurity = ltImpurity;
                    rightImpurity = gtImpurity;
                    res.improvement = thisImprovement;
                    res.splitPoint = bestSplitPoint;
                    res.leftSplitSet = null;
                    //res.rightSplitSet = null;
                }
            } else { // 离散特征
                // 寻找最优划分子集
                Set<Double> bestSplitSet = null; //  最优划分子集
                double thisMinGini = 2; // 该特征不同划分下的最小基尼指数
                double thisImprovement = 0;
                double inImpurity = 2;
                double notInImpurity = 2;
                if (conDistMap.size() == 1) { // 特征值只有一个
                    thisMinGini = classGini;
                    inImpurity = classGini;
                    notInImpurity = 1;
                    bestSplitSet = new HashSet<>();
                    bestSplitSet.add(conDistMap.keySet().iterator().next());
                } else {
                    Set<Double> attrValueSet = dataset.attrValueSet(i); // 离散特征的值集合
                    Set<Double>[] subValueSets = SetUtil.subSets(attrValueSet); // 集合的子集
                    for (int j = 1; j < subValueSets.length / 2; j++) {
                        Set<Double> subValueSet = subValueSets[j];
                        Map<Double, Double> inConDistMap = new TreeMap<>(); // 属性值在子集中的条件下class分布
                        Map<Double, Double> notInConDistMap = new TreeMap<>(); // 属性值不在子集中的条件下class分布
                        // 初始化 inConDistMap, notInContDisMap
                        for (double classValue: classDistMap.keySet()) {
                            inConDistMap.put(classValue, 0.0);
                            notInConDistMap.put(classValue, 0.0);
                        }
                        // 更新 inConDistMap
                        for (Map.Entry<Double, TreeMap<Double, Double>> entry: conDistMap.entrySet()) {
                            double attrValue = entry.getKey();
                            if (subValueSet.contains(attrValue)) {
                                for (Map.Entry<Double, Double> dist: entry.getValue().entrySet()) {
                                    double clazz = dist.getKey();
                                    double weightedNumber = inConDistMap.get(clazz) + dist.getValue();
                                    inConDistMap.put(clazz, weightedNumber);
                                }
                            }
                        }
                        // 更新 notInConDistMap
                        for (Map.Entry<Double, Double> dist: classDistMap.entrySet()) {
                            double clazz = dist.getKey();
                            double weightedNumber = dist.getValue() - inConDistMap.get(clazz);
                            notInConDistMap.put(clazz, weightedNumber);
                        }
                        // 计算新分布下的gini指数
                        double inWeightedNumber = 0;
                        double inGini = 0;
                        for (double weightedNumber: inConDistMap.values()) {
                            inGini -= weightedNumber * weightedNumber;
                            inWeightedNumber += weightedNumber;
                        }
                        inGini = inGini / (inWeightedNumber*inWeightedNumber) + 1;
                        double notInWeightedNumber = 0;
                        double notInGini = 0;
                        for (double weightedNumber: notInConDistMap.values()) {
                            notInGini -= weightedNumber * weightedNumber;
                            notInWeightedNumber += weightedNumber;
                        }
                        notInGini = notInGini / (notInWeightedNumber*notInWeightedNumber) + 1;
                        double gini = inWeightedNumber*inGini/sumWeightedNumber + notInWeightedNumber*notInGini/sumWeightedNumber;
                        if (gini <= thisMinGini) {
                            thisMinGini = gini;
                            bestSplitSet = subValueSet;
                            thisImprovement = (sumWeightedNumber*classGini - inWeightedNumber*inGini - notInWeightedNumber*notInGini)/this.weightedNSamples;
                            inImpurity = inGini;
                            notInImpurity = notInGini;
                        }
                    }
                }
                if (thisMinGini <= minGini) {
                    minGini  = thisMinGini;
                    res.leftSplitSet = bestSplitSet;
                    res.splitPoint = Double.NaN;
                    res.improvement = thisImprovement;
                    attrIndex = i;
                    leftImpurity = inImpurity;
                    rightImpurity = notInImpurity;
                }
            }
            res.feature = attrIndex;
            res.impurity = classGini;
            res.leftImpurity = leftImpurity;
            res.rightImpurity = rightImpurity;
            if (!dataset.attributeInfo(attrIndex).continuous()) {
                res.rightSplitSet = SetUtil.except(dataset.attrValueSet(res.feature), res.leftSplitSet);
            }
        }
        return res;
    }

    private static class GiniIndex extends SplitRecord {

        private double splitPoint;
        private Set<Double> leftSplitSet;
        private Set<Double> rightSplitSet;
        private double leftImpurity;
        private double rightImpurity;
    }

    /**
     * Computes the gini index of given dataset.
     * CartTree use "gini index" to estimate the impurity.
     *
     * @param dataset  a data set
     * @return the gini index of given dataset
     */
    @Override
    protected double computeImpurity(DataSet dataset) {
        Map<Double, Double> classDistMap = DataSets.columnDistMap(dataset, -1);
        double totalWeightedNumber = 0;
        for (double eachWeightedNumber: classDistMap.values()) {
            totalWeightedNumber += eachWeightedNumber;
        }
        double gini = 1;
        for (double eachWeightedNumber: classDistMap.values()) {
            double p_i = eachWeightedNumber / totalWeightedNumber;
            gini -= p_i*p_i;
        }
        return gini;
    }


    private abstract static class CartNode extends DTNode {

        protected CartNode leftChild;

        protected CartNode rightChild;

        @Override
        protected boolean isLeaf() {
            return this.leftChild == null && this.rightChild == null;
        }

        @Override
        protected void addChild(DTNode child) {
            CartNode node = (CartNode) child;
            if (this.leftChild == null) {
                this.leftChild = node;
            } else {
                this.rightChild = node;
            }
        }

        @Override
        protected Iterator<CartNode> children() {
            if (this.isLeaf()) {
                return new Iterator<CartNode>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public CartNode next() {
                        return null;
                    }
                };
            }

            return new Iterator<CartNode>() {
                private byte flag = 2;

                @Override
                public boolean hasNext() {
                    return flag > 0;
                }

                @Override
                public CartNode next() {
                    flag--;
                    if (flag == 1) {
                        return leftChild;
                    } else if (flag == 0) {
                        return rightChild;
                    } else {
                        return null;
                    }
                }
            };
        }

        public abstract CartNode copy();
    }

    private static class ContinuousCartNode extends CartNode {

        private final double splitPoint;

        public ContinuousCartNode(double splitPoint) {
            this.splitPoint = splitPoint;
        }

        @Override
        protected void toLeaf() {
            this.leftChild = null;
            this.rightChild = null;
        }

        @Override
        protected CartNode matchChild(double value) {
            if (value <= this.splitPoint) {
                return this.leftChild;
            } else {
                return this.rightChild;
            }
        }

        @Override
        protected String edgeString() {
            return "[ [<=" + splitPoint + "] [>" + splitPoint + "] ]";
        }

        @Override
        public ContinuousCartNode copy() {
            ContinuousCartNode node = new ContinuousCartNode(this.splitPoint);
            node.feature = this.feature;
            node.clazz = this.clazz;
            node.depth = this.depth;
            node.impurity = this.impurity;
            node.nNodeSamples = this.nNodeSamples;
            node.weightedNNodeSamples = this.weightedNNodeSamples;
            return node;
        }
    }

    private static class DiscreteCartNode extends CartNode {

        private Set<Double> leftSplitSet;

        private Set<Double> rightSplitSet;

        public DiscreteCartNode(Set<Double> leftSplitSet, Set<Double> rightSplitSet) {
            this.leftSplitSet = leftSplitSet;
            this.rightSplitSet = rightSplitSet;
        }

        @Override
        protected void toLeaf() {
            this.leftChild = null;
            this.rightChild = null;
            // let GC work.
            this.leftSplitSet = null;
            this.rightSplitSet = null;

        }

        @Override
        protected CartNode matchChild(double value) {
            if (leftSplitSet.contains(value)) {
                return this.leftChild;
            } else if (rightSplitSet.contains(value)) {
                return this.rightChild;
            }
            return null;
        }

        @Override
        protected String edgeString() {
            return "[ " + leftSplitSet.toString() + " " + rightSplitSet.toString() + " ]";
        }

        @Override
        public DiscreteCartNode copy() {
            DiscreteCartNode node = new DiscreteCartNode(this.leftSplitSet, this.rightSplitSet);
            node.feature = this.feature;
            node.clazz = this.clazz;
            node.depth = this.depth;
            node.impurity = this.impurity;
            node.nNodeSamples = this.nNodeSamples;
            node.weightedNNodeSamples = this.weightedNNodeSamples;
            return node;
        }
    }
}
