package main.java.classify.decisionTree;

import main.java.core.*;
import main.java.utils.ArrayUtil;
import main.java.utils.DataUtil;

import java.util.*;

/**
 * This class uses the C4.5 algorithm to build a decision tree.
 *
 * @author Cloudy1225
 * @see DecisionTree
 */
public class C45Tree extends DecisionTree {

    public C45Tree() {
        super();
    }

    public C45Tree(int maxDepth, int minSamplesSplit, int minSamplesLeaf, double minImpurityDecrease, double ccpAlpha) {
        super(maxDepth, minSamplesSplit, minSamplesLeaf, minImpurityDecrease, ccpAlpha);
    }

    @Override
    protected C45Node buildTree(DataSet dataset, int depth) {
        C45Node node;
        if (depth >= this.maxDepth) {
            node = new DiscreteC45Node();
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
            node = new DiscreteC45Node();
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
            node = new DiscreteC45Node();
            node.feature = dataset.classInfo();
            node.depth = depth;
            node.impurity = this.computeImpurity(dataset); // 计算该节点的不纯度
            node.clazz = this.getDefaultClass(dataset); // 数据集中最多的类为默认类;
            node.nNodeSamples = dataset.size();
            node.weightedNNodeSamples = DataSets.sumWeight(dataset);
            return node;
        }
        if (dataset.size() < this.minSamplesSplit) {
            node = new DiscreteC45Node();
            node.feature = dataset.classInfo();
            node.depth = depth;
            node.impurity = this.computeImpurity(dataset); // 计算该节点的不纯度
            node.clazz = this.getDefaultClass(dataset); // 数据集中最多的类为默认类;
            node.nNodeSamples = dataset.size();
            node.weightedNNodeSamples = DataSets.sumWeight(dataset);
            return node;
        }
        GainRatio splitRecord = this.selectBestFeature(dataset);
        if (splitRecord.improvement < this.minImpurityDecrease) {  // 划分后不纯度降低过小时
            // 置node为叶节点
            node = new DiscreteC45Node();
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
        if (dataset.attributeInfo(splitRecord.feature).continuous()) {
            node = new ContinuousC45Node(splitRecord.splitPoint);
            for (DataSet subDataSet: subDataSets.values()) {
                C45Node child;
                if (subDataSet.size() < minSamplesLeaf) { // 子集样本量过少，直接置为叶节点，不再继续分割
                    child = new DiscreteC45Node();
                    child.depth = depth + 1;
                    child.clazz = this.getDefaultClass(subDataSet);
                    child.feature = subDataSet.classInfo();
                    child.nNodeSamples = subDataSet.size();
                    child.weightedNNodeSamples = DataSets.sumWeight(subDataSet);
                    child.impurity = this.computeImpurity(subDataSet);
                }else {
                    child = this.buildTree(subDataSet, depth+1);
                }
                node.addChild(child);
            }
        } else {
            node = new DiscreteC45Node();
            for (Map.Entry<Double, DataSet> entry: subDataSets.entrySet()) {
                C45Node child;
                DataSet subDataSet = entry.getValue();
                if (subDataSet.size() < minSamplesLeaf) { // 子集样本量过少，直接置为叶节点，不再继续分割
                    child = new DiscreteC45Node();
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
                ((DiscreteC45Node) node).addEdgeValue(entry.getKey());
            }
        }
        node.feature = dataset.attributeInfo(splitRecord.feature);
        node.depth = depth;
        node.impurity = splitRecord.impurity; // 计算该节点的不纯度
        node.clazz = this.getDefaultClass(dataset); // 数据集中最多的类为默认类;
        node.nNodeSamples = dataset.size();
        node.weightedNNodeSamples = DataSets.sumWeight(dataset);
        return node;
    }

    private Map<Double, DataSet> split(DataSet dataset, GainRatio splitRecord) {
        TreeMap<Double, DataSet> subDataSets = new TreeMap<>();
        int attrIndex = splitRecord.feature; // Feature to split on.
        List<AttributeInfo> oldAttributeInfoList = dataset.attributeInfoList();
        List<AttributeInfo> attributeInfoList = new ArrayList<>();
        for (int i = 0; i < attrIndex; i++) {
            attributeInfoList.add(oldAttributeInfoList.get(i));
        }
        for (int i = attrIndex+1; i < oldAttributeInfoList.size(); i++) {
            attributeInfoList.add(oldAttributeInfoList.get(i));
        }
        if (dataset.attributeInfo(attrIndex).continuous()) {
            StandardDataSet ltDataSet = new StandardDataSet(attributeInfoList, dataset.classInfo());
            StandardDataSet gtDataSet = new StandardDataSet(attributeInfoList, dataset.classInfo());
            double splitPoint = splitRecord.splitPoint;
            for (Instance oldInstance: dataset) {
                double threshold = oldInstance.attribute(attrIndex);
                Instance newInstance = oldInstance.deleteAttribute(attrIndex);
                if (threshold <= splitPoint) {
                    ltDataSet.add(newInstance);
                } else {
                    gtDataSet.add(newInstance);
                }
            }
            subDataSets.put(-1.0, ltDataSet);
            subDataSets.put(1.0, gtDataSet);
        } else {
            for (double threshold: splitRecord.thresholds) {
                subDataSets.put(threshold, new StandardDataSet(attributeInfoList, dataset.classInfo()));
            }
            for (Instance oldInstance: dataset) {
                double threshold = oldInstance.attribute(attrIndex);
                DataSet subDataSet = subDataSets.get(threshold);
                subDataSet.add(oldInstance.deleteAttribute(attrIndex));
            }
        }
        return subDataSets;
    }

    private static class GainRatio extends SplitRecord {

        private double splitPoint; // continuous node

        private SortedSet<Double> thresholds; // discrete node
    }

    @Override
    protected GainRatio selectBestFeature(DataSet dataset) {
        GainRatio res = new GainRatio();
        TreeMap<Double, Double> classDistMap = DataSets.columnDistMap(dataset, -1); // 类的分布
        double sumWeightedNumber = DataSets.sumWeight(dataset);
        double classEnt = this.computeImpurity(dataset);
        int dimension = dataset.dimensionality(); // 数据集中特征个数

        // 计算每个属性的信息增益，以此选择信息增益高于平均值的计算信息增益率
        // 保存每个特征下的分布，因为若是连续属性，会修改分布情况
        List<TreeMap<Double, Double>> distMaps = new ArrayList<>(dimension);
        for (int i = 0; i < dimension; i++) {
            distMaps.add(DataSets.columnDistMap(dataset, i));
        }
        double[] infoGains = new double[dimension]; // 保存每个特征的信息增益
        Map<Integer, Double> splitPointMap = new HashMap<>(); // 保存连续特征的最优划分点，key: 特征索引, value: 划分点的值
        // 开始计算信息增益
        for (int i = 0; i < dimension; i++) {
            // class在对应属性下的条件分布
            TreeMap<Double, TreeMap<Double, Double>> conDistMap = DataSets.colConDistMap(dataset, i, -1);
            if (dataset.attributeInfo(i).continuous()) { // 当前属性为连续的
                // 寻找最优划分点
                double bestSplitPoint = 0; //  最优划分点
                double maxGain = -1; // 最优先划分点对应的信息增益
                if (conDistMap.size() == 1) { // 连续属性的取值却只有1个
                    bestSplitPoint = conDistMap.keySet().iterator().next();
                    maxGain = 0; // 此时无任何帮助
                    infoGains[i] = maxGain; // 保存最大信息增益
                    splitPointMap.put(i, bestSplitPoint); // 保存划分点
                } else {
                    double[] splitDistArray = new double[2]; // 最优划分下，属性值的分布：小于和大于的数量
                    double[] splitKeys = new double[2]; // 最优划分下，a_i 和 a_{i+1} 的值
                    Map<Double, Double> ltConDistMap = new TreeMap<>(); // 属性值小于最优划分点的条件下class分布
                    Map<Double, Double> gtConDistMap = new TreeMap<>(); // 属性值大于最优划分点的条件下class分布
                    // 初始化 ltConDistMap, gtConDistMap
                    for (Double clazz: classDistMap.keySet()) {
                        ltConDistMap.put(clazz, 0.0);
                        gtConDistMap.put(clazz, 0.0);
                    }
                    // 遍历 N-1 个可能的划分点
                    Iterator<Map.Entry<Double, TreeMap<Double, Double>>> iter1 = conDistMap.entrySet().iterator();
                    Iterator<Map.Entry<Double, TreeMap<Double, Double>>> iter2 = conDistMap.entrySet().iterator();
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
                        double ltEnt = 0;
                        for (double weightedNumber: ltConDistMap.values()) {
                            if (weightedNumber == 0) continue;
                            ltWeightedNumber += weightedNumber;
                            ltEnt -= weightedNumber * DataUtil.log2(weightedNumber);
                        }
                        ltEnt = ltEnt / ltWeightedNumber + DataUtil.log2(ltWeightedNumber);
                        double gtEnt = 0;
                        for (double weightedNumber: gtConDistMap.values()) {
                            if (weightedNumber == 0) continue;
                            gtEnt -= weightedNumber * DataUtil.log2(weightedNumber);
                        }
                        double gtWeightedNumber = sumWeightedNumber - ltWeightedNumber;
                        gtEnt = gtEnt / gtWeightedNumber + DataUtil.log2(gtWeightedNumber);
                        double infoGain = classEnt - ltWeightedNumber/sumWeightedNumber*ltEnt - gtWeightedNumber/sumWeightedNumber*gtEnt;
                        if (infoGain > maxGain) {
                            maxGain = infoGain;
                            bestSplitPoint = splitPoint;
                            splitDistArray[0] = ltWeightedNumber;
                            splitDistArray[1] = gtWeightedNumber;
                            splitKeys[0] = a1;
                            splitKeys[1] = a2;
                        }
                    }
                    infoGains[i] = maxGain; // 保存最大信息增益
                    splitPointMap.put(i, bestSplitPoint); // 保存划分点
                    TreeMap<Double, Double> newDistMap = new TreeMap<>(); // 新的分布
                    newDistMap.put(splitKeys[0], splitDistArray[0]);
                    newDistMap.put(splitKeys[1], splitDistArray[1]);
                    distMaps.set(i, newDistMap); // 更新划分后的分布
                }
            } else { // 离散属性
                double thisSplitEntropy = 0;
                for (Map<Double, Double> distMap: conDistMap.values()) {
                    double subWeightedNumber = 0; // 子集样本大小
                    double subEntropy = 0; // 子集的熵
                    for (double weightedNumber: distMap.values()) {
                        if (weightedNumber == 0) continue;
                        subWeightedNumber += weightedNumber;
                        subEntropy -= (weightedNumber * DataUtil.log2(weightedNumber));
                    }
                    subEntropy = subEntropy / subWeightedNumber + DataUtil.log2(subWeightedNumber);
                    thisSplitEntropy += subWeightedNumber * subEntropy;
                }
                thisSplitEntropy /= sumWeightedNumber;
                infoGains[i] = classEnt - thisSplitEntropy;
            }
        }
        double avgInfoGain = ArrayUtil.sum(infoGains) / infoGains.length; // 平均信息增益
        // 选择信息增益大于平均的特征计算信息增益率
        double maxGainRatio = -1;
        int attrIndex = -1;
        for (int i = 0; i < dimension; i++) {
            if (infoGains[i] >= avgInfoGain) {
                TreeMap<Double, Double> distMap = distMaps.get(i);
                double splitInfo = 0;
                for (double eachWeightedNumber: distMap.values()) {
                    double p_i = eachWeightedNumber / sumWeightedNumber;
                    splitInfo -= p_i * DataUtil.log2(p_i);
                }
                double gainRatio = infoGains[i] / splitInfo;
                if (gainRatio > maxGainRatio) {
                    maxGainRatio = gainRatio;
                    attrIndex = i;
                }
            }
        }
        res.feature= attrIndex;
        res.impurity = classEnt;
        res.improvement = sumWeightedNumber / this.weightedNSamples * infoGains[attrIndex];
        if (dataset.attributeInfo(attrIndex).continuous()) {
            res.splitPoint = splitPointMap.get(attrIndex);
        } else {
            res.thresholds = dataset.attrValueSet(attrIndex);
        }
        return res;
    }

    /**
     * Computes the entropy of given dataset.
     * C45Tree use "entropy" to estimate the impurity.
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
        for (double eachWeightedNumber: classDistMap.values()) {
            totalWeightedNumber += eachWeightedNumber;
        }
        double entropy = 0;
        for (double eachWeightedNumber: classDistMap.values()) {
            double p_i = eachWeightedNumber / totalWeightedNumber;
            entropy -= p_i * DataUtil.log2(p_i);
        }
        return entropy;
    }


    private abstract static class C45Node extends DTNode {

        protected C45Node child;

        protected C45Node brother;

        @Override
        protected void addChild(DTNode child) {
            C45Node node = (C45Node) child;
            if (this.child == null) {
                this.child = node;
            } else {
                C45Node oldChild = this.child;
                while (oldChild.brother != null) {
                    oldChild = oldChild.brother;
                }
                oldChild.brother = node;
            }
        }

        @Override
        protected boolean isLeaf() {
            return this.child == null;
        }

        @Override
        protected Iterator<C45Node> children() {
            if (this.isLeaf()) {
                return new Iterator<C45Node>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public C45Node next() {
                        return null;
                    }
                };
            }

            return new Iterator<C45Node>() {
                private C45Node next = child;

                @Override
                public boolean hasNext() {
                    return next != null;
                }

                @Override
                public C45Node next() {
                    C45Node oldNext = next;
                    next = next.brother;
                    return oldNext;
                }
            };
        }
    }

    private static class ContinuousC45Node extends C45Node {

        private final double splitPoint;

        public ContinuousC45Node(double splitPoint) {
            this.splitPoint = splitPoint;
        }

        @Override
        protected void toLeaf() {
            this.child = null;
        }

        @Override
        protected C45Node matchChild(double value) {
            Iterator<C45Node> children = this.children();
            C45Node child = children.next(); // 左子树
            if (value > this.splitPoint) { // 右子树
                child = children.next();
            }
            return child;
        }

        @Override
        protected String edgeString() {
            return "[ [<=" + splitPoint + "] [>" + splitPoint + "] ]";
        }

        @Override
        public ContinuousC45Node copy() {
            ContinuousC45Node node = new ContinuousC45Node(this.splitPoint);
            node.feature = this.feature;
            node.clazz = this.clazz;
            node.depth = this.depth;
            node.impurity = this.impurity;
            node.nNodeSamples = this.nNodeSamples;
            node.weightedNNodeSamples = this.weightedNNodeSamples;
            return node;
        }
    }

    private static class DiscreteC45Node extends C45Node {

        private ArrayList<Double> edgeValues;

        public DiscreteC45Node() {
            this.edgeValues = new ArrayList<>(2);
        }

        private void addEdgeValue(double edgeValue) {
            this.edgeValues.add(edgeValue);
        }

        @Override
        protected void toLeaf() {
            this.child = null;
            this.edgeValues = null; // let GC work.
        }

        @Override
        protected C45Node matchChild(double value) {
            Iterator<C45Node> children = this.children();
            C45Node child;
            for (Double edgeValue : edgeValues) {
                child = children.next();
                if (edgeValue == value) {
                    return child;
                }
            }
            return null;
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
        public DiscreteC45Node copy() {
            DiscreteC45Node node = new DiscreteC45Node();
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
