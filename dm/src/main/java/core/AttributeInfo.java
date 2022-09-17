package main.java.core;

import java.util.Map;

/**
 * The class contains some key information of an attribute, except for its value.
 * There are only two types for an attribute: categorical and numeric.
 * Categorical attributes such as "a","b","c","1","2","3",... : are discrete strings.
 * Numeric attributes such as 1.,2.,3.,12.25,...: are continuous numbers.
 *
 * @author Cloudy1225
 * @see DataSet
 */
public class AttributeInfo {

    /**
     * The constant set for categorical attributes.
     */
    public static final byte CATEGORICAL = 0;

    /**
     * The constant set for numeric attributes.
     */
    public static final byte NUMERIC = 1;

    /**
     * The attribute's name.
     */
    public final String name;

    /**
     * The attribute's type.
     */
    public final byte type;

    /**
     * The attribute's index.
     * When this attribute is class, index is set -1.
     */
    public int index;

    /**
     * The attribute's weight whose default value is 1.0.
     */
    public double weight = 1.0;

    /**
     * Holds the mapping between values and encodings for categorical attribute.
     */
    public Map<String, Double> encodingMap;

    /**
     * Constructs with given attribute's name and type.
     * The attribute's weight is default 1.0.
     *
     * @param name the attribute's name
     * @param type the attribute's type: {0: CATEGORICAL, 1: NUMERIC}
     * @param attrIndex the attribute's index
     * @throws IllegalArgumentException if given type is neither 0 nor 1
     */
    public AttributeInfo(String name, byte type, int attrIndex) {
        if (type != 0 && type != 1) {
            throw new IllegalArgumentException("Type is 0 or 1. {0: CATEGORICAL, 1: NUMERIC}");
        }
        this.name = name;
        this.type = type;
        this.index = attrIndex;
    }

    /**
     * Constructs with given attribute's name, type and weight.
     *
     * @param name the attribute's name
     * @param type the attribute's type: {0: CATEGORICAL, 1: NUMERIC}
     * @param weight the attribute's weight which >= 0
     * @param attrIndex the attribute's index
     * @throws IllegalArgumentException if given type is neither 0 nor 1
     */
    public AttributeInfo(String name, byte type, double weight, int attrIndex) {
        this(name, type, attrIndex);
        this.weight = weight;
    }

    /**
     * Constructs with given attribute's name.
     * And if given "numeric" is true, the type will be set 1.
     * Otherwise, 0.
     * The attribute's weight is default 1.0.
     *
     * @param name the attribute's name
     * @param numeric true if the attribute is numeric
     * @param attrIndex the attribute's index
     */
    public AttributeInfo(String name, boolean numeric, int attrIndex) {
        this.name = name;
        this.type = (byte) (numeric ? 1: 0);
        this.index = attrIndex;
    }

    /**
     * Constructs with given attribute's name and weight.
     * And if given "numeric" is true, the type will be set 1.
     * Otherwise, 0.
     *
     * @param name the attribute's name
     * @param numeric true if the attribute is numeric
     * @param weight the attribute's weight which >=
     * @param attrIndex the attribute's index
     */
    public AttributeInfo(String name, boolean numeric, double weight, int attrIndex) {
        this(name, numeric, attrIndex);
        this.weight = weight;
    }

    /**
     * Returns whether the attribute is continuous.
     *
     * @return true: only when the type is numeric
     */
    public final boolean continuous() {
        return this.type == NUMERIC;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
