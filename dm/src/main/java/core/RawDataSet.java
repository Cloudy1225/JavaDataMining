package main.java.core;

import main.java.core.exception.DimensionNotMatchedException;
import main.java.preprocessing.encoding.LabelEncoder;

import java.util.List;

/**
 * This class is used to hold raw data loaded from a file.
 * It is just a wrapper of {@code List<String[]>}.
 *
 * @author Cloudy1225
 * @see DataSet
 */
public class RawDataSet {

    /**
     * Holds raw instances.
     */
    private final List<String[]> data;

    /**
     * Holds class information.
     */
    private AttributeInfo classInfo;

    /**
     * Holds each attribute's information.
     */
    private final AttributeInfo[] attributeInfoArray;


    /**
     * Creates a new raw unlabeled data set with given data.
     * The data's dimensionality should equal the number of attributes.
     *
     * @param data raw data
     * @param attributeInfoArray attributes' information
     * @throws DimensionNotMatchedException if the data's dimensionality doesn't equal the number of attributes
     */
    public RawDataSet(List<String[]> data, AttributeInfo[] attributeInfoArray) {
        if (data.get(0).length != attributeInfoArray.length) {
            throw new DimensionNotMatchedException("The data's dimensionality should equal the number of attributes.");
        }
        this.data = data;
        this.attributeInfoArray = attributeInfoArray;
    }

    /**
     * Creates a new raw labeled data set with given data.
     * The data's dimensionality should equal (the number of attributes + 1).
     *
     * @param data raw data
     * @param attributeInfoArray attributes' information
     * @param classInfo class information 
     * @throws DimensionNotMatchedException if the data's dimensionality doesn't equal (the number of attributes + 1)
     */
    public RawDataSet(List<String[]> data, AttributeInfo[] attributeInfoArray, AttributeInfo classInfo) {
        if (data.get(0).length != attributeInfoArray.length+1) {
            throw new DimensionNotMatchedException("The data's dimensionality should equal (the number of attributes + 1).");
        }
        this.data = data;
        this.attributeInfoArray = attributeInfoArray;
        this.classInfo = classInfo;
    }

    /**
     * Converts this raw data set to {@link StandardDataSet}.
     *
     * @return a standard data set
     * @throws NumberFormatException if the string of a numeric attribute value does not have the appropriate format
     */
    public DataSet toDataSet() {
        StandardDataSet res = new StandardDataSet(this.attributeInfoArray, this.classInfo);
        int dimension = this.attributeInfoArray.length;
        int instanceNum = this.data.size();
        double[][] attributes = new double[instanceNum][dimension];
        for (int j = 0; j < dimension; j++) {
            if (attributeInfoArray[j].type == AttributeInfo.NUMERIC) {
                for (int i = 0; i < instanceNum; i++) {
                    attributes[i][j] = Double.parseDouble(data.get(i)[j]);
                }
            } else { // categorical
                LabelEncoder<String> encoder = new LabelEncoder<>();
                String[] values = new String[instanceNum];
                for (int i = 0; i < instanceNum; i++) {
                    values[i] = data.get(i)[j];
                }
                double[] encodings = encoder.fitTransform(values);
                for (int i = 0; i < instanceNum; i++) {
                    attributes[i][j] = encodings[i];
                }
                // categorical属性需要存储映射
                this.attributeInfoArray[j].encodingMap = encoder.getEncodingMap();
            }
        }
        if (this.classInfo != null) { // labeled
            double[] classValues;
            if (this.classInfo.type == AttributeInfo.NUMERIC) {
                classValues = new double[instanceNum];
                for (int i = 0; i < instanceNum; i++) {
                    classValues[i] = Double.parseDouble(data.get(i)[dimension]);
                }
            } else { // categorical
                LabelEncoder<String> encoder = new LabelEncoder<>();
                String[] classes = new String[instanceNum];
                for (int i = 0; i < instanceNum; i++) {
                   classes[i] = data.get(i)[dimension];
                }
                classValues = encoder.fitTransform(classes);
                // categorical属性需要存储映射
                classInfo.encodingMap = encoder.getEncodingMap();
            }
            for (int i = 0; i < instanceNum; i++) {
                double[] attributeValues = new double[dimension];
                System.arraycopy(attributes[i], 0, attributeValues, 0, dimension);
                DenseInstance instance = new DenseInstance(attributeValues, classValues[i]);
                res.add(instance);
            }
        } else { // unlabeled
            for (int i = 0; i < instanceNum; i++) {
                double[] attributeValues = new double[dimension];
                System.arraycopy(attributes[i], 0, attributeValues, 0, dimension);
                DenseInstance instance = new DenseInstance(attributeValues);
                res.add(instance);
            }
        }
        return res;
    }
}
