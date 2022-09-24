package main.java.classify.bayes;

import main.java.classify.Classifier;
import main.java.core.Instance;
import main.java.core.WeightHandler;
import main.java.core.exception.EstimatorNotFittedException;

import java.util.Map;

/**
 * Abstract base class for naive Bayes classifiers.
 *
 * @author Cloudy1225
 * @see Classifier
 * @see <a href="https://scikit-learn.org/stable/modules/naive_bayes.html#">naive_bayes</a>
 */
public abstract class NaiveBayesClassifier implements Classifier, WeightHandler {

    /**
     * Whether this is fitted yet.
     */
    protected boolean isFitted;

    @Override
    public double classify(Instance instance) {
        return this.predict(instance);
    }

    /**
     * Performs classification on the test instance.
     *
     * @param instance the input instance
     * @return predicted target values
     */
    public double predict(Instance instance) {
        if (!isFitted) {
            throw new EstimatorNotFittedException("Naive bayes classifier is not fitted yet.");
        }
        Map<Double, Double> jll = this.jointLogLikelihood(instance);
        double res = Double.NaN;
        double max = -Double.MAX_VALUE;
        for (Map.Entry<Double, Double> entry: jll.entrySet()) {
            double p = entry.getValue();
            if (p >= max) {
                max = p;
                res = entry.getKey();
            }
        }
        return res;
    }

    /**
     * Computes the unnormalized posterior log probability of given instance.
     * I.e. ``log P(c) + log P(x|c)`` for given instance.
     *
     * @param instance the input instance
     * @return {@code Map}: key is the class, value is the posterior log probability
     */
    public abstract Map<Double, Double> jointLogLikelihood(Instance instance);

}
