package main.java.core;

import main.java.core.exception.DimensionNotMatchedException;

import java.util.*;

/**
 * The class is a standard implementation of {@link DataSet}.
 * StandardDataSet must be created with given {@link AttributeInfo} of each attribute.
 * Instances of the data set must have the same number of attributes.
 *
 * @author Cloudy1225
 * @see DataSet
 */
public class StandardDataSet implements DataSet{

    /**
     * Holds instances.
     */
    protected List<Instance> instances;

    /**
     * Holds class information.
     */
    protected AttributeInfo classInfo;

    /**
     * Holds each attribute's information.
     */
    protected List<AttributeInfo> attributeInfoList;

    /**
     * The number of attributes.
     * It will be set when constructor functions are called.
     */
    protected int dimensionality;

    /**
     * A cache holding different class values.
     */
    protected SortedSet<Double> classSet;

    /**
     * A cache holding all different values of each attribute.
     */
    protected List<SortedSet<Double>> attrValueSetList;

    /**
     * Creates an unsafe empty data set with nothing to do.
     * This method is protected.
     */
    protected StandardDataSet() {}

    /**
     * Creates a new data set with given {@link AttributeInfo} array.
     * The dimensionality is initialized with the array's length.
     *
     * @param attributeInfoArray an array contains each attribute's information.
     */
    public StandardDataSet(AttributeInfo[] attributeInfoArray) {
        this.attributeInfoList = Arrays.asList(attributeInfoArray);
        this.dimensionality = attributeInfoArray.length;
        this.instances = new ArrayList<>();
    }


    /**
     * Creates a new data set with given {@link AttributeInfo} array and the {@link AttributeInfo} of class.
     * The dimensionality is initialized with the array's length.
     *
     * @param attributeInfoArray an array contains each attribute's information.
     * @param classInfo the class information
     */
    public StandardDataSet(AttributeInfo[] attributeInfoArray, AttributeInfo classInfo) {
        this.attributeInfoList = Arrays.asList(attributeInfoArray);
        this.dimensionality = attributeInfoArray.length;
        this.classInfo = classInfo;
        this.instances = new ArrayList<>();
    }

    /**
     * Creates a new data set with given {@link AttributeInfo} list and the {@link AttributeInfo} of class.
     * The dimensionality is initialized with the array's length.
     *
     * @param attributeInfoList a list contains each attribute's information.
     * @param classInfo the class information
     */
    public StandardDataSet(List<AttributeInfo> attributeInfoList, AttributeInfo classInfo) {
        this.attributeInfoList = attributeInfoList;
        this.dimensionality = attributeInfoList.size();
        this.classInfo = classInfo;
        this.instances = new ArrayList<>();
    }

    @Override
    public int dimensionality() {
        return this.dimensionality;
    }

    @Override
    public int size() {
        return this.instances.size();
    }

    @Override
    public Instance instance(int index) {
        return this.instances.get(index);
    }

    @Override
    public AttributeInfo classInfo() {
        return this.classInfo;
    }

    @Override
    public AttributeInfo attributeInfo(int attrIndex) {
        return this.attributeInfoList.get(attrIndex);
    }

    @Override
    public List<AttributeInfo> attributeInfoList() {
        return this.attributeInfoList;
    }

    @Override
    public SortedSet<Double> classSet() {
        if (this.classSet == null) {
            TreeSet<Double> res = new TreeSet<>();
            if (this.classInfo != null) { // If classInfo is null, no classes are available.
                for (Instance instance: this.instances) {
                    res.add(instance.classValue());
                }
            }
            this.classSet = res;
        }
        return this.classSet;
    }

    @Override
    public SortedSet<Double> attrValueSet(int attrIndex) {
        if (this.attrValueSetList == null) {
            List<SortedSet<Double>> res = new ArrayList<>(this.dimensionality);
            for (int i = 0; i < this.dimensionality; i++) {
                res.add(new TreeSet<>());
            }
            for (Instance instance: this.instances) {
                for (int i = 0; i < this.dimensionality; i++) {
                    res.get(i).add(instance.attribute(i));
                }
            }
            this.attrValueSetList = res;
        }
        return this.attrValueSetList.get(attrIndex);
    }

    @Override
    public double[] classValues() {
        double[] res = new double[this.instances.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = this.instances.get(i).classValue();
        }
        return res;
    }

    @Override
    public double[] attrValues(int attrIndex) {
        double[] res = new double[this.instances.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = this.instances.get(i).attribute(attrIndex);
        }
        return res;
    }

    @Override
    public void add(Instance instance) {
        this.check(instance);
        this.instances.add(instance);
        instance.setDataSet(this);
        this.resetSortedSet();
    }

    /**
     * Checks if the given instance is compatible with this dataset before {@code add}.
     * Only looks at the dimensionality of the instance.
     *
     * @param instance the instance to be appended to this data set
     * @throws DimensionNotMatchedException if the instance's dimensionality is different from this data set's
     */
    private void check(Instance instance) {
        if (this.dimensionality != instance.dimensionality()) {
            String msg = "The data set has " + this.dimensionality +
                    " attributes, but the instance has " + instance.dimensionality() + " attributes.";
            throw new DimensionNotMatchedException(msg);
        }
    }

    @Override
    public void addAll(Collection<? extends Instance> instances) {
        for (Instance instance: instances) {
            this.check(instance);
        }
        this.instances.addAll(instances);
        for (Instance instance: instances) {
            instance.setDataSet(this);
        }
        this.resetSortedSet();
    }

    /**
     * Resets classSet and attrValueSetList to null after instances are modified.
     * Why reset? The getters of classSet and attrValueSetList are like "snapshot".
     */
    private void resetSortedSet() {
        this.classSet = null;
        this.attrValueSetList = null;
    }

    @Override
    public Iterator<Instance> iterator() {
        return this.instances.iterator();
    }

    /**
     * Returns a deep copy of this data set.
     * This method also creates new copies of instances in this data set.
     * But the other member variables are just shallowly copied.
     *
     * @return a deep copy of this data set
     */
    @Override
    public StandardDataSet copy() {
        StandardDataSet res = new StandardDataSet();
        res.classInfo = this.classInfo;
        res.attributeInfoList = this.attributeInfoList;
        res.dimensionality = this.dimensionality;
        res.classSet = this.classSet;
        res.attrValueSetList = this.attrValueSetList;
        ArrayList<Instance> copyInstances = new ArrayList<>();
        for (Instance instance: this.instances) {
            Instance copyInstance = instance.copy();
            copyInstances.add(copyInstance);
            copyInstance.setDataSet(this);
        }
        res.instances = copyInstances;
        return res;
    }

    @Override
    public String toString() {
        String[] res = new String[this.instances.size()];
        int i = 0;
        for (Instance instance : this.instances) {
            res[i++] = instance.toString();
        }
        String prefix = "{" + System.lineSeparator() +
                String.join(System.lineSeparator(), res) +
                System.lineSeparator() + "} " +
                this.attributeInfoList.toString();
        if (this.classInfo != null) {
            return prefix + ";" + this.classInfo;
        } else {
            return prefix;
        }
    }
}
