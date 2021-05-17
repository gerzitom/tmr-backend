package cz.cvut.fel.tmr.exception;

public class ValidationException extends EarException{
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
