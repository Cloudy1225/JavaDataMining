package main.java.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * This class provides some static methods as complements of {@link Math}.
 *
 * @author Cloudy1225
 */
public class MathUtil {

    /**
     * The absolute tolerance parameter fot comparing doubles.
     */
    public static final double epsilon = 1e-6;

    /**
     * Tests if a is equal to b within a tolerance.
     */
    public static boolean eq(double a, double b) {
        return ((a - b < epsilon) && (b - a < epsilon)) || (Double.isNaN(a) && Double.isNaN(b));
    }

    /**
     * Returns the logarithm (base <i>2</i>) of a {@code double} value.
     *
     * @param a a value
     * @return the value log2 {@code a}
     */
    public static double log2(double a) {
        return Math.log(a) / Math.log(2);
    }

    /**
     * Judges whether an attribute is continuous.
     * How? If (N_c / N >= rate), the attribute is continuous.
     * N_c: the number of different values;
     * N: the number of values;
     * rate is default 30%.
     *
     * @param data an array contains values
     * @return true / false
     */
    public static boolean isContinuous(double[] data) {
        return MathUtil.isContinuous(data, 0.3);
    }

    /**
     * Judges whether an attribute is continuous with the given rate.
     * How? If (N_c / N >= rate), the attribute is continuous.
     * N_c: the number of different values;
     * N: the number of values;
     * rate is default 30%.
     *
     * @param data an array contains values
     * @param rate 0 < rate <= 1
     * @return true / false
     * @throws IllegalArgumentException if rate > 1 or rate <= 0
     */
    public static boolean isContinuous(double[] data, double rate) {
        if (rate <= 0 || rate > 1) {
            throw new IllegalArgumentException("rate must > 0 and <= 1");
        }
        if (data.length == 0) return false;
        Set<Double> set = new HashSet<>();
        for (double d: data) {
            set.add(d);
        }
        return ((double) set.size() / data.length) >= rate;
    }
}
