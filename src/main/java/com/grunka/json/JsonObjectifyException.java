package com.grunka.json;

/**
 * Exception thrown when converting a JsonValue to an object instance fails
 */
public class JsonObjectifyException extends RuntimeException {
    /**
     * Constructs an exception with a message
     * @param message the exception message
     */
    public JsonObjectifyException(String message) {
        super(message);
    }

    /**
     * Constructs an exception with a message and a cause
     * @param message the exception message
     * @param cause the exception cause
     */
    public JsonObjectifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
