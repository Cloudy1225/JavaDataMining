package main.java.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class provides some static methods to manipulating sets.
 * Such as getting subsets.
 *
 * @author Cloudy1224
 */
public class SetUtil {

    /**
     * Gets the subsets of given set.
     *
     * @param set target set
     * @param <E> element's type
     * @return an array of subsets
     */
    public static <E> Set<E>[] subSets(Set<E> set) {
        int len = (int) Math.pow(2, set.size());
        Set<E>[] res = new Set[len];
        for (int i = 0; i < len; i++) {
            res[i] = new HashSet<>();
        }
        Iterator<E> iter = set.iterator();
        for (int i = 2; i <= len; i*=2) {
            E elem = iter.next();
            int step = len / i;
            for (int j = 0; j < i; j++) {
                if (j % 2 == 0) {
                    int start = j * step;
                    for (int k = 0; k < step; k++) {
                        res[start + k].add(elem);
                    }
                }
            }
        }
        return res;
    }

    /**
     * Gets the relative complement of A in B.
     *
     * @param front B
     * @param behind A
     * @param <E> element's type
     * @return front - behind
     */
    public static <E> Set<E> except(Set<E> front, Set<E> behind) {
        Set<E> res = new HashSet<>();
        for (E e: front) {
            if (!behind.contains(e)) {
                res.add(e);
            }
        }
        return res;
    }
}
