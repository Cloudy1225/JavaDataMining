package main.java.core;

/**
 * Interface implemented by classes that can produce copies
 * of their objects.
 *
 * @param <T> the type of class
 *
 * @author Cloudy1225
 */
public interface Copyable<T> {

    /**
     * Creates and returns a copy of this object.
     * But "deep" or "shallow" is decided by concrete implementation.
     *
     * @return a copy of this object
     */
    T copy();


}
