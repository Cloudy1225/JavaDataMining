package main.java.classify.evaluation;

import main.java.core.DataSet;

import java.util.Map;

/**
 * The interface is used for classes that do not implement {@link main.java.classify.Classifier},
 * but support cross-validation.
 *
 * @author Cloudy1225
 */
public interface Evaluatable {

    /**
     * Performs cross validation with the specified parameters.
     *
     * @param dataset the data set to use in the cross validation.
     *                This data set is split in the appropriate number of folds.
     * @param k the number of folds to create
     * @return the results of the cross-validation.
     */
    public Map<Double, PerformanceMeasure> crossValidation(DataSet dataset, int k);

}
