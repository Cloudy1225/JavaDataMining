package main.java.preprocessing.weight;

import main.java.core.DataSet;
import main.java.core.Instance;
import main.java.core.WeightHandler;
import main.java.utils.ArrayUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * This class provides some static utility methods to estimate weights for unbalanced datasets.
 * <p>
 * Estimates class weights for unbalanced datasets.
 * </p>
 * <p>
 * Estimates sample weights by class for unbalanced datasets.
 * </p>
 *
 * @author Cloudy1225
 */
public class ClassWeightUtil implements WeightHandler {

    /**
     * Sets each instance's weight.
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
    public static void setWeight(DataSet dataset, Map<Double, Double> classWeight, double[] sampleWeight) {
        // 设置每个instance的权重
        // instanceWeight = classWeight * sampleWeight
        if (classWeight == null) { // 每个class权重为1
            if (sampleWeight == null) { // 每个sample权重为1
                // 1 * 1
                for (Instance instance : dataset) {
                    instance.setWeight(1.0);
                }
            } else {
                // 1 * sampleWeight
                if (dataset.size() != sampleWeight.length) {
                    String msg = "sampleWeight.length = " + sampleWeight.length + ", expected " + dataset.size();
                    throw new IllegalArgumentException(msg);
                }
                for (int i = 0; i < sampleWeight.length; i++) {
                    Instance instance = dataset.instance(i);
                    instance.setWeight(sampleWeight[i]);
                }
            }
        } else {
            if (sampleWeight == null) {
                // classWeight * 1
                for (Instance instance: dataset) {
                    double clazz = instance.classValue();
                    Double clazzWeight = classWeight.get(clazz);
                    if (clazzWeight == null) { // 若给定权重未出现该类，默认为1.0
                        clazzWeight = 1.0;
                    }
                    instance.setWeight(clazzWeight);
                }
            } else {
                // classWeight * sampleWeight
                for (int i = 0; i < sampleWeight.length; i++) {
                    Instance instance = dataset.instance(i);
                    double clazz = instance.classValue();
                    Double clazzWeight = classWeight.get(clazz);
                    if (clazzWeight == null) { // 若给定权重未出现该类，默认为1.0
                        clazzWeight = 1.0;
                    }
                    instance.setWeight(clazzWeight * sampleWeight[i]);
                }
            }
        }
    }

    /**
     * Estimates balanced class weights for unbalanced datasets.
     * "balanced": class weights will be given like this:
     * <p>
     *     input: [2, 2, 4, 1, 1, 1]
     * </p>
     * <p>
     *     nSamples = input.length = 6 (number of samples)
     *     nClasses = 3 (number of classes)
     * </p>
     * <p>
     *     for class 1:
     *     weight = nSamples / (nClasses * 3) = 6/9 = 0.666666...
     * </p>
     * <p>
     *     for class 2:
     *     weight = nSamples / (nClasses * 2) = 6/6 = 1.0
     * </p>
     * <p>
     *     for class 4:
     *     weight = nSamples / (nClasses * 1) = 6/3 = 2.0
     * </p>
     * <p>
     *     output: {1: 0.666666..., 2: 1.0, 4: 2.0}, it is sorted.
     * </p>
     *
     * @param classList an array contains the class of each sample in unbalanced datasets
     * @return <tt>TreeMap<Double, Double></tt>: a sorted map, key is the class, value is the weight
     */
    public static TreeMap<Double, Double> computeBalancedClassWeight(double[] classList) {
        TreeMap<Double, Double> res = new TreeMap<>(); // 键是类标签，值是对应的权重
        TreeMap<Double, Integer> classDistMap = ArrayUtil.countDistMap(classList); // 类分布Map，记录每个类的数量
        int nSamples = classList.length; // 样本总数
        int nClasses = classDistMap.size(); // 类标记种数
        for (Map.Entry<Double, Integer> entry: classDistMap.entrySet()) {
            double weight = ((double) nSamples) / (nClasses * entry.getValue()); // 样本数 / (类种数 * 取值为该类的样本数量)
            res.put(entry.getKey(), weight);
        }
        return res;
    }

    /**
     * Estimates sample weights by class for unbalanced datasets with given classWeight.
     * The weight of sample whose class is not in given "classWeight" will be set 1 .
     * <p>
     *     input: {3:4, 2:1, 1:5}, [2, 2, 3, 1, 1]
     *     output: [1, 1, 4, 5, 5]
     * </p>
     * <p>
     *     input: {2:1, 1:5}, [2, 2, 3, 1, 1]
     *     output: [1, 1, 1, 5, 5]
     * </p>
     *
     * @param classWeight weights associated with classes in the form {class_label: weight}
     * @param classList array of original class labels per sample.
     * @return array with sample weights as applied to the original y.
     */
    public static double[] computeSampleWeight(Map<Double, Double> classWeight, double[] classList) {
        double[] res = new double[classList.length];
        for (int i = 0; i < classList.length; i++) {
            Double weight = classWeight.get(classList[i]);
            if (weight == null) {
                res[i] = 1;
            } else {
                res[i] = classList[i];
            }
        }
        return res;
    }

}
