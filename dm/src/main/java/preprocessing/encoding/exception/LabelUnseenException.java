package main.java.preprocessing.encoding.exception;

/**
 * Exception that is raised when trying to transform a label that wasn't encoded to normalized encoding.
 *
 * @author Cloudy1225
 * @see main.java.preprocessing.encoding.LabelEncoder
 */
public class LabelUnseenException extends RuntimeException {

    /**
     * Creates a new LabelUnseenException with given message.
     *
     * @param message the reason for raising an exception.
     */
    public LabelUnseenException(String message) {
        super(message);
    }

}
