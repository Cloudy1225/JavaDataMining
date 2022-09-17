package main.java.utils.io;

import main.java.core.*;
import org.junit.Assert;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;

/**
 * A class to load data sets from file and write them back.
 * <p>
 * The format of the file should be as follows:
 * 1. the file can have comment lines and blank lines that will be ignored;
 * 2. the first effective line must indicate each attribute's name;
 * 4. the second effective line must indicate each attribute's type:
 * {categorical, numeric}, you can find details in {@link AttributeInfo};
 * 5. one instance on each line;
 * 6. all lines should have the same number of values;
 * 7. the class value must at the end of each line;
 * 8. values should be separated by the same separator such as ","...
 * <p>
 *
 * @author Cloudy1225
 * @see DataSet
 * @see RawDataSet
 * @see AttributeInfo
 */
public class FileTool {

    /**
     * Loads a data set from a formatted file.
     * This method will load a {@link RawDataSet} first and then transform it to {@link StandardDataSet}.
     *
     * @param file the source file
     * @param separator the value separator on a line
     * @param labeled whether the data set is labeled or not
     * @return a data set
     * @throws IOException If an I/O error occurs
     */
    public static DataSet loadDataSet(File file, String separator, boolean labeled) throws IOException {
        if (labeled) {
            return loadLabeledDataSet(file, separator);
        } else {
            return loadUnlabeledDataSet(file, separator);
        }
    }

    /**
     * Loads a labeled data set from a formatted and labeled file.
     * This method will load a {@link RawDataSet} first and then transform it to {@link StandardDataSet}.
     *
     * @param file the source file
     * @param separator the value separator on a line
     * @return a labeled data set
     * @throws IOException if an I/O error occurs
     * @throws NumberFormatException if a value is not numeric
     */
    public static DataSet loadLabeledDataSet(File file, String separator) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream(file));
        LineIterator it = new LineIterator(in, true, true);
        String[] firstLine = it.next().split(separator);
        String[] secondLine = it.next().split(separator);
        int dimension = firstLine.length - 1;
        AttributeInfo[] attributeInfoArray = new AttributeInfo[dimension];
        for (int i = 0; i < dimension; i++) {
            attributeInfoArray[i] = new AttributeInfo(firstLine[i], parseType(secondLine[i]), i);
        }
        AttributeInfo classInfo = new AttributeInfo(firstLine[dimension], parseType(secondLine[dimension]), -1);
        LinkedList<String[]> data = new LinkedList<>();
        while (it.hasNext()) {
            String line = it.next();
            data.add(line.split(separator));
        }
        RawDataSet raw = new RawDataSet(data, attributeInfoArray, classInfo);
        return raw.toDataSet();
    }

    /**
     * Loads an unlabeled data set from a formatted but unlabeled file.
     * This method will load a {@link RawDataSet} first and then transform it to {@link StandardDataSet}.
     *
     * @param file the source file
     * @param separator the value separator on a line
     * @return an unlabeled data set
     * @throws IOException if an I/O error occurs
     * @throws NumberFormatException if a value is not numeric
     */
    public static DataSet loadUnlabeledDataSet(File file, String separator) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream(file));
        LineIterator it = new LineIterator(in, true, true);
        String[] firstLine = it.next().split(separator);
        String[] secondLine = it.next().split(separator);
        int dimension = firstLine.length;
        AttributeInfo[] attributeInfoArray = new AttributeInfo[dimension];
        for (int i = 0; i < dimension; i++) {
            attributeInfoArray[i] = new AttributeInfo(firstLine[i], parseType(secondLine[i]), i);
        }
        LinkedList<String[]> data = new LinkedList<>();
        while (it.hasNext()) {
            String line = it.next();
            data.add(line.split(separator));
        }
        RawDataSet raw = new RawDataSet(data, attributeInfoArray);
        return raw.toDataSet();
    }

    /**
     * Loads a data set from a formatted file.
     * This method will load a {@link RawDataSet} first and then transform it to {@link StandardDataSet}.
     * This is faster than {@code loadDataSet}.
     *
     * @param file the source file
     * @param separator the value separator on a line
     * @param labeled whether the data set is labeled or not
     * @return a data set
     * @throws IOException If an I/O error occurs
     * @throws NumberFormatException if a value is not numeric
     * @deprecated This method only supports the data set with numeric attributes and class
     */
    public static DataSet loadNumericDataSet(File file, String separator, boolean labeled) throws IOException {
        if (labeled) {
            return loadNumericLabeledDataSet(file, separator);
        } else {
            return loadNumericUnlabeledDataSet(file, separator);
        }
    }

    /**
     * Loads a labeled and numeric data set from a formatted and labeled file.
     * This is faster than {@code loadLabeledDataSet}.
     *
     * @param file the source file
     * @param separator the value separator on a line
     * @return a labeled data set
     * @throws IOException if an I/O error occurs
     * @throws NumberFormatException if the attribute is not numeric
     * @deprecated This method only supports the data set with numeric attributes and class
     */
    public static DataSet loadNumericLabeledDataSet(File file, String separator) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream(file));
        LineIterator it = new LineIterator(in, true, true);
        String[] firstLine = it.next().split(separator);
        String[] secondLine = it.next().split(separator);
        int dimension = firstLine.length - 1;
        AttributeInfo[] attributeInfos = new AttributeInfo[dimension];
        for (int i = 0; i < dimension; i++) {
            attributeInfos[i] = new AttributeInfo(firstLine[i], (byte) 1, i);
        }
        AttributeInfo classInfo = new AttributeInfo(firstLine[dimension], (byte) 1, -1);
        StandardDataSet res = new StandardDataSet(attributeInfos, classInfo);
        while (it.hasNext()) {
            String line = it.next();
            String[] words = line.split(separator);
            double[] attributeValues = new double[dimension];
            for (int i = 0; i < dimension; i++) {
                attributeValues[i] = Double.parseDouble(words[i]);
            }
            double classValue = Double.parseDouble(words[dimension]); // 未检查NaN的情况
            res.add(new DenseInstance(attributeValues, classValue));
        }
        return res;
    }

    /**
     * Loads an unlabeled and numeric data set from a formatted and unlabeled file.
     * This is faster than {@code loadUnlabeledDataSet}.
     * @param file the source file
     * @param separator the value separator on a line
     * @return an unlabeled data set
     * @throws IOException if an I/O error occurs
     * @throws NumberFormatException if the attribute is not numeric
     * @deprecated This method only supports the data set with numeric attributes and class
     */
    public static DataSet loadNumericUnlabeledDataSet(File file, String separator) throws IOException {
        InputStreamReader in = new InputStreamReader(new FileInputStream(file));
        LineIterator it = new LineIterator(in, true, true);
        String[] firstLine = it.next().split(separator);
        String[] secondLine = it.next().split(separator);
        int dimension = firstLine.length;
        AttributeInfo[] attributeInfos = new AttributeInfo[dimension];
        for (int i = 0; i < dimension; i++) {
            attributeInfos[i] = new AttributeInfo(firstLine[i], (byte) 1, i);
        }
        StandardDataSet res = new StandardDataSet(attributeInfos);
        while (it.hasNext()) {
            String line = it.next();
            String[] words = line.split(separator);
            double[] attributeValues = new double[dimension];
            for (int i = 0; i < dimension; i++) {
                attributeValues[i] = Double.parseDouble(words[i]);
            }
            res.add(new DenseInstance(attributeValues));
        }
        return res;
    }

    /**
     * Loads and returns the iris dataset.
     * The iris dataset is a classic and very easy multi-class classification dataset.
     *
     * @return the iris dataset
     */
    public static DataSet loadIris() {
        String projectDir = FileTool.getProjectDir();
        String filePath = "dm/data/numeric/iris.csv";
        File file = new File(projectDir+filePath);
        DataSet res = null;
        try{
            res = FileTool.loadDataSet(file, ",", true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        return res;
    }

    /**
     * Loads and returns the breast cancer wisconsin dataset.
     * The breast cancer dataset is a classic and very easy binary classification dataset.
     *
     * @return the breast cancer wisconsin dataset
     */
    public static DataSet loadBreastCancer() {
        String projectDir = FileTool.getProjectDir();
        String filePath = "dm/data/numeric/breast_cancer.csv";
        File file = new File(projectDir+filePath);
        DataSet res = null;
        try{
            res = FileTool.loadDataSet(file, ",", true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        return res;
    }

    /**
     * Loads and returns the tic-tac-toe dataset.
     * We can predict who will win the game.
     *
     * @return the tic-tac-toe dataset
     */
    public static DataSet loadTicTacToe() {
        String projectDir = FileTool.getProjectDir();
        String filePath = "dm/data/categorical/tic-tac-toe.csv";
        File file = new File(projectDir+filePath);
        DataSet res = null;
        try{
            res = FileTool.loadDataSet(file, ",", true);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
        return res;
    }

    /**
     * Gets this project's direction.
     * Reference: https://stackoverflow.com/questions/27705216/java-read-file-within-static-method-using-classloader-gives-filenotfoundexcepti
     *
     * @return path string
     */
    private static String getProjectDir() {
        try {
            Class<?> callingClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            URL url = callingClass.getProtectionDomain().getCodeSource().getLocation();
            URI parentDir = url.toURI().resolve("../../../"); // 可以个性化定制
            return parentDir.getPath();
        } catch (ClassNotFoundException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Returns if given string is "categorical" or "numeric".
     *
     * @param str a string to check
     * @return 1: numeric; 0: categorical
     * @throws IllegalArgumentException if the string is neither “categorical” nor "numeric".
     */
    private static byte parseType(String str) {
        if (str.startsWith("cat")) {
            return 0;
        } else if (str.startsWith("num")) {
            return 1;
        } else {
            throw new IllegalArgumentException("Words of the second effective line must be 'numeric' or 'categorical'");
        }
    }
}
