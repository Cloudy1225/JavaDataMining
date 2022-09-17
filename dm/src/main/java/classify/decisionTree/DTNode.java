package main.java.classify.decisionTree;

import main.java.core.AttributeInfo;
import main.java.core.Copyable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This defines the abstract base class for nodes in decision trees.
 *
 * @author Cloudy1225
 * @see DecisionTree
 */
public abstract class DTNode implements Copyable<DTNode> {

    /**
     * the feature to split on，for the internal node
     */
    protected AttributeInfo feature;

    /**
     * Constant prediction value of the node （该节点的预测类）
     */
    protected double clazz;

    /**
     * Depth of the node. ( The depth of the root is 1 )
     */
    protected int depth;

    /**
     * Impurity of the node (i.e., the value of the criterion)
     */
    protected double impurity;

    /**
     * Number of samples at the node
     */
    protected int nNodeSamples;

    /**
     * Weighted number of samples at the node
     */
    protected double weightedNNodeSamples;


    protected abstract boolean isLeaf();

    protected abstract void toLeaf();

    protected abstract void addChild(DTNode child);

    protected abstract DTNode matchChild(double value);

    protected abstract Iterator<? extends DTNode> children();

    protected abstract String edgeString();

    public void print() {
        List<DTNode> line = new ArrayList<>();
        line.add(this);
        while (line.size() > 0) {
            System.out.println(line);
            List<DTNode> newLine = new ArrayList<>();
            for (DTNode node: line) {
                for (Iterator<? extends DTNode> it = node.children(); it.hasNext(); ) {
                    newLine.add(it.next());
                }
            }
            line = newLine;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.isLeaf()) {
            sb.append("{ class: ");
            sb.append(this.clazz);
        } else {
            sb.append("{ attrName: ");
            sb.append(this.feature.name);
            sb.append(", edgeValue: ");
            sb.append(this.edgeString());
        }
        sb.append(" }");
        return sb.toString();
    }

}
