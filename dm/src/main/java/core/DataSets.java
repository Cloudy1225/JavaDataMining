package main.java.core;

import java.util.*;

/**
 * This class contains various static methods for creating, manipulating, and modifying datasets.
 *
 * @author Cloudy1225
 * @see DataSet
 * @see StandardDataSet
 */
public class DataSets {

    /**
     * Prints the given data sets. ({@code System.put.println)})
     *
     * @param dataSets a {@link DataSet} array
     */
    public static void print(DataSet[] dataSets) {
        for (DataSet dataset: dataSets) {
            System.out.println(dataset);
        }
    }

    /**
     * Returns a number of folds from the source data set.
     * Instances of the data set will be randomly and averagely allocated.
     *
     * @param src the source data set
     * @param k the number of folds, >= 1
     * @return an array containing each fold data set
     */
    public static DataSet[] folds(DataSet src, int k) {
        StandardDataSet[] res = new StandardDataSet[k];
        int srcSize = src.size(); // 源数据集中实例数量
        List<Integer> indices = new ArrayList<>(srcSize);
        for (int i = 0; i < srcSize; i++) {
            indices.add(i);
        }
        int subSize = (srcSize / k) + 1; // 子数据集中实例数量
        int[][] subIndices = new int[k][subSize]; // 每个子数据集中实例所对应在源数据中的索引
        Random random = new Random();
        for (int i = 0; i < subSize; i++) {
            for (int j = 0; j < k; j++) {
                if (indices.size() > 0)
                    subIndices[j][i] = indices.remove(random.nextInt(indices.size()));
                else
                    subIndices[j][i] = -1;
            }
        }
        for (int i = 0; i < k; i++) {
            int[] subIndex;
            if (subIndices[i][subSize - 1] == -1) { // 处理源数据集中实例数量不能整除k的情况
                subIndex = new int[subSize - 1];
                System.arraycopy(subIndices[i], 0, subIndex, 0, subSize - 1);
            } else {
                subIndex = new int[subSize];
                System.arraycopy(subIndices[i], 0, subIndex, 0, subSize);
            }
            StandardDataSet subDataSet = new StandardDataSet(src.attributeInfoList(), src.classInfo());
            for (int index: subIndex) {
                subDataSet.add(src.instance(index));
            }
            res[i] = subDataSet;
        }
        return res;
    }

    /**
     * Splits the source dataset into random train and test sub-dataset.
     *
     * @param src the source dataset
     * @param testRate the proportion of the instances to include in the test split
     * @return an array containing train-test split of inputs,
     * array[0] is the train set when array[1] is the test set
     */
    public static DataSet[] trainTestSplit(DataSet src, double testRate) {
        List<AttributeInfo> attributeInfoList = src.attributeInfoList();
        AttributeInfo classInfo = src.classInfo();
        StandardDataSet train = new StandardDataSet(attributeInfoList, classInfo);
        StandardDataSet test = new StandardDataSet(attributeInfoList, classInfo);
        int srcSize = src.size();
        int testSize = (int) (srcSize * testRate);
        List<Integer> trainIndices = new ArrayList<>(srcSize);
        for (int i = 0; i < srcSize; i++) {
            trainIndices.add(i);
        }
        int[] testIndices = new int[testSize];
        Random random = new Random();
        for (int i = 0; i < testSize; i++) {
            testIndices[i] = trainIndices.remove(random.nextInt(trainIndices.size()));
        }
        for (int i: testIndices) {
            test.add(src.instance(i));
        }
        for (int i: trainIndices)  {
            train.add(src.instance(i));
        }
        return new StandardDataSet[] {train, test};
    }


    /**
     * Returns the weighted distribution of a specified attribute in a dataset.
     * Computes the weighted number of each unique value for the specified attribute in a dataset.
     * If given index is -1, this will return the weighted number of each unique class.
     * This is different from {@link main.java.utils.ArrayUtil#countDistMap(double[])}.
     *
     * @param dataset given dataset
     * @param index [0, dimensionality-1]: the index of the specified attribute or -1: the index of class
     * @return a sorted <tt>TreeMap</tt>, key is the unique value of the attribute, value is the weighted number
     * @throws IllegalArgumentException if given index is less than -1
     */
    public static TreeMap<Double, Double> columnDistMap(DataSet dataset, int index) {
        if (index < -1) {
            throw new IllegalArgumentException("Given index must be greater than -1.");
        }
        TreeMap<Double, Double> distribution = new TreeMap<>();
        if (index == -1) { // 计算类的加权分布
            for (Instance instance: dataset) {
                double clazz = instance.classValue();
                double weight = instance.getWeight();
                Double sumWeight = distribution.get(clazz);
                if (sumWeight == null) { // 说明Map之前没有这个clazz作为键
                    sumWeight = weight;
                } else {
                    sumWeight += weight;
                }
                distribution.put(clazz, sumWeight);
            }
        } else { // 计算指定属性的加权分布
            for (Instance instance: dataset) {
                double attrValue = instance.attribute(index);
                double weight = instance.getWeight();
                Double sumWeight = distribution.get(attrValue);
                if (sumWeight == null) { // 说明Map之前没有这个attrValue作为键
                    sumWeight = weight;
                } else {
                    sumWeight += weight;
                }
                distribution.put(attrValue, sumWeight);
            }
        }
        return distribution;
    }

    /**
     * Returns the weighted conditional distribution of a specified attribute in a dataset.
     * @param dataset given dataset
     * @param conIndex the index of conditional attribute: [-1, dimensionality-1]
     * @param distIndex the index of target attribute: [-1, dimensionality-1]
     * @return {@code TreeMap<Double, TreeMap<Double, Double>}
     */
    public static TreeMap<Double, TreeMap<Double, Double>> colConDistMap(DataSet dataset, int conIndex, int distIndex) {
        if (conIndex < -1 || distIndex < -1) {
            throw new IllegalArgumentException("Given index must be greater than -1.");
        }
        if (conIndex == distIndex) {
            throw new IllegalArgumentException("conIndex can't equal distIndex");
        }
        TreeMap<Double, TreeMap<Double, Double>> distribution = new TreeMap<>();
        if (conIndex != -1) {
            if (distIndex == -1) { // 类相对属性的条件分布
                SortedSet<Double> classSet = dataset.classSet(); // 类的所有取值
                for (Instance instance: dataset) {
                    double conAttrValue = instance.attribute(conIndex); // 条件属性的值
                    if (!distribution.containsKey(conAttrValue)) {
                        TreeMap<Double, Double> subDistribution = new TreeMap<>();
                        for (Double classValue: classSet) {
                            subDistribution.put(classValue, 0.0);
                        }
                        distribution.put(conAttrValue, subDistribution);
                    }
                    TreeMap<Double, Double> subDistribution = distribution.get(conAttrValue);
                    double classValue = instance.classValue();
                    double weight = subDistribution.get(classValue) + instance.getWeight();
                    subDistribution.put(classValue, weight);
                }
            }
            else { // 属性相对其他属性的条件分布
                SortedSet<Double> attrValueSet = dataset.attrValueSet(distIndex);
                for (Instance instance: dataset) {
                    double conAttrValue = instance.attribute(conIndex);
                    if (!distribution.containsKey(conAttrValue)) {
                        TreeMap<Double, Double> subDistribution = new TreeMap<>();
                        for (Double attrValue: attrValueSet) {
                            subDistribution.put(attrValue, 0.0);
                        }
                        distribution.put(conAttrValue, subDistribution);
                    }
                    TreeMap<Double, Double> subDistribution = distribution.get(conAttrValue);
                    double distAttrValue = instance.attribute(distIndex);
                    double weight = subDistribution.get(distAttrValue) + instance.getWeight();
                    subDistribution.put(distAttrValue, weight);
                }
            }
        } else { // 类作为条件
            SortedSet<Double> attrValueSet = dataset.attrValueSet(distIndex);
            for (Instance instance: dataset) {
                double conClassValue = instance.classValue();
                if (!distribution.containsKey(conClassValue)) {
                    TreeMap<Double, Double> subDistribution = new TreeMap<>();
                    for (Double attrValue: attrValueSet) {
                        subDistribution.put(attrValue, 0.0);
                    }
                    distribution.put(conClassValue, subDistribution);
                }
                TreeMap<Double, Double> subDistribution = distribution.get(conClassValue);
                double distAttrValue = instance.attribute(distIndex);
                double weight = subDistribution.get(distAttrValue) + instance.getWeight();
                subDistribution.put(distAttrValue, weight);
            }
        }
        return distribution;
    }

    /**
     * Computes the weighted number of a dataset.
     *
     * W = \sum_{i in dataset} w_i
     *
     * @param dataset given dataset
     * @return the sum of each instance's weight in the given dataset
     */
    public static double sumWeight(DataSet dataset) {
        double sum = 0;
        for (Instance instance: dataset) {
            sum += instance.getWeight();
        }
        return sum;
    }
}