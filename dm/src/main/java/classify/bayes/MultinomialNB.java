package main.java.classify.bayes;

import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.Instance;
import main.java.utils.ArrayUtil;
import main.java.utils.MapUtil;
import main.java.utils.MathUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Naive Bayes classifier for multinomial models.
 * The multinomial Naive Bayes classifier is suitable for classification with discrete features (e.g., word counts for text classification).
 * The multinomial distribution normally requires integer feature counts. However, in practice, fractional counts such as tf-idf may also work.
 *
 * @author Cloudy1225
 * @see <a href="https://scikit-learn.org/stable/modules/naive_bayes.html#multinomial-naive-bayes">multinomial-naive-bayes</a>
 */
public class MultinomialNB extends NaiveBayesClassifier {

    /**
     * Prior probabilities of the classes.
     * If specified, the priors are not adjusted according to the data.
     */
    private double[] priors;

    /**
     * Additive (Laplace/Lidstone) smoothing parameter
     * (default=1.0, and 0 for no smoothing).
     */
    private double alpha;

    /**
     * Create a MultinomialNB with default {@code alpha = 1.0}
     * and no prior probabilities of the classes.
     */
    public MultinomialNB() {
        this.alpha = 1.0;
    }

    /**
     *  Create a MultinomialNB with given {@code alpha}
     *  and no prior probabilities of the classes.
     *
     * @param alpha additive (Laplace/Lidstone) smoothing parameter
     */
    public MultinomialNB(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Create a MultinomialNB with default {@code alpha = 1.0}
     * and given prior probabilities of the classes.
     *
     * @param priors prior probabilities of the classes
     */
    public MultinomialNB(double[] priors) {
        this.priors = priors;
        this.alpha = 1.0;
    }

    /**
     * Create a MultinomialNB with given {@code alpha}
     * and given prior probabilities of the classes.
     *
     * @param priors prior probabilities of the classes
     * @param alpha additive (Laplace/Lidstone) smoothing parameter
     */
    public MultinomialNB(double[] priors, double alpha) {
        this.priors = priors;
        this.alpha = alpha;
    }

    /**
     * Number of samples encountered for each class during fitting.
     * This value is weighted by the sample weight when provided.
     */
    private TreeMap<Double, Double> classCountMap;

    /**
     * Smoothed empirical log probability for each class.
     */
    private double[] classPrior_;

    /**
     * Number of samples encountered for each (class, feature) during fitting.
     * This value is weighted by the sample weight when provided.
     */
    private TreeMap<Double, TreeMap<Double, Double>>[] featureCountMaps;

    /**
     * Empirical log probability of features given a class, P(x_i|y).
     */
    private HashMap<Double, Double>[][] featureProb_;

    @Override
    public void fit(DataSet dataset) {
        this.classCountMap = DataSets.columnDistMap(dataset, -1);

        int nFeatures = dataset.dimensionality();
        int nClasses = this.classCountMap.size();

        // Initialise the class log prior
        // Take into account the priors
        if (this.priors != null) {
            // Check that the provided prior match the number of classes
            if (priors.length != nClasses) {
                throw new IllegalArgumentException("Number of priors must match number of classes.");
            }
            // Check that the sum is 1
            if (!MathUtil.eq(ArrayUtil.sum(priors), 1)) {
                throw new IllegalArgumentException("The sum of the priors should be 1.");
            }
            // Check that the prior are non-negative
            for (double prior : priors) {
                if (prior < 0) {
                    throw new IllegalArgumentException("Priors must be non-negative.");
                }
            }
            this.classPrior_ = priors;
        } else {
            this.classPrior_ = new double[nClasses];
            // Empirical prior, with sample_weight taken into account
            double sumCount = MapUtil.sumValues(this.classCountMap) + this.alpha*nClasses;
            int i = 0;
            for (double count: this.classCountMap.values()) {
                this.classPrior_[i++] = (count+this.alpha) / sumCount;
            }
        }

        this.featureCountMaps = new TreeMap[nFeatures];
        this.featureProb_ = new HashMap[nClasses][nFeatures];
        for (int c = 0; c < nFeatures; c++) {
            TreeMap<Double, TreeMap<Double, Double>> colConDistMap = DataSets.colConDistMap(dataset, -1, c);
            featureCountMaps[c] = colConDistMap;
            int r = 0;
            for (TreeMap<Double, Double> featureDistMap: colConDistMap.values()) {
                HashMap<Double, Double> featureProb = new HashMap<>();
                double sumCount = MapUtil.sumValues(featureDistMap) + this.alpha*featureDistMap.size();
                for (Map.Entry<Double, Double> entry: featureDistMap.entrySet()) {
                    double p = (entry.getValue()+this.alpha) / sumCount;
                    featureProb.put(entry.getKey(), p);
                }
                this.featureProb_[r++][c] = featureProb;
            }
        }

        this.isFitted = true;
    }

    @Override
    public Map<Double, Double> jointLogLikelihood(Instance instance) {
        Map<Double, Double> res = new TreeMap<>();
        int i = 0;
        for (Double clazz: this.classCountMap.keySet()) {
            double joint_i = Math.log(this.classPrior_[i]);
            for (int j = 0; j < this.featureProb_[i].length; j++) {
                Double p = featureProb_[i][j].get(instance.attribute(j));
                if (p != null) {
                    double n_ij = Math.log(p);
                    joint_i += n_ij;
                }
            }
            res.put(clazz, joint_i);
            i++;
        }
        return res;
    }
}
