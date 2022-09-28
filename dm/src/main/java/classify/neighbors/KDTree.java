package main.java.classify.neighbors;

import main.java.core.DataSet;
import main.java.core.DenseInstance;
import main.java.core.Instance;
import main.java.core.exception.EstimatorNotFittedException;
import main.java.metrics.distance.DistanceMetric;

import java.util.*;

/**
 * KDTree for fast generalized N-point problems.
 *
 * @author Cloudy1225
 * @see <a href="https://blog.csdn.net/qq_29923461/article/details/119204500">kd树算法原理详解及C++实现</a>
 */
public class KDTree {

    private int dimensionality;

    private KDNode root;

    /**
     * Builds a k-d tree with given dataset.
     *
     * @param dataset dataset to store
     */
    public void buildTree(DataSet dataset) {
        this.dimensionality = dataset.dimensionality();
        List<Instance> train = new ArrayList<>(dataset.size());
        for (Instance instance: dataset) {
            train.add(instance);
        }
        this.root = this.build(train);
    }

    /**
     * 根据方差大小选择划分维度，方差越大，划分效果越好
     */
    private KDNode build(List<Instance> dataset) {
        int size = dataset.size();
        if (size == 0) {
            return null;
        }
        KDNode node = new KDNode();
        List<Instance> thisDataSet = new ArrayList<>();
        if (size == 1) {
            thisDataSet.add(dataset.get(0));
            node.instances = thisDataSet;
            return node;
        }

        // median of the most spread dimension pivoting strategy
        double maxVar = -1;
        int feature  = -1;
        for (int i = 0; i < dimensionality; i++) {
            double var = this.computeVariance(dataset, i);
            if (var > maxVar) {
                maxVar = var;
                feature = i;
            }
        }
        double pivot = this.getMedian(dataset, feature);
        List<Instance> leftDataSet = new ArrayList<>(size / 2);
        List<Instance> rightDataSet = new ArrayList<>(size / 2);
        for (Instance instance: dataset) {
            double attrValue = instance.attribute(feature);
            if (attrValue == pivot) {
                thisDataSet.add(instance);
            } else if (attrValue < pivot) {
                leftDataSet.add(instance);
            } else {
                rightDataSet.add(instance);
            }
        }
        node.feature = feature;
        node.pivot = pivot;
        node.instances = thisDataSet;
        node.left = this.build(leftDataSet);
        node.right = this.build(rightDataSet);
        return node;
    }

    /**
     * 寻找中位数，偶数时返回右边的
     */
    private double getMedian(List<Instance> dataset, int index) {
        double[] values = new double[dataset.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = dataset.get(i).attribute(index);
        }
        Arrays.sort(values);
        return values[values.length / 2];
    }

    /**
     * 计算方差
     */
    private double computeVariance(List<Instance> dataset, int index) {
        int n = dataset.size();
        double sumX_i2 = 0;
        double sumX_i = 0;
        for (Instance instance: dataset) {
            double attrValue = instance.attribute(index);
            sumX_i2 += attrValue * attrValue;
            sumX_i += attrValue;
        }
        return (sumX_i2 - sumX_i/n*sumX_i) / n;
    }
    /**
     * Searches the tree for the k nearest neighbors.
     *
     * @param instance instance to query
     * @param k number of nearest neighbors to return
     * @param metric metric to use for distance computation
     * @return the sorted K-neighbors: instance and distance
     */
    public Map<Instance, Double> query(Instance instance, int k, DistanceMetric metric) {
        if (this.root == null) {
            throw new EstimatorNotFittedException("KDTree is not fitted yet.");
        }
        PriorityQueue<InDistance> maxHeap = new PriorityQueue<>(k); // 保存最近K个邻居
        ArrayDeque<KDNode> stack = new ArrayDeque<>();// 用于递归加回溯
        KDNode curRoot = this.root;
        // 寻找初始时instance所属于的叶节点
        while (true) {
            // 只有倒数第二层节点可能只有一个子树
            stack.push(curRoot);
            if (curRoot.left != null) { // 有左子树
                if (instance.attribute(curRoot.feature) < curRoot.pivot) {
                    curRoot = curRoot.left; // 小于中位数，走左子树
                } else {
                    if (curRoot.right != null) { // 大于等于中位数且有右子树，走右子树
                        curRoot = curRoot.right;
                    } else { // 有左子树，但无右子树，即倒数第二层的内部节点，只能走左子树
                        curRoot = curRoot.left;
                    }
                }
            } else if (curRoot.right != null){ // 无左子树，但有右子树，即倒数第二层的内部节点，只能走右子树
                curRoot = curRoot.right;
            } else { // 叶子节点
                break;
            }
        }
        while (!stack.isEmpty()) { // 栈空时，说明所有可能的k近邻的已经遍历完
            KDNode node = stack.poll();
            List<Instance> candidates = node.instances; // 候选邻居
            for (Instance candidate: candidates) {
                double distance = metric.measure(instance, candidate);
                if (maxHeap.size() < k) {
                    maxHeap.add(new InDistance(candidate, distance));
                } else {
                    double maxDistance = maxHeap.peek().distance;
                    if (maxDistance > distance) {
                        maxHeap.poll();
                        maxHeap.offer(new InDistance(candidate, distance));
                    }
                }
            }
            // 对于有左右子树的内部节点，回溯时另一个子树可能有候选邻居，故需要判断另一个树是否过去
            // 对于叶子节点和只有一个子树的内部节点不需要回溯
            if (node.left != null && node.right != null) {
                // 当已经有k个邻居，且instance到分割面的距离不小于当前邻居的最大距离，则另一个子树没有候选邻居
                if (maxHeap.size() >= k) { // 已经有k个邻居
                    double[] splitLineCoordinate = new double[this.dimensionality]; // 分割线的坐标
                    for (int i = 0; i < node.feature; i++) {
                        splitLineCoordinate[i] = instance.attribute(i);
                    }
                    splitLineCoordinate[node.feature] = node.pivot;
                    for (int i = node.feature+1; i < this.dimensionality; i++) {
                        splitLineCoordinate[i] = instance.attribute(i);
                    }
                    DenseInstance splitLine = new DenseInstance(splitLineCoordinate);
                    double distToSplitLine = metric.measure(instance, splitLine);
                    double maxDistance = maxHeap.peek().distance;
                    if (distToSplitLine >= maxDistance) { // instance到分割面的距离不小于当前邻居的最大距离
                        continue;
                    }
                }
                // 当还没有有k个邻居，或instance到分割面的距离小于当前邻居的最大距离，则另一个子树有候选邻居
                // 由于是二叉树，要去另一个子树，只需要知道先去了哪个即可
                if (instance.attribute(node.feature) < node.pivot) {  // 先去去左子树，则另一个子树为右子树
                    curRoot = node.right;
                } else {
                    curRoot = node.left;
                }
                while (true) { // 寻找另一个子树中instance所属于的叶节点，同上
                    // 只有倒数第二层节点可能只有一个子树
                    stack.push(curRoot);
                    if (curRoot.left != null) { // 有左子树
                        if (instance.attribute(curRoot.feature) < curRoot.pivot) {
                            curRoot = curRoot.left; // 小于中位数，走左子树
                        } else {
                            if (curRoot.right != null) { // 大于等于中位数且有右子树，走右子树
                                curRoot = curRoot.right;
                            } else { // 有左子树，但无右子树，即倒数第二层的内部节点，只能走左子树
                                curRoot = curRoot.left;
                            }
                        }
                    } else if (curRoot.right != null){ // 无左子树，但有右子树，即倒数第二层的内部节点，只能走右子树
                        curRoot = curRoot.right;
                    } else { // 叶子节点
                        break;
                    }
                }
            }
        }
        // 将保存邻居的堆转变为根据距离从小到大排列的Map
        InDistance[] neighbors = new InDistance[maxHeap.size()];
        maxHeap.toArray(neighbors);
        Arrays.sort(neighbors, new Comparator<InDistance>() {
            @Override
            public int compare(InDistance o1, InDistance o2) {
                return Double.compare(o1.distance, o2.distance);
            }
        });
        // LinkedHashMap保证了插入顺序
        LinkedHashMap<Instance, Double> kNeighbors = new LinkedHashMap<>(neighbors.length);
        for (InDistance neighbor: neighbors) {
            kNeighbors.put(neighbor.instance, neighbor.distance);
        }
        return kNeighbors;
    }

    /**
     * Searches the tree for neighbors within a radius r
     *
     * @param instance instance to query
     * @param r limiting distance of neighbors to return
     * @param metric metric to use for distance computation
     * @return the sorted radius-neighbors: instance and distance
     */
    public Map<Instance, Double> queryRadius(Instance instance, double r, DistanceMetric metric) {
        if (this.root == null) {
            throw new EstimatorNotFittedException("KDTree is not fitted yet.");
        }
        ArrayList<InDistance> neighborBall = new ArrayList<>(); // 保存球内的邻居
        ArrayDeque<KDNode> stack = new ArrayDeque<>();// 用于递归加回溯
        KDNode curRoot = this.root;
        // 寻找初始时instance所属于的叶节点
        while (true) {
            // 只有倒数第二层节点可能只有一个子树
            stack.push(curRoot);
            if (curRoot.left != null) { // 有左子树
                if (instance.attribute(curRoot.feature) < curRoot.pivot) {
                    curRoot = curRoot.left; // 小于中位数，走左子树
                } else {
                    if (curRoot.right != null) { // 大于等于中位数且有右子树，走右子树
                        curRoot = curRoot.right;
                    } else { // 有左子树，但无右子树，即倒数第二层的内部节点，只能走左子树
                        curRoot = curRoot.left;
                    }
                }
            } else if (curRoot.right != null){ // 无左子树，但有右子树，即倒数第二层的内部节点，只能走右子树
                curRoot = curRoot.right;
            } else { // 叶子节点
                break;
            }
        }
        while (!stack.isEmpty()) { // 栈空时，说明所有可能的近邻的已经遍历完
            KDNode node = stack.poll();
            List<Instance> candidates = node.instances; // 候选邻居
            for (Instance candidate: candidates) {
                double distance = metric.measure(instance, candidate);
                if (distance <= r) {
                    neighborBall.add(new InDistance(candidate, distance));
                }
            }
            // 对于有左右子树的内部节点，回溯时另一个子树可能有候选邻居，故需要判断另一个树是否过去
            // 对于叶子节点和只有一个子树的内部节点不需要回溯
            if (node.left != null && node.right != null) {
                // 且instance到分割面的距离不小于radius，则另一个子树没有候选邻居
                double[] splitLineCoordinate = new double[this.dimensionality]; // 分割线的坐标
                for (int i = 0; i < node.feature; i++) {
                    splitLineCoordinate[i] = instance.attribute(i);
                }
                splitLineCoordinate[node.feature] = node.pivot;
                for (int i = node.feature+1; i < this.dimensionality; i++) {
                    splitLineCoordinate[i] = instance.attribute(i);
                }
                DenseInstance splitLine = new DenseInstance(splitLineCoordinate);
                double distToSplitLine = metric.measure(instance, splitLine);
                if (distToSplitLine >= r) { // instance到分割面的距离不小于当前邻居的最大距离
                    continue; // 另一个子树无候选节点
                }
                // 由于是二叉树，要去另一个子树，只需要知道先去了哪个即可
                if (instance.attribute(node.feature) < node.pivot) {  // 先去去左子树，则另一个子树为右子树
                    curRoot = node.right;
                } else {
                    curRoot = node.left;
                }
                while (true) { // 寻找另一个子树中instance所属于的叶节点，同上
                    // 只有倒数第二层节点可能只有一个子树
                    stack.push(curRoot);
                    if (curRoot.left != null) { // 有左子树
                        if (instance.attribute(curRoot.feature) < curRoot.pivot) {
                            curRoot = curRoot.left; // 小于中位数，走左子树
                        } else {
                            if (curRoot.right != null) { // 大于等于中位数且有右子树，走右子树
                                curRoot = curRoot.right;
                            } else { // 有左子树，但无右子树，即倒数第二层的内部节点，只能走左子树
                                curRoot = curRoot.left;
                            }
                        }
                    } else if (curRoot.right != null){ // 无左子树，但有右子树，即倒数第二层的内部节点，只能走右子树
                        curRoot = curRoot.right;
                    } else { // 叶子节点
                        break;
                    }
                }
            }
        }
        // 将保存邻居的堆转变为根据距离从小到大排列的Map
        neighborBall.sort(new Comparator<InDistance>() {
            @Override
            public int compare(InDistance o1, InDistance o2) {
                return Double.compare(o1.distance, o2.distance);
            }
        });
        // LinkedHashMap保证了插入顺序
        LinkedHashMap<Instance, Double> neighbors = new LinkedHashMap<>(neighborBall.size());
        for (InDistance neighbor: neighborBall) {
            neighbors.put(neighbor.instance, neighbor.distance);
        }
        return neighbors;
    }

    private static class KDNode {
        int feature;
        double pivot;
        List<Instance> instances;
        KDNode left;
        KDNode right;
    }

}

