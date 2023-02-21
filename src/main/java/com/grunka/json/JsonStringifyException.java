package com.grunka.json;

/**
 * Exception that is thrown for any errors that happen while making a string from an object
 */
public class JsonStringifyException extends RuntimeException {
    /**
     * Constructs an exception with a message
     * @param message the exception message
     */
    public JsonStringifyException(String message) {
        super(message);
    }

    /**
     * Constructs an exception with a message and a cause
     * @param message the exception message
     * @param cause the exception cause
     */
    public JsonStringifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
