package com.grunka.json.type;

import java.util.Objects;

public class JsonString extends JsonValue {
    private final String value;

    public JsonString(String value) {
        this.value = Objects.requireNonNull(value, "Value cannot be null");
    }

    private static String encodeString(String contents) {
        return contents.replaceAll("\"", "\\\\\"");
    }

    @Override
    public String toString() {
        return '"' + encodeString(value) + '"';
    }

    public String getString() {
        return value;
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JsonString that = (JsonString) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
