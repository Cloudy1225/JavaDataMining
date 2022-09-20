package test.java.classify;

import main.java.classify.bayes.GaussianNB;
import main.java.classify.bayes.MultinomialNB;
import main.java.classify.bayes.NaiveBayesClassifier;
import main.java.core.*;
import main.java.utils.io.FileTool;
import org.junit.Test;

import java.util.Arrays;

/**
 * Tests naive bayes classifiers.
 *
 * @author Cloudy1225
 * @see NaiveBayesClassifier
 */
public class NaiveBayesClassifierTest {

    @Test
    public void testGaussianNB() {
        DataSet iris = FileTool.loadIris();
        GaussianNB clf = new GaussianNB();
        clf.fit(iris);
        DenseInstance instance = new DenseInstance(new double[] {5.5, 2.4, 3.7, 1.0});
        clf.predict(instance);
        ClassifierTestUtil.crossValidation(clf, iris, 5);

        DataSet breastCancer = FileTool.loadBreastCancer();
        clf.fit(breastCancer);
        DenseInstance instance1 = new DenseInstance(new double[] {
                17.99,10.38,122.8,1001,0.1184,0.2776,0.3001,0.1471,0.2419,0.07871,1.095,0.9053,8.589,153.4,0.006399,0.04904,0.05373,0.01587,0.03003,0.006193,25.38,17.33,184.6,2019,0.1622,0.6656,0.7119,0.2654,0.4601,0.1189
        });
        clf.predict(instance1);
        ClassifierTestUtil.crossValidation(clf, breastCancer, 5);
    }

    @Test
    public void testMultinomialNB() {
        AttributeInfo info0 = new AttributeInfo("索引0", true, 0);
        AttributeInfo info1 = new AttributeInfo("索引1", true, 1);
        AttributeInfo classInfo = new AttributeInfo("类标记", false, -1);
        StandardDataSet dataset = new StandardDataSet(new AttributeInfo[] {info0, info1}, classInfo);
        DenseInstance[] instances = new DenseInstance[] { // 《统计学习方法》李航，P63
                new DenseInstance(new double[]{1, 1}, -1),
                new DenseInstance(new double[]{1, 2}, -1),
                new DenseInstance(new double[]{1, 2}, 1),
                new DenseInstance(new double[]{1, 1}, 1),
                new DenseInstance(new double[]{1, 1}, -1),
                new DenseInstance(new double[]{2, 1}, -1),
                new DenseInstance(new double[]{2, 2}, -1),
                new DenseInstance(new double[]{2, 2}, 1),
                new DenseInstance(new double[]{2, 3}, 1),
                new DenseInstance(new double[]{2, 3}, 1),
                new DenseInstance(new double[]{3, 3}, 1),
                new DenseInstance(new double[]{3, 2}, 1),
                new DenseInstance(new double[]{3, 2}, 1),
                new DenseInstance(new double[]{3, 3}, 1),
                new DenseInstance(new double[]{3, 3}, -1),
        };
        dataset.addAll(Arrays.asList(instances));
        MultinomialNB clf = new MultinomialNB();
        clf.fit(dataset);
        DenseInstance test = new DenseInstance(new double[] {2, 1});
        System.out.println(clf.predict(test));
    }
}
