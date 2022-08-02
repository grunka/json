package com.grunka.json;

public class JsonStringifyException extends RuntimeException {
    public JsonStringifyException(String message) {
        super(message);
    }

    public JsonStringifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
