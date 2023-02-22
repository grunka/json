package com.grunka.json.type;

/**
 * Representation of a JSON null value
 */
public class JsonNull implements JsonValue {
    /**
     * The only instance for the 'null' value
     */
    public static final JsonNull NULL = new JsonNull();

    private JsonNull() {
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public String toString() {
        return "null";
    }
}
