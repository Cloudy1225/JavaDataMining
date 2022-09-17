package main.java.core;

import main.java.core.exception.DimensionNotMatchedException;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

/**
 * The interface for a data set.
 *
 * @author Cloudy1225
 * @see Instance
 * @see StandardDataSet
 */
public interface DataSet extends Iterable<Instance>, Copyable<DataSet> {

    /**
     * Returns the dimensionality of each instance, which equals the number of attributes.
     * When the data set contains no instances, this method should return 0.
     *
     * @return the number of attributes as an integer
     */
    int dimensionality();

    /**
     * Returns the number of instances in the dataset.
     *
     * @return the number of instances in the dataset as an integer
     */
    int size();

    /**
     * Returns the instance at the given position.
     *
     * @param index the instance's index (index starts with 0)
     * @return the instance at the given position
     */
    Instance instance(int index);

    /**
     * Returns the class information.
     *
     * @return the {@link AttributeInfo} of class
     */
    AttributeInfo classInfo();

    /**
     * Returns the attribute's information at given position.
     *
     * @param attrIndex the attribute's index (index starts with 0)
     * @return the {@link AttributeInfo} at the given position
     */
    AttributeInfo attributeInfo(int attrIndex);

    /**
     * Returns all attributes' information.
     *
     * @return the {@link AttributeInfo} List of all attributes in the data set.
     */
    List<AttributeInfo> attributeInfoList();

    /**
     * Returns a set containing all unique classes in this data set.
     * If no classes are available, this will return the empty set.
     *
     * @return a sorted set
     */
    SortedSet<Double> classSet();

    /**
     * Returns a set containing all different values of the attribute at the given position.
     *
     * @param attrIndex the attribute's index
     * @return a sorted set
     */
    SortedSet<Double> attrValueSet(int attrIndex);

    /**
     * Returns an array contains the class value of each instance in the data set.
     *
     * @return an array contains the class value of each instance
     */
    double[] classValues();

    /**
     * Returns an array contains the attribute's value of each instance at the given position in the data set.
     *
     * @param attrIndex the attribute's index
     * @return an array the attribute's value of each instance
     */
    double[] attrValues(int attrIndex);

    /**
     * Appends an instance to the end of this data set.
     * The new instance's dimensionality must equal the data set's dimensionality.
     * Incompatible instance should not be added to the data set.
     *
     * @param instance the instance to be added to the end of the data set
     * @throws DimensionNotMatchedException if the instance's dimensionality is different from this data set's
     */
    void add(Instance instance);

    /**
     * Appends a collection of instances to the end of this data set.
     * The new instance's dimensionality must equal the data set's dimensionality.
     * Incompatible instance should not be added to the data set.
     * If there is an incompatible instance in given instances, all will not be appended.
     *
     * @param instances the collection of instances with the same dimensionality
     * @throws DimensionNotMatchedException if the instance's dimensionality is different from this data set's
     */
    void addAll(Collection<? extends Instance> instances);

}
