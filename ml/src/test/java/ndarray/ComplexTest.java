package test.java.ndarray;

import main.java.matrix.Complex;
import org.junit.Assert;
import org.junit.Test;

/**
 * It tests some methods in Complex.
 *
 * @author Cloudy1225
 */
public class ComplexTest {

    private static final double delta = 1e-6;

    @Test
    public void testToString() {
        Complex complex1 = new Complex(2, -3);
        Complex complex2 = new Complex(2.5, 3.9);
        Complex complex3 = new Complex(0, -0.8);
        Complex complex4 = new Complex(1);
        Complex complex5 = new Complex(0, 0.8);
        Complex complex6 = Complex.ZERO;
        System.out.println(complex1);
        System.out.println(complex2);
        System.out.println(complex3);
        System.out.println(complex4);
        System.out.println(complex5);
        System.out.println(complex6);
    }

    @Test
    public void testDiv() {
        Complex complex1 = new Complex(10, -10);
        Complex complex2 = new Complex(3, 4);
        Complex res = complex1.div(complex2);
        System.out.println(res);
        Assert.assertTrue(res.eq(new Complex(-0.4, -2.8)));
        try {
            complex1.div(Complex.ZERO);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSqrt() {
        Complex complex1 = new Complex(4);
        Assert.assertTrue(complex1.sqrt().eq(new Complex(2)));
        Complex complex2 = new Complex(1, 4);
        Complex sqrt = complex2.sqrt();
        System.out.println(sqrt);
        Complex mul = sqrt.mul(sqrt);
        System.out.println(mul);
        Assert.assertTrue(complex2.eq(mul));
    }

    @Test
    public void testArg() {
        Complex complex = new Complex(1, 1);
        double angel = complex.arg();
        System.out.println(complex.arg());
        Assert.assertEquals(complex.arg()*4, Math.PI, delta);
    }

    @Test
    public void testParseComplex() {
        Complex complex1 = Complex.parseComplex("1+5i");
        Complex complex2 = Complex.parseComplex("12.j+5");
        Complex complex3 = Complex.parseComplex("1e-3-2e3i");
        Complex complex4 = Complex.parseComplex("0+1.j");
        Complex complex5 = Complex.parseComplex(".5");
        System.out.println(complex1);
        System.out.println(complex2);
        System.out.println(complex3);
        System.out.println(complex4);
        System.out.println(complex5);
        try {
            Complex.parseComplex("0.2+ 3j");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            Complex.parseComplex("0.2+3ja");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

}
