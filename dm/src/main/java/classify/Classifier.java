package main.java.classify;

import main.java.core.DataSet;
import main.java.core.Instance;


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
     * Predicts the class value for given instance.
     *
     * @param instance the specified instance
     * @return class value for given instance
     */
    double classify(Instance instance);
}
