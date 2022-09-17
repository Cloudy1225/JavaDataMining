package main.java.classify.evaluation;

import main.java.classify.Classifier;
import main.java.core.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of the cross-validation evaluation technique.
 *
 * @author Cloudy1225
 * @see Classifier
 */
public class CrossValidation implements Evaluatable{

    private Classifier classifier;

    /**
     * Creates a cross-validation to evaluate the specified classifier.
     *
     * @param classifier given classifier
     */
    public CrossValidation(Classifier classifier) {
        this.classifier = classifier;
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
            classifier.fit(training);
            for (Instance instance: validation) {
                double prediction = classifier.classify(instance);
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


}
