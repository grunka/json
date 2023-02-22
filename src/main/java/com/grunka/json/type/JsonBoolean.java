package com.grunka.json.type;

import java.util.Objects;

/**
 * Representation of a JSON boolean value
 */
public class JsonBoolean implements JsonValue, Comparable<JsonBoolean> {
    /**
     * The only instance for the 'true' boolean value
     */
    public static final JsonBoolean TRUE = new JsonBoolean(true);
    /**
     * The only instance for the 'false' boolean value
     */
    public static final JsonBoolean FALSE = new JsonBoolean(false);
    private final boolean value;

    private JsonBoolean(boolean value) {
        this.value = value;
    }

    /**
     * Gets the value of the {@link JsonBoolean}
     * @return true if true
     */
    public boolean isTrue() {
        return value;
    }

    @Override
    public boolean isBoolean() {
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
        JsonBoolean that = (JsonBoolean) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    /**
     * Gets the value of the {@link JsonBoolean} as an {@link Object}
     * @return true if true
     */
    public Boolean getBoolean() {
        return value;
    }

    @Override
    public int compareTo(JsonBoolean o) {
        return Boolean.compare(this.value, o.value);
    }
}
