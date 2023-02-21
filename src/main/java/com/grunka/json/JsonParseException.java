package com.grunka.json;

/**
 * Exception that is thrown for any parsing error
 */
public class JsonParseException extends RuntimeException {
    /**
     * Constructs an exception with a message
     * @param message the exception message
     */
    public JsonParseException(String message) {
        super(message);
    }

    /**
     * Constructs an exception with a message and a cause
     * @param message the exception message
     * @param cause the exception cause
     */
    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
