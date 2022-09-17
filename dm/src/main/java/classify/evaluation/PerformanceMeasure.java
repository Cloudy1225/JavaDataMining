package main.java.classify.evaluation;

/**
 * The class implements several performance measures commonly used for
 * classification algorithms. Each of these measure can be calculated from four
 * basic values from the classifier. The number of true positives, the number of
 * true negatives, the number of false positives and the number of false
 * negatives.
 *
 * @author Cloudy1225
 */
public class PerformanceMeasure {

    /**
     * The number of true positives.
     */
    public double TP;

    /**
     * The number of false positives.
     */
    public double FP;

    /**
     * The number of true negatives.
     */
    public double TN;

    /**
     * The number of false negatives.
     */
    public double FN;

    /**
     * Constructs a new performance measure using given arguments.
     *
     * @param TP the number of true positives
     * @param TN the number of true negatives
     * @param FP the number of false positives
     * @param FN the number of false negatives
     */
    public PerformanceMeasure(double TP, double TN, double FP, double FN) {
        this.TP = TP;
        this.TN = TN;
        this.FP = FP;
        this.FN = FN;
    }

    /**
     * Default constructor for a new performance measure, all values (TP,TN,FP
     * and FN) will be set zero
     */
    public PerformanceMeasure() {
        this(0, 0, 0, 0);
    }

    public double getTPRate() {
        return this.TP / (this.TP + this.FN);
    }

    public double getTNRate() {
        return this.TN / (this.TN + this.FP);
    }

    public double getFNRate() {
        return this.FN / (this.TP + this.FN);
    }

    public double getFPRate() {
        return this.FP / (this.FP + this.TN);
    }

    public double getAccuracy() {
        return (TP+TN) / (TP+TN+FP+FN);
    }

    public double getErrorRate() {
        return (FP+FN) / (TP+TN+FP+FN);
    }

    public double getPrecision() {
        return TP / (TP+FP);
    }

    public double getRecall() {
        return this.TP / (this.TP + this.FN);
    }

    /**
     * Returns the balanced F-score. (beta = 1)
     *
     * @return F_1 score
     */
    public double getFMeasure() {
        double precision = this.getPrecision();
        double recall = this.getRecall();
        return 2*precision*recall / (precision+recall);
    }

    /**
     * Returns the F-score with given beta.
     *
     * @param beta recall is considered beta times as important as precision
     * @return F_beta Score
     */
    public double getFMeasure(double beta) {
        double precision = this.getPrecision();
        double recall = this.getRecall();
        return (1+beta*beta) * precision * recall / (beta*beta*precision+recall);
    }

    @Override
    public String toString() {
        return "[TP=" + this.TP + ", FP=" + this.FP + ", TN=" + this.TN + ", FN=" + this.FN + "]";
    }

}
