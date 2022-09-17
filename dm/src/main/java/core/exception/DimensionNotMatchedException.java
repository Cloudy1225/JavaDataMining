package main.java.core.exception;

/**
 * Exception that is raised when {@link main.java.core.DataSet} and {@link main.java.core.Instance}
 * have different dimensionality.
 *
 * @author Cloudy1225
 */
public class DimensionNotMatchedException extends RuntimeException {

    /**
     * Creates a new UnassignedClassException.
     *
     * @param message the reason for raising an exception.
     */
    public DimensionNotMatchedException(String message) {
        super(message);
    }

}
