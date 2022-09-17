package main.java.preprocessing.encoding;

import main.java.core.exception.EstimatorNotFittedException;
import main.java.preprocessing.encoding.exception.EncodingNotFoundException;
import main.java.preprocessing.encoding.exception.LabelUnseenException;
import main.java.utils.MapUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Encodes target labels with value between 0 and n_classes-1.
 * If T is comparable, this class supports encoding by "natural-order" or by "index-order".
 * Otherwise, this class only supports encoding by "index-order".
 * Attention: there is no check for label <tt>null</tt>.
 *
 * @param <T> the Java Class of labels
 * @author Cloudy1225
 */
public class LabelEncoder<T> extends Encoder<T>{

    /**
     * Holds the mapping between labels and encodings.
     */
    private Map<T, Double> encodingMap;

    /**
     * Whether this encoder has already been fitted.
     */
    private boolean fitted;


    /**
     * Creates a LabelEncoder.
     */
    public LabelEncoder() {}

    /**
     * Fits the label encoder by default order.
     * If labels are comparable, the default order is "natural-order".
     * Otherwise, it is "index-order".
     *
     * @param labels an array of target values
     */
    public void fit(T[] labels) {
        this.fit(labels, labels instanceof Comparable[]);
    }

    /**
     * Fits the label encoder by "natural-order" or by "index-order".
     * If given labels are not comparable, "natural" will be automatically set false.
     *
     * @param labels an array of target values
     * @param natural true: encode by "natural-order"; false: encode by "index-order"
     */
    public void fit(T[] labels, boolean natural) {
        if (!(labels instanceof Comparable[])) {
            natural = false;
        }
        this.reset(natural);
        Double encoding = 0.0;
        for (T label: labels) {
            if (!this.encodingMap.containsKey(label)) {
                this.encodingMap.put(label, encoding);
                encoding++;
            }
        }
        this.fitted = true;
    }

    /**
     * Fits the label encoder by "natural-order".
     * You should call this method, only when the generic type of the class is Double.
     *
     * @param labels a double array of target values.
     * @throws ClassCastException if the generics of this class is not Double
     */
    public void fit(double[] labels) {
        this.fit(labels, true);
    }

    /**
     * Fits the label encoder by "natural-order" or by "index-order".
     * You should call this method, only when the generic type of the class is Double.
     *
     * @param labels a double array of target values.
     * @param natural true: encode by "natural-order"; false: encode by "index-order"
     * @throws ClassCastException if the generic type of this class is not Double
     */
    public void fit(double[] labels, boolean natural) {
        this.reset(natural);
        Double encoding = 0.0;
        for (Double label: labels) {
            T l = (T) label;
            if (!this.encodingMap.containsKey(l)) {
                this.encodingMap.put(l, encoding);
                encoding++;
            }
        }
        this.fitted = true;
    }

    /**
     * Resets or initializes before being fitted.
     *
     * @param natural true: encode by "natural-order"; false: encode by "index-order"
     */
    private void reset(boolean natural) {
        // Sorting is implemented by TreeMap or by LinkedHashMap.
        // 通过TreeMap和LinkedHashMap的特殊性质来实现以大小关系编码或者索引顺序编码
        if (natural) {
            this.encodingMap = new TreeMap<>();
        } else {
            this.encodingMap = new LinkedHashMap<>();
        }
        this.fitted = false;
    }

    /**
     * Fits label encoder and returns encoded labels.
     * If given labels are not comparable, "natural" will be automatically set false.
     *
     * @param labels an array of target values
     * @param natural true: encode by "natural-order"; false: encode by "index-order"
     * @return a double array of encoded labels
     */
    public double[] fitTransform(T[] labels, boolean natural) {
        this.fit(labels, natural);
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++) {
            res[i] = this.encodingMap.get(labels[i]);
        }
        return res;
    }

    /**
     * Fits label encoder and returns encoded labels.
     * If labels are comparable, the default order is "natural-order".
     * Otherwise, it is "index-order".
     *
     * @param labels an array of target values
     * @return a double array of encoded labels
     */
    public double[] fitTransform(T[] labels) {
        this.fit(labels);
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++) {
            res[i] = this.encodingMap.get(labels[i]);
        }
        return res;
    }

    /**
     * Transforms labels to normalized encodings.
     *
     * @param labels an array of target values
     * @return a double array containing labels as normalized encodings
     * @throws EstimatorNotFittedException if this encoder is not fitted yet
     * @throws LabelUnseenException if trying to transform a label that wasn't encoded to normalized encoding
     */
    public double[] transform(T[] labels) {
        if (!this.fitted) {
            throw new EstimatorNotFittedException("This encoder is not fitted yet.");
        }
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++) {
            Double encoding = this.encodingMap.get(labels[i]);
            if (encoding == null) {
                String msg = "Given parameter contains previously unseen label: " + labels[i] + ".";
                throw new LabelUnseenException(msg);
            }
            res[i] = encoding;
        }
        return res;
    }

    /**
     * Transforms labels to normalized encodings.
     *
     * @param labels a double array of target values
     * @return a double array containing labels as normalized encodings
     * @throws EstimatorNotFittedException if this encoder is not fitted yet
     * @throws LabelUnseenException if trying to transform a label that wasn't encoded to normalized encoding
     */
    @SuppressWarnings("unchecked")
    public double[] transform(double[] labels) {
        if (!this.fitted) {
            throw new EstimatorNotFittedException("This encoder is not fitted yet.");
        }
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++) {
            T l = (T) Double.valueOf(labels[i]);
            Double encoding = this.encodingMap.get(l);
            if (encoding == null) {
                String msg = "Given parameter contains previously unseen label: " + labels[i] + ".";
                throw new LabelUnseenException(msg);
            }
            res[i] = encoding;
        }
        return res;
    }

    /**
     * Transforms encodings back to original labels.
     *
     * @param encodings a double array of encodings
     * @return an array of original labels
     * @throws EstimatorNotFittedException if this encoder is not fitted yet
     * @throws EncodingNotFoundException if trying to transform an encoding untransformed to original label
     */
    @SuppressWarnings("unchecked")
    public T[] inverseTransform(double[] encodings) {
        if (!this.fitted) {
            throw new EstimatorNotFittedException("This encoder is not fitted yet.");
        }
        Object[] res = new Object[encodings.length];
        for (int i = 0; i < encodings.length; i++) {
            T key = MapUtil.keyOf(this.encodingMap, encodings[i]);
            if (key == null) {
                String msg = "Given parameter contains previously untransformed encoding: " + encodings[i];
                throw new EncodingNotFoundException(msg);
            }
            res[i] = key;
        }
        return (T[]) res;
    }

    /**
     * Gets the mapping between labels and encodings.
     *
     * @return an encoding map
     * @throws EstimatorNotFittedException if this encoder is not fitted yet
     */
    public Map<T, Double> getEncodingMap() {
        if (!this.fitted) {
            throw new EstimatorNotFittedException("This encoder is not fitted yet.");
        }
        return this.encodingMap;
    }


    public static void main(String[] args) {
        LabelEncoder<Double> encoder = new LabelEncoder<>();
        encoder.fit(new double[] {3,4,5}, true);
        Map<Double, Double> map = encoder.encodingMap;
        System.out.println(map);
    }

}
