package main.java.core.exception;

/**
 * Exception that is raised when trying to use some data that has no
 * class assigned to it, but a class is needed to perform the operation.
 *
 * @author Cloudy1225
 */
public class UnassignedClassException extends RuntimeException {

    /**
     * Creates a new UnassignedClassException with no message.
     */
    public UnassignedClassException() {
        super();
    }

    /**
     * Creates a new UnassignedClassException.
     *
     * @param message the reason for raising an exception.
     */
    public UnassignedClassException(String message) {
        super(message);
    }

}
