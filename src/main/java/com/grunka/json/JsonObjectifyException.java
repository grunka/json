package com.grunka.json;

public class JsonObjectifyException extends RuntimeException {
    public JsonObjectifyException(String message) {
        super(message);
    }

    public JsonObjectifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
