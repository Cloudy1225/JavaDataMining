package main.java.core;

import main.java.core.exception.DimensionNotMatchedException;

/**
 * The interface for instances in a data set.
 *
 * <p>
 * All methods that change an instance's attribute values must be safe,
 * a change of an instance's attribute values must not affect any other instances.
 * </p>
 *
 * @author Cloudy1225
 * @see DataSet
 * @see DenseInstance
 */
public interface Instance extends Copyable<Instance> {

    /**
     * Returns the weight of this instance.
     *
     * @return instance's weight
     */
    double getWeight();

    /**
     * Sets the weight of the instance.
     *
     * @param weight weight: classWeight * sampleWeight
     */
    void setWeight(double weight);

    /**
     * Returns whether the instance has the class value.
     *
     * @return true: labeled; false: unlabeled
     */
    boolean isLabeled();

    /**
     * Returns the class value for this instance.
     *
     * @return the class value of this instance, or {@code Double.NaN} if the class is not set.
     */
    double classValue();

    /**
     * Sets the class value of this instance to the given value.
     * @param value the new class value
     */
    void setClassValue(double value);

    /**
     * Sets the reference to the dataset.
     * It may throw {@link DimensionNotMatchedException}.
     *
     * @param dataset the reference to the dataset
     * @throws DimensionNotMatchedException if the dataset's dimensionality is different from this instance's
     */
    void setDataSet(DataSet dataset);

    /**
     * Returns the dimensionality of this instance, which equals the number of attributes.
     *
     * @return the number of attributes as an integer
     */
    int dimensionality();

    /**
     * Returns the value of this instance's attribute with given index.
     *
     * @param index the attribute's index
     * @return the attribute's value
     */
    double attribute(int index);

    /**
     * Returns a new instance without the attribute at given position.
     *
     * @param index the index of attribute to be removed
     * @return a new instance
     */
    Instance deleteAttribute(int index);

}
