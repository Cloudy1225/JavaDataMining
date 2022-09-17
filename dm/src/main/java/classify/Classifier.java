package main.java.classify;

import main.java.core.DataSet;
import main.java.core.Instance;

import java.util.Map;

/**
 * Interface for all classifiers.
 *
 * @author Cloudy1225
 */
public interface Classifier {

    /**
     * Builds a classifier from the training set.
     *
     * @param dataset training set
     */
    void fit(DataSet dataset);

    /**
     * Builds a classifier from the training set with given classWeight and sampleWeight.
     *
     * @param dataset training set
     * @param classWeight class weights
     * @param sampleWeight sample weights
     */
    void fit(DataSet dataset, Map<Double, Double> classWeight, double[] sampleWeight);

    /**
     * Predicts the class value for given instance.
     *
     * @param instance the specified instance
     * @return class value for given instance
     */
    double classify(Instance instance);
}
