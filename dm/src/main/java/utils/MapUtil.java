package main.java.utils;

import java.util.Map;

/**
 * This class provides some static methods to manipulating maps.
 * Such as getting key by value.
 *
 * @author Cloudy1224
 */
public class MapUtil {

    /**
     * Returns the key of the first occurrence of the specified value in the given map,
     * or <tt>null</tt> if the map does not contain the value.
     *
     * @param map the target map
     * @param value the target value
     * @param <K> the type of keys maintained by the map
     * @param <V> the type of mapped values
     * @return the key of the first occurrence of the specified value in the given map,
     *         or <tt>null</tt> if the map does not contain the value
     */
    public static <K, V>  K keyOf(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry: map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Returns values of {@code Map<Double, Integer>} as an int array,
     * in the order they are returned by the map's iterator.
     *
     * @param map {@code Map<Double, Integer>}
     * @return values as an int array
     */
    public static int[] mapValues(Map<Double, Integer> map) {
        int[] res = new int[map.size()];
        int i = 0;
        for (Integer value: map.values()) {
            res[i++] = value;
        }
        return res;
    }

    /**
     * Returns the sum of values in a {@code Map<Double, Integer>}.
     *
     * @param map {@code Map<Double, Integer>}
     * @param <K> the key's type
     * @return sum of values
     */
    public static <K> int sumValues(Map<K, Integer> map) {
        int sum = 0;
        for (int i: map.values()) {
            sum += i;
        }
        return sum;
    }

}
