package main.java.core;


import main.java.core.exception.DimensionNotMatchedException;

import java.util.Arrays;

/**
 * Implementation of a dense instance. A dense instance is a wrapper around an
 * array that provides a value for each attribute index.
 *
 * @author Cloudy1225
 * @see DataSet
 * @see Instance
 */
public class DenseInstance implements Instance {

    /**
     * For instances without class label, their classValue will be set as NaN.
     */
    public static final double NO_LABEL = Double.NaN;

    /**
     * The default weight of an instance is 1.0
     */
    public static final double DEFAULT_WEIGHT = 1.0;

    /**
     * The instance's class. Double.NaN if the class is not set.
     */
    protected double classValue = Double.NaN;

    /**
     * The instance's attribute values.
     */
    protected double[] attributes;

    /**
     * The instance's weight whose default value is 1.0.
     */
    protected double weight = 1.0;

    /**
     * The dataset that the instance has access to.
     * Null if the instance doesn't have access to any dataset.
     * Only if an instance has access to a dataset,
     * it knows about the actual AttributeInfo.
     */
    protected DataSet dataset;

    /**
     * Creates a new labeled and weighted instance with the provided attribute values, class label, weight, dataset.
     *
     * @param attributeValues the provided attribute values
     * @param classValue the provided class label
     * @param weight the instance's weight
     * @param dataset the data set which this instance belongs to
     */
    public DenseInstance(double[] attributeValues, double classValue, double weight, DataSet dataset) {
        this.attributes = attributeValues;
        this.classValue = classValue;
        this.weight = weight;
        this.dataset = dataset;
    }

    /**
     * Creates a new labeled and weighted instance with the provided attribute values, class label and weight.
     *
     * @param attributeValues the provided attribute values
     * @param classValue the provided class label
     * @param weight the instance's weight
     */
    public DenseInstance(double[] attributeValues, double classValue, double weight) {
        this.attributes = attributeValues;
        this.classValue = classValue;
        this.weight = weight;
    }

    /**
     * Creates a new labeled instance with the provided attribute values and class label.
     *
     * @param attributeValues the provided attribute values
     * @param classValue the provided class label
     */
    public DenseInstance(double[] attributeValues, double classValue) {
        this.attributes = attributeValues;
        this.classValue = classValue;
    }

    /**
     * Creates a new unlabeled instance with the provided attribute values.
     * The new instance's class value is {@code Double.NaN}, weight is 1.0.
     *
     * @param attributeValues the provided attribute values
     */
    public DenseInstance(double[] attributeValues) {
        this.attributes = attributeValues;
    }


    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public final boolean isLabeled() {
        return !Double.isNaN(this.classValue);
    }

    @Override
    public final double classValue() {
        return this.classValue;
    }

    @Override
    public final void setClassValue(double value) {
        this.classValue = value;
    }

    @Override
    public final void setDataSet(DataSet dataset) {
        if (dataset.dimensionality() != this.dimensionality()) {
            String msg = "The data set has " + dataset.dimensionality() +
                    " attributes, but the instance has " + this.dimensionality() + " attributes.";
            throw new DimensionNotMatchedException(msg);
        }
        this.dataset = dataset;
    }

    @Override
    public final int dimensionality() {
        return this.attributes.length;
    }

    @Override
    public final double attribute(int index) {
        return this.attributes[index];
    }

    @Override
    public DenseInstance deleteAttribute(int index) {
        double[] current = new double[attributes.length-1];
        System.arraycopy(attributes, 0, current, 0, index);
        System.arraycopy(attributes, index+1, current, index, attributes.length-index-1);
        return new DenseInstance(current, this.classValue);
    }

    /**
     * Returns a shallow copy of this instance.
     * But the data set it has access to is null.
     *
     * @return a shallow copy of this instance
     */
    @Override
    public DenseInstance copy() {
        return new DenseInstance(this.attributes, this.classValue, this.weight, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(attributes);
        return result;
    }

    @Override
    public String toString() {
        if (this.isLabeled()) {
            return Arrays.toString(this.attributes) + ";" + this.classValue;
        } else {
            return Arrays.toString(this.attributes);
        }
    }
}
