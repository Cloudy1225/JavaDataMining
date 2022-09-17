package main.java.preprocessing.encoding.exception;

/**
 * Exception that is raised when trying to transform an encoding untransformed to original label.
 *
 * @author Cloudy1225
 * @see main.java.preprocessing.encoding.LabelEncoder
 */
public class EncodingNotFoundException extends RuntimeException {

    /**
     * Creates a new EncodingNotFoundException with given message.
     *
     * @param message the reason for raising an exception.
     */
    public EncodingNotFoundException(String message) {
        super(message);
    }

}
