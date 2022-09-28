package test.java.classify;

import main.java.classify.neighbors.*;
import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.DenseInstance;
import main.java.metrics.distance.EuclideanDistance;
import main.java.metrics.distance.SEuclideanDistance;
import main.java.utils.io.FileTool;
import org.junit.Test;

/**
 * Tests nearest neighbors classifiers.
 *
 * @author Cloudy1225
 * @see KNeighborsClassifier
 * @see RadiusNeighborsClassifier
 */
public class NeighborsClassifierTest {

    @Test
    public void testBruteKNN() {
        DataSet iris = FileTool.loadIris();
        BruteKNNClassifier clf = new BruteKNNClassifier();
        System.out.println("获取给定实例最近的k个邻居：");
        clf.fit(iris);
        DenseInstance instance = new DenseInstance(new double[] {5.5, 2.4, 3.7, 1.0});
        System.out.println(clf.kNeighbors(instance, 5));
        System.out.println();
        System.out.println("数据集划分为测试集与训练集：");
        ClassifierTestUtil.trainTestEvaluate(clf, iris, 0.2);
        System.out.println();
        System.out.println("交叉验证：");
        ClassifierTestUtil.crossValidation(clf, iris, 5);
    }

    @Test
    public void testKDTree() {
        DataSet iris = FileTool.loadIris();
        KDTree tree = new KDTree();
        tree.buildTree(iris);
        DenseInstance instance = new DenseInstance(new double[] {5.5, 2.4, 3.7, 1.0});
        double[] var = DataSets.var(iris);
        SEuclideanDistance metric = new SEuclideanDistance(var);
        System.out.println(tree.query(instance, 5, metric));
        EuclideanDistance metric1 = new EuclideanDistance();
        System.out.println(tree.queryRadius(instance, 1.0, metric1));
    }

    @Test
    public void testKDTreeKNN() {
        DataSet iris = FileTool.loadIris();
        KDTreeKNNClassifier clf = new KDTreeKNNClassifier();
        System.out.println("获取给定实例最近的k个邻居：");
        clf.fit(iris);
        DenseInstance instance = new DenseInstance(new double[] {5.5, 2.4, 3.7, 1.0});
        System.out.println(clf.kNeighbors(instance, 5));
        System.out.println();
        System.out.println("数据集划分为测试集与训练集：");
        ClassifierTestUtil.trainTestEvaluate(clf, iris, 0.2);
        System.out.println();
        System.out.println("交叉验证：");
        ClassifierTestUtil.crossValidation(clf, iris, 5);
    }

    @Test
    public void testBruteRNN() {
        DataSet iris = FileTool.loadIris();
        BruteRNNClassifier clf = new BruteRNNClassifier();
        System.out.println("获取给定实例距离radius(1.0)范围内的邻居：");
        clf.fit(iris);
        DenseInstance instance = new DenseInstance(new double[] {5.5, 2.4, 3.7, 1.0});
        System.out.println(clf.radiusNeighbors(instance, 1.0));
        System.out.println();
        System.out.println("数据集划分为测试集与训练集：");
        ClassifierTestUtil.trainTestEvaluate(clf, iris, 0.2);
        System.out.println();
        System.out.println("交叉验证：");
        ClassifierTestUtil.crossValidation(clf, iris, 5);
    }

    @Test
    public void testKDTreeRNN() {
        DataSet iris = FileTool.loadIris();
        KDTreeRNNClassifier clf = new KDTreeRNNClassifier();
        System.out.println("获取给定实例距离radius(1.0)范围内的邻居：");
        clf.fit(iris);
        DenseInstance instance = new DenseInstance(new double[] {5.5, 2.4, 3.7, 1.0});
        System.out.println(clf.radiusNeighbors(instance, 1.0));
        System.out.println();
        System.out.println("数据集划分为测试集与训练集：");
        ClassifierTestUtil.trainTestEvaluate(clf, iris, 0.2);
        System.out.println();
        System.out.println("交叉验证：");
        ClassifierTestUtil.crossValidation(clf, iris, 5);
    }
}
