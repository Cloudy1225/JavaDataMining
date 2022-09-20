package test.java.classify;

import main.java.classify.Classifier;
import main.java.classify.decisionTree.*;
import main.java.classify.evaluation.PerformanceMeasure;
import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.Instance;
import main.java.utils.io.FileTool;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

/**
 * Tests decision tree classifiers: ID3, C4.5, CART.
 *
 * @author Cloudy1225
 * @see DecisionTreeClassifier
 */
public class DecisionTreeClassifierTest {

    @Test
    public void testID3() {
        DataSet dataset = FileTool.loadTicTacToe();
        System.out.println("{");
        System.out.println("使用ID3算法，预测离散数据：");
        System.out.println("使用数据集：tic-tac-toe.csv");

        DecisionTree tree = new ID3Tree();
        tree.fit(dataset);
        System.out.println("决策树");
        tree.print();

        // 使用交叉验证
        DecisionTreeClassifier classifier = new DecisionTreeClassifier("infoGain");
        this.crossValidation(classifier, dataset);
        System.out.println("}");
    }

    @Test
    public void testC45_Iris() {
        DataSet dataset = FileTool.loadIris();
        System.out.println("{");
        System.out.println("使用C4.5算法，预测连续数据：");
        System.out.println("使用数据集：iris.csv");

        System.out.println("限制树深为4，进行预剪枝");
        DecisionTree tree = new C45Tree(4, 2, 1, 0, 0);
        tree.fit(dataset);
        System.out.println("决策树");
        tree.print();

        // 使用交叉验证
        DecisionTreeClassifier classifier = new DecisionTreeClassifier("gainRatio", 4,2,1,0,0);
        this.crossValidation(classifier, dataset);
        System.out.println("}");
    }

    @Test
    public void testC45_TicTacToe() {
        DataSet dataset = FileTool.loadTicTacToe();
        System.out.println("{");
        System.out.println("使用C45算法，预测离散数据：");
        System.out.println("使用数据集：tic-tac-toe.csv");

        DecisionTree tree = new C45Tree();
        tree.fit(dataset);
        System.out.println("决策树");
        tree.print();

        // 使用交叉验证
        DecisionTreeClassifier classifier = new DecisionTreeClassifier("gainRatio");
        this.crossValidation(classifier, dataset);
        System.out.println("}");
    }

    @Test
    public void testCart_Iris() {
        DataSet dataset = FileTool.loadIris();
        System.out.println("{");
        System.out.println("使用Cart算法，预测连续数据：");
        System.out.println("使用数据集：iris.csv");

        System.out.println("设置ccpAlpha为0.1，进行后剪枝");
        DecisionTree tree = new CartTree(100,2,1,0,0.1);
        tree.fit(dataset);
        System.out.println("决策树");
        tree.print();

        // 使用交叉验证
        DecisionTreeClassifier classifier = new DecisionTreeClassifier("gini",100,2,1,0,0.1);
        this.crossValidation(classifier, dataset);
        System.out.println("}");
    }

    @Test
    public void testCart_TicTacToe() {
        DataSet dataset = FileTool.loadTicTacToe();
        System.out.println("{");
        System.out.println("使用Cart算法，预测离散数据：");
        System.out.println("使用数据集：tic-tac-toe.csv");

        DecisionTree tree = new CartTree();
        tree.fit(dataset);
        System.out.println("决策树");
        tree.print();

        // 使用交叉验证
        DecisionTreeClassifier classifier = new DecisionTreeClassifier("gini");
        this.crossValidation(classifier, dataset);
        System.out.println("}");
    }

    @Test
    public void testCart_BreastCancer() {
        DataSet dataset = FileTool.loadBreastCancer();

        DataSet[] trainAndTest = DataSets.trainTestSplit(dataset, 0.1);
        DataSet train = trainAndTest[0];
        DataSet test = trainAndTest[1];

        System.out.println("{");
        System.out.println("使用Cart算法，预测连续数据：");
        System.out.println("使用数据集：breast_cancer.csv");

        System.out.println("剪枝前");
        DecisionTree tree = new CartTree();
        tree.fit(train);
        System.out.println("决策树");
        tree.print();
        this.evaluate(tree, test);

        System.out.println();

        System.out.println("根据ccpAlpha剪枝后");
        DecisionTree tree1 = new CartTree(100,2,1,0,0.1);
        tree1.fit(train);
        System.out.println("决策树");
        tree1.print();
        this.evaluate(tree1, test);
        System.out.println("}");
    }

    /**
     * k-折交叉验证并打印结果
     */
    private void crossValidation(Classifier classifier, DataSet dataset) {
        ClassifierTestUtil.crossValidation(classifier, dataset, 5);
    }

    private void evaluate(DecisionTree tree, DataSet test) {
        Map<Double, PerformanceMeasure> performance = new TreeMap<>();
        for (double clazz: test.classSet()) {
            performance.put(clazz, new PerformanceMeasure());
        }
        for (Instance instance: test) {
            double prediction = tree.predict(instance);
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
    }
}
