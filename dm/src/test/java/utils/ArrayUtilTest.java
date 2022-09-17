package test.java.utils;

import main.java.utils.ArrayUtil;
import org.junit.Test;

import java.util.Arrays;

public class ArrayUtilTest {

    @Test
    public void testUnique() {
        double[] test = new double[] {2,2,1,33,4,5,1};
        double[] res = ArrayUtil.unique(test);
        System.out.println(Arrays.toString(res));
    }
}
