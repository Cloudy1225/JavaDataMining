package main.java.classify.bayes;

import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.core.Instance;
import main.java.utils.ArrayUtil;
import main.java.utils.MathUtil;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * Gaussian Naive Bayes (GaussianNB).
 *
 * @author Cloudy1225
 * @see NaiveBayesClassifier
 * @see <a href="https://scikit-learn.org/stable/modules/naive_bayes.html#gaussian-naive-bayes">gaussian-naive-bayes</a>
 */
public class GaussianNB extends NaiveBayesClassifier {

    /**
     * Prior probabilities of the classes.
     * If specified, the priors are not adjusted according to the data.
     */
    private double[] priors;

    /**
     * Portion of the largest variance of all features that is added to variances for calculation stability.
     * Default = 1e-9
     */
    private final double varSmoothing;

    /**
     * Create a GaussianNB with default {@code varSmoothing = 1e-9}
     * and no prior probabilities of the classes.
     */
    public GaussianNB() {
        this.varSmoothing = 1e-9;
    }

    /**
     * Create a GaussianNB with given {@code varSmoothing}
     * and no prior probabilities of the classes.
     *
     * @param varSmoothing portion of the largest variance of all features that is added to variances for calculation stability
     */
    public GaussianNB(double varSmoothing) {
        this.varSmoothing = varSmoothing;
    }

    /**
     * Create a GaussianNB with default {@code varSmoothing = 1e-9}
     * and given prior probabilities of the classes.
     *
     * @param priors prior probabilities of the classes
     */
    public GaussianNB(double[] priors) {
        this.priors = priors;
        this.varSmoothing = 1e-9;
    }

    /**
     * Create a GaussianNB with given {@code varSmoothing}
     * and given prior probabilities of the classes.
     *
     * @param priors prior probabilities of the classes
     * @param varSmoothing portion of the largest variance of all features that is added to variances for calculation stability
     */
    public GaussianNB(double[] priors, double varSmoothing) {
        this.priors = priors;
        this.varSmoothing = varSmoothing;
    }

    /**
     * Number of training samples observed in each class.
     */
    private double[] classCount_;

    /**
     * Probability of each class.
     */
    private double[] classPrior_;

    /**
     * Class labels known to the classifier.
     */
    private SortedSet<Double> classes_;

    /**
     * Absolute additive value to variances.
     */
    private double epsilon_;

    /**
     * Variance of each feature per class.
     */
    private double[][] var_;

    /**
     * Mean of each feature per class.
     */
    private double[][] theta_;

//    /**
//     * The total weighted number of given dataset.
//     */
//    private double weightedNSamples;

    /**
     * Fits Gaussian Naive Bayes according to training set.
     *
     * @param dataset training set
     */
    @Override
    public void fit(DataSet dataset) {
        // If the ratio of data variance between dimensions is too small, it
        // will cause numerical errors. To address this, we artificially
        // boost the variance by epsilon, a small fraction of the standard
        // deviation of the largest dimension.
        this.epsilon_ = this.varSmoothing * ArrayUtil.max(DataSets.var(dataset));
        TreeMap<Double, Double> classDistMap = DataSets.columnDistMap(dataset, -1);
        this.classes_ = (SortedSet<Double>) classDistMap.keySet();

        // Initialize various counters.
        int nFeatures = dataset.dimensionality();
        int nClasses = this.classes_.size();
        this.var_ = new double[nClasses][nFeatures];
        this.theta_ = new double[nClasses][nFeatures];

        this.classCount_ = new double[nClasses];
        int k = 0;
        for (double count: classDistMap.values()) {
            classCount_[k++] = count;
        }

        // Initialise the class prior
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
            double sumCount = ArrayUtil.sum(this.classCount_);
            for (int i = 0; i < nClasses; i++) {
                this.classPrior_[i] = classCount_[i] / sumCount;
            }
        }

        for (int i = 0; i < nFeatures; i++) {
            TreeMap<Double, TreeMap<Double, Double>> colConDistMap = DataSets.colConDistMap(dataset, -1, i);
            int j = 0;
            for (TreeMap<Double, Double> distMap : colConDistMap.values()) {
                double sumX_i = 0;
                double sumWeight = 0;
                for (Map.Entry<Double, Double> entry : distMap.entrySet()) {
                    double element = entry.getKey();
                    double weight = entry.getValue();
                    sumWeight += weight;
                    sumX_i += element * weight;
                }
                double mean = sumX_i / sumWeight;
                double variance = 0;
                for (Map.Entry<Double, Double> entry : distMap.entrySet()) {
                    double element = entry.getKey();
                    double weight = entry.getValue();
                    variance += weight / sumWeight * (element - mean) * (element - mean);
                }
                this.theta_[j][i] = mean;
                this.var_[j][i] = variance + this.epsilon_;
                j++;
            }
        }

        this.isFitted = true;
    }

    @Override
    protected Map<Double, Double> jointLogLikelihood(Instance instance) {
        Map<Double, Double> res = new TreeMap<>();
        int i = 0;
        for (Double clazz: this.classes_) {
            double joint_i = Math.log(this.classPrior_[i]);
            for (int j = 0; j < theta_[i].length; j++) {
                double n_ij = -0.5 * Math.log(2*Math.PI * var_[i][j]);
                n_ij -= 0.5 * (instance.attribute(j) - theta_[i][j]) / var_[i][j] * (instance.attribute(j) - theta_[i][j]);
                joint_i += n_ij;
            }
            res.put(clazz, joint_i);
            i++;
        }
        return res;
    }

}
