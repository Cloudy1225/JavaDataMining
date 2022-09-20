package test.java.classify.decisionTree;

import main.java.classify.Classifier;
import main.java.classify.evaluation.CrossValidation;
import main.java.classify.evaluation.PerformanceMeasure;
import main.java.core.DataSet;

import java.util.Map;

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
}
