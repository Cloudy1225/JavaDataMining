package main.java.classify.decisionTree;

import main.java.core.AttributeInfo;

/**
 * This abstract class just holds data to track sample split which is also the result of feature selection.
 * It should be extended in different algorithms.
 *
 * @author Cloudy1225
 */
public abstract class SplitRecord {

    /**
     * Index of feature to split on.
     */
    protected int feature;

    /**
     * Impurity before being split. (entropy or gini)
     */
    protected double impurity;

    /**
     * Impurity improvement after being split.
     */
    protected double improvement;
}
