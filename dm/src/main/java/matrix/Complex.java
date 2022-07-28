package main.java.matrix;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A complex value with double precision.
 *
 * @author Cloudy1225
 */
public class Complex {

    private double real, imag;

    /***** Constant Variables *****/

    public static final Complex REAL_UNIT = new Complex(1.0, 0.0);
    public static final Complex NEG_REAL_UNIT = new Complex(-1.0, 0.0);
    public static final Complex IMAG_UNIT = new Complex(0.0, 1.0);
    public static final Complex NEG_IMAG_UNIT = new Complex(0.0, -1.0);
    public static final Complex ZERO = new Complex(0.0);

    /***** Constructors *****/

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public Complex(double real) {
        this.real = real;
        this.imag = 0;
    }

    public Complex() {
        this.real = 0;
        this.imag = 0;
    }

    /***** getter and setter *****/

    public double real() {
        return this.real;
    }

    public double imag() {
        return this.imag;
    }

    protected void set(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    protected void setReal(double real) {
        this.real = real;
    }

    protected void setImag(double imag) {
        this.imag = imag;
    }

    /**
     * Returns the value of the specified complex as a double.
     * It may lose the imaginary part.
     *
     * @return the real part of this complex
     */
    public double doubleValue() {
        return this.real;
    }

    /**
     * Constructs a newly allocated Complex object that
     * represents the complex value represented by the double.
     *
     * @param  d  a double to be converted to a complex.
     */
    public static Complex valueOf(double d) {
        return new Complex(d);
    }

    /**
     * Constructs a newly allocated Complex object that
     * represents the complex value represented by the string.
     *
     * @param  s  a string to be converted to a complex.
     * @throws    NumberFormatException  if the string does not contain a
     *            parsable number.
     */
    public static Complex valueOf(String s) {
        return parseComplex(s);
    }

    /**
     * Constructs a newly allocated Complex object that
     * represents the complex value represented by the string.
     *
     * @param  s  a string to be converted to a complex.
     * @throws    NumberFormatException  if the string does not contain a
     *            parsable number.
     */
    public static Complex parseComplex(String s) {
        double real = 0;
        double imag = 0;

        final String doubleRegex = "[-+]?(\\d+(\\.\\d*)?|\\.\\d+)([eE]([-+]?([012]?\\d{1,2}|30[0-7])|-3([01]?[4-9]|[012]?[0-3])))?";
        final String pureImagRegex = doubleRegex+"[a-zA-Z]";
        final String complexRegex1 = "(?<real>"+doubleRegex+")" + "(?<imag>"+doubleRegex+")" + "[a-zA-Z]";
        final String complexRegex2 = "(?<imag>"+doubleRegex+")" + "[a-zA-Z]" + "(?<real>"+doubleRegex+")";

        if (Pattern.compile(doubleRegex).matcher(s).matches()) { // It's just a real number.
            real = Double.parseDouble(s);
        } else if (Pattern.compile(pureImagRegex).matcher(s).matches()) { // It's just a pure imaginary number.
            imag = Double.parseDouble(s.substring(0, s.length()-1));
        } else {
            Matcher matcher = Pattern.compile(complexRegex1).matcher(s);
            if (matcher.matches()) { // It's like '20.02+12.25i'.
                real = Double.parseDouble(matcher.group("real"));
                imag = Double.parseDouble(matcher.group("imag"));
            } else {
                matcher = Pattern.compile(complexRegex2).matcher(s);
                if(matcher.matches()) { // It's like '12.25i+20.02'.
                    real = Double.parseDouble(matcher.group("real"));
                    imag = Double.parseDouble(matcher.group("imag"));
                } else {
                    throw new NumberFormatException("\""+ s + "\" can't be translated as a complex.");
                }
            }
        }
        return new Complex(real, imag);
    }

    /***** Override some methods in Object *****/

    @Override
    public String toString() {
        if (this.imag > 0) {
            return ((real==0) ? "" : real+"+") + imag + "i";
        } else if (this.imag < 0) {
            return ((real==0) ? "" : real+"") + imag + "i";
        } else {
            return String.valueOf(real);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Complex)) return false;
        Complex other = (Complex) obj;
        return this.real == other.real && this.imag == other.imag;
    }

    @Override
    public int hashCode() {
        return Double.valueOf(this.real).hashCode() ^ Double.valueOf(this.imag).hashCode();
    }

    /**
     * Create a deep copy of the complex
     *
     * @return deep copy of the complex
     */
    public Complex copy() {
        return new Complex(this.real, this.imag);
    }


    /***** Arithmetic Operations *****/

    /**
     * Does this equal another complex within the allowed error range 1e-6?
     *
     * @param complex complex to be compared with
     * @return true / false
     */
    public boolean eq(Complex complex) {
        return Math.abs(real-complex.real)+Math.abs(imag-complex.imag) < 1e-6;
    }

    /**
     * Returns a new Complex whose value is {this + augend}
     *
     * @param augend complex to be added to this
     * @return {this + augend}
     */
    public Complex add(Complex augend) {
        return new Complex(this.real+augend.real, this.imag+augend.imag);
    }

    /**
     * Returns a new Complex whose value is {this + real}
     *
     * @param real real number to be added to this
     * @return {this + real}
     */
    public Complex add(double real) {
        return new Complex(this.real+real, this.imag);
    }

    /**
     * Returns a new Complex whose value is {this - subtrahend}
     *
     * @param subtrahend complex to be subtracted from this
     * @return {this - subtrahend}
     */
    public Complex sub(Complex subtrahend) {
        return new Complex(this.real-subtrahend.real, this.imag-subtrahend.imag);
    }

    public Complex sub(double real) {
        return new Complex(this.real-real, this.imag);
    }

    /**
     * Returns a new Complex whose value is {this * multiplicand}
     *
     * @param multiplicand complex to be multiplied by this
     * @return {this * multiplicand}
     */
    public Complex mul(Complex multiplicand) {
        double real = this.real*multiplicand.real - this.imag*multiplicand.imag;
        double imag = this.real*multiplicand.imag + this.imag*multiplicand.real;
        return new Complex(real, imag);
    }

    public Complex mul(double real) {
        return new Complex(this.real*real, this.imag*real);
    }

    /**
     * Returns a new Complex whose value is {this / divisor}
     *
     * @param divisor complex by which this is to be divided
     * @return {this / divisor}
     * @throws ArithmeticException if /0
     */
    public Complex div(Complex divisor) {
        double denominator = divisor.real*divisor.real + divisor.imag*divisor.imag;
        if (denominator == 0) {
            throw new ArithmeticException("Complex division by zero!");
        }
        double real = (this.real*divisor.real + this.imag*divisor.imag) / denominator;
        double imag = (this.imag*divisor.real - this.real*divisor.imag) / denominator;
        return new Complex(real, imag);
    }

    public Complex div(double real) {
        if (real == 0) {
            throw new ArithmeticException("Complex division by zero!");
        }
        return new Complex(this.real/real, this.imag/real);
    }

    /**
     * Returns the modulus value of a complex number ( 复数的模 )
     *
     * @return length of the vector in 2d plane
     */
    public double abs() {
        return Math.sqrt(real*real + imag*imag);
    }

    /**
     * Returns the modulus value of a complex number ( 复数的模 )
     *
     * @return length of the vector in 2d plane
     */
    public double mod() {
        return Math.sqrt(real*real + imag*imag);
    }

    /**
     * Returns the argument of a complex number ( 复数的辐角 )
     *
     * @return angle in radians of the vector in 2d plane (-PI~PI)
     */
    public double arg() {
        return Math.atan2(this.imag, this.real);
    }

    /**
     * Returns the inverse of a complex number ( 复数的倒数 )
     *
     * @return inverse of this
     * @throws ArithmeticException ZERO has no inverse.
     */
    public Complex inv() {
        double denominator = real*real + imag*imag;
        if (denominator == 0) {
            throw new ArithmeticException("ZERO has no inverse!");
        }
        double real = this.real / denominator;
        double imag = this.imag / denominator;
        return new Complex(real, imag);
    }

    /**
     * Returns  the numerical negative value of a complex number
     *
     * @return {0 - this}
     */
    public Complex neg() {
        return new Complex(-this.real, -this.imag);
    }

    /**
     * Returns the conjugate of a complex number
     *
     * @return conjugate of this
     */
    public Complex conj() {
        return new Complex(this.real, -this.imag);
    }

    /**
     * Returns the square root of a complex number
     *
     * @return square root of this
     */
    public Complex sqrt() {
        double mod = this.mod();
        double sqrt2 = Math.sqrt(2);
        double real =  Math.sqrt(mod + this.real) / sqrt2;
        double sgn = Math.signum(this.imag);
        if (sgn == 0.0) {
            sgn = 1.0;
        }
        double imag = Math.sqrt(mod - this.real) / sqrt2 * Math.signum(sgn);
        return new Complex(real, imag);
    }

    /**
     * Is this exactly ZERO?
     *
     * @return true / false
     */
    public boolean isZero() {
        return this.real == 0 && this.imag == 0;
    }

    /**
     * Is this a pure real number?
     *
     * @return true / false
     */
    public boolean isReal() {
        return this.imag == 0;
    }

    /**
     * Is this a pure imaginary number?
     *
     * @return true / false
     */
    public boolean isImag() {
        return this.real == 0;
    }

}
