package test.java.classify;

import main.java.classify.Classifier;
import main.java.classify.evaluation.CrossValidation;
import main.java.classify.evaluation.PerformanceMeasure;
import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.Instance;

import java.util.Map;
import java.util.TreeMap;

public class ClassifierTestUtil {

    /**
     * K-折交叉验证指定分类器，打印并返回验证结果
     *
     * @param classifier 分类器
     * @param dataset 数据集
     * @param k 折数
     * @return 结果
     */
    public static Map<Double, PerformanceMeasure> crossValidation(Classifier classifier, DataSet dataset, int k) {
        CrossValidation cv = new CrossValidation(classifier);
        Map<Double, PerformanceMeasure> performance = cv.crossValidation(dataset, k);
        System.out.println("混淆矩阵");
        System.out.println(performance);
        System.out.println("精确率(预测为正，有多少是真的正）");
        for (PerformanceMeasure p: performance.values()) {
            System.out.println(p.getPrecision());
        }
        System.out.println("召回率（有多少正被预测出来）");
        for (PerformanceMeasure p: performance.values()) {
            System.out.println(p.getRecall());
        }
        return performance;
    }

    /**
     * 将数据集分为训练集与测试集。
     * 利用训练集建立模型，测试集用来测试
     *
     * @param classifier 分类器
     * @param dataset 数据集
     * @param testRate 测试集占比
     * @return 结果
     */
    public static Map<Double, PerformanceMeasure> trainTestEvaluate(Classifier classifier, DataSet dataset, double testRate) {
        Map<Double, PerformanceMeasure> performance = new TreeMap<>();
        DataSet[] subDataSets = DataSets.trainTestSplit(dataset, testRate);
        DataSet train = subDataSets[0];
        DataSet test = subDataSets[1];
        for (double clazz: train.classSet()) {
            performance.put(clazz, new PerformanceMeasure());
        }
        classifier.fit(train);
        for (Instance instance: test) {
            double prediction = classifier.classify(instance);
            double actual = instance.classValue();
            double weight = instance.getWeight();
            if (prediction == actual) { // 预测正确
                for (double clazz: performance.keySet()) {
                    if (clazz == actual) { // 本来为正，预测为正
                        performance.get(clazz).TP += weight;
                    } else { // 本来为负，预测为负
                        performance.get(clazz).TN += weight;
                    }
                }
            } else {
                for (double clazz: performance.keySet()) {
                    if (clazz == actual) { // 本来为正，预测成负
                        performance.get(clazz).FN += weight;
                    } else if (clazz == prediction) { // 本来为负，预测为正
                        performance.get(clazz).FP += weight;
                    } else { // 本来为负，预测为负
                        performance.get(clazz).TN += weight;
                    }
                }
            }
        }
        System.out.println("混淆矩阵");
        System.out.println(performance);
        System.out.println("精确率(预测为正，有多少是真的正）");
        for (PerformanceMeasure p: performance.values()) {
            System.out.println(p.getPrecision());
        }
        System.out.println("召回率（有多少正被预测出来）");
        for (PerformanceMeasure p: performance.values()) {
            System.out.println(p.getRecall());
        }
        System.out.println("正确率");
        for (PerformanceMeasure p: performance.values()) {
            System.out.println(p.getAccuracy());
        }
        return performance;
    }
}
