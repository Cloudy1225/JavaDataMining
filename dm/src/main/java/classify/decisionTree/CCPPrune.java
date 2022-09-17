package main.java.classify.decisionTree;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * This class implements the Minimal Cost-Complexity Pruning algorithm to prune a decision tree.
 *
 * @author Cloudy1225
 * @see DecisionTree
 * @see <a href="https://scikit-learn.org/stable/modules/tree.html#minimal-cost-complexity-pruning">Minimal Cost-Complexity Pruning</a>
 */
public class CCPPrune {

    /**
     * The origin root of the tree to prune.
     */
    private final DTNode root;

    /**
     * A sequence of effective alphas and a sequence of pruned tree correspondingly.
     */
    public final TreeMap<Double, DTNode> subTrees;

    /**
     * The weighted number of the dataset that fit the decision tree.
     */
    private final double weightedNSamples;

    private double effectiveAlpha = Integer.MAX_VALUE;

    private DTNode nodeToPrune;

    /**
     * Constructs a CCPPrune with the root of tree to prune.
     *
     * @param tree the tree to prune
     */
    public CCPPrune(DecisionTree tree) {
        this.subTrees = new TreeMap<>();
        subTrees.put(0.0, tree.root);
        this.root = tree.root;
        this.weightedNSamples = tree.root.weightedNNodeSamples;
    }

    /**
     * Prunes the tree with the specified alpha.
     * The subtree with the largest cost complexity that is smaller than ccpAlpha will be chosen.
     *
     * @param ccpAlpha complexity parameter used for Minimal Cost-Complexity Pruning
     * @return the subtree with the largest cost complexity that is smaller than ccpAlpha
     */
    public DTNode pruneWithAlpha(double ccpAlpha){
        DTNode lastTree = this.root;
        while (!lastTree.isLeaf()) {
            this.computeLeafNumberAndSubtreeImpurity(lastTree);
            if (this.effectiveAlpha > ccpAlpha) {
                break;
            }
            DTNode prunedTree = this.copyAndPrune(lastTree);
            this.effectiveAlpha = Integer.MAX_VALUE;
            this.nodeToPrune = null;
            lastTree = prunedTree;
        }
        return lastTree;
    }

    /**
     * Prunes leaves of the tree one by one, util only one root node in the tree.
     * Returns a sequence of effective alphas and a sequence of pruned tree correspondingly.
     *
     * @return a map whose key is alpha, value is the root of pruned tree.
     */
    public TreeMap<Double, DTNode> pruneAll() {
        DTNode lastTree = subTrees.lastEntry().getValue();
        while (!lastTree.isLeaf()) {
            this.computeLeafNumberAndSubtreeImpurity(lastTree);
            DTNode prunedTree = this.copyAndPrune(lastTree);
            this.subTrees.put(this.effectiveAlpha, prunedTree);
            this.effectiveAlpha = Integer.MAX_VALUE;
            this.nodeToPrune = null;
            lastTree = prunedTree;
        }
        return this.subTrees;
    }

    private DTNode copyAndPrune(DTNode oldRoot) {
        DTNode newRoot = oldRoot.copy();
        if (oldRoot.isLeaf()) {
            return newRoot;
        }
        if (oldRoot == this.nodeToPrune) {
            newRoot.toLeaf();
            return newRoot;
        }
        for (Iterator<? extends DTNode> it = oldRoot.children(); it.hasNext(); ) {
            DTNode oldChild = it.next();
            newRoot.addChild(this.copyAndPrune(oldChild));
        }
        return newRoot;
    }

    /**
     * Recursively gets the number of leaves and the impurity and subtree.
     *
     * @param node root of tree
     * @return [0]: leaves' number; [1]: impurity
     */
    private double[] computeLeafNumberAndSubtreeImpurity(DTNode node) {
        double impurity = node.weightedNNodeSamples * node.impurity / this.weightedNSamples;
        if (node.isLeaf()) {
            return new double[]{1, impurity};
        } else {
            double leafNum = 0;
            double subTreeImpurity = 0;
            for (Iterator<? extends DTNode> it = node.children(); it.hasNext(); ) {
                DTNode child = it.next();
                double[] childReturn = computeLeafNumberAndSubtreeImpurity(child);
                leafNum += childReturn[0];
                subTreeImpurity += childReturn[1];
            }
            double alpha = (impurity - subTreeImpurity) / (leafNum - 1);
            if (alpha < this.effectiveAlpha) {
                this.effectiveAlpha = alpha;
                this.nodeToPrune = node;
            } else if (alpha == this.effectiveAlpha) {
                if (node.weightedNNodeSamples <= this.nodeToPrune.weightedNNodeSamples) {
                    this.effectiveAlpha = alpha;
                    this.nodeToPrune = node;
                }
            }
            return new double[]{leafNum, subTreeImpurity};
        }
    }

}
