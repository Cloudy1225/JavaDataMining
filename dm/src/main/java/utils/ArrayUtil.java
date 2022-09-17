package main.java.utils;


import main.java.core.collection.DoubleList;

import java.util.*;

/**
 * This class contains various methods for creating, manipulating, and modifying arrays.
 *
 * @author Cloudy1225
 */
public class ArrayUtil {

    /**
     * Returns a new double array of given length, filled with ones.
     *
     * @param len the length of the new array
     * @return a double array of ones with the given length
     */
    public static double[] ones(int len) {
        double[] res = new double[len];
        for (int i = 0; i < len; i++) {
            res[i] = 1;
        }
        return res;
    }

    /**
     * Transforms a Double array to a double array.
     *
     * @param arr a Double array
     * @return a double array
     * @throws NullPointerException if given arr contains <tt>null</tt>
     */
    public static double[] doublize(Double[] arr) {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    /**
     * Computes the number of occurrences of each value in the given array.
     *
     * @param values a double array
     * @return a sorted {@code TreeMap<Double, Integer>}
     */
    public static TreeMap<Double, Integer> countDistMap(double[] values) {
        TreeMap<Double, Integer> res = new TreeMap<>();
        for (double value: values) {
            if (res.containsKey(value)) {
                int count = res.get(value);
                res.put(value, count+1);
            } else {
                res.put(value, 1);
            }
        }
        return res;
    }

    /**
     * Returns the index of the first occurrence of the specified element
     * int an array, or -1 if the array does not contain the element.
     *
     * @param array the array to indexOf in
     * @param element the element you want to {@code indexOf}
     * @return the index of the first occurrence, or -1
     */
    public static int indexOf(double[] array, double element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * int an array, or -1 if the array does not contain the element.
     *
     * @param array the array to lastIndexOf in
     * @param element the element you want to {@code lastIndexOf}
     * @return the index of the last occurrence, or -1
     */
    public static int lastIndexOf(double[] array, double element) {
        for (int i = array.length-1; i >= 0; i--) {
            if (array[i] == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the sorted unique elements of an array.
     *
     * @param values a double array
     * @return a double array containing the sorted unique values
     */
    public static double[] unique(double[] values) {
        TreeSet<Double> set = new TreeSet<>();
        for (double value: values) {
            set.add(value);
        }
        Double[] res = new Double[set.size()];
        set.toArray(res);
        return doublize(res);
    }

    /**
     * Returns the sum of given array.
     * This does not handle overflow.
     *
     * @param values an int array
     * @return sum of elements in given array
     */
    public static int sum(int[] values) {
        int sum = 0;
        for (int i: values) {
            sum += i;
        }
        return sum;
    }

    /**
     * Returns the sum of given array.
     * This does not handle overflow.
     *
     * @param values a double array
     * @return sum of elements in given array
     */
    public static double sum(double[] values) {
        double sum = 0;
        for (double d: values) {
            sum += d;
        }
        return sum;
    }
}
