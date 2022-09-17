package main.java.core.exception;

/**
 * Exception that is raised when trying to use the estimator before it is fitted.
 *
 * @author Cloudy1225
 */
public class EstimatorNotFittedException extends RuntimeException{

    /**
     * Creates a new EncoderNotFittedException with given message.
     *
     * @param message the reason for raising an exception.
     */
    public EstimatorNotFittedException(String message) {
        super(message);
    }
}
