package com.grunka.json.type;

/**
 * Base class representing any JSON value
 */
public abstract class JsonValue {
    JsonValue() {
    }

    /**
     * Checks if the value is a JSON null
     * @return true if the value is {@link JsonNull}
     */
    public boolean isNull() {
        return false;
    }

    /**
     * Checks if the value is a JSON boolean value
     * @return true if this instance is a {@link JsonBoolean}
     */
    public boolean isBoolean() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonBoolean}
     * @return this instance cast to a {@link JsonBoolean}
     * @throws ClassCastException if this instance is not a {@link JsonBoolean}
     */
    public JsonBoolean asBoolean() {
        return (JsonBoolean) this;
    }

    /**
     * Checks if the value is a JSON string value
     * @return true if this instance is a {@link JsonString}
     */
    public boolean isString() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonString}
     * @return this instance cast to a {@link JsonString}
     * @throws ClassCastException if this instance is not a {@link JsonString}
     */
    public JsonString asString() {
        return (JsonString) this;
    }

    /**
     * Checks if the value is a JSON number value
     * @return true if this instance is a {@link JsonNumber}
     */
    public boolean isNumber() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonNumber}
     * @return this instance cast to a {@link JsonNumber}
     * @throws ClassCastException if this instance is not a {@link JsonNumber}
     */
    public JsonNumber asNumber() {
        return (JsonNumber) this;
    }

    /**
     * Checks if the value is a JSON array
     * @return true if this instance is a {@link JsonArray}
     */
    public boolean isArray() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonArray}
     * @return this instance cast to a {@link JsonArray}
     * @throws ClassCastException if this instance is not a {@link JsonArray}
     */
    public JsonArray asArray() {
        return (JsonArray) this;
    }

    /**
     * Checks if the value is a JSON object
     * @return true if this instance is a {@link JsonObject}
     */
    public boolean isObject() {
        return false;
    }

    /**
     * Checks if the value is a JSON primitive
     * @return true if this instance is a {@link JsonBoolean}, {@link JsonString}, {@link JsonNumber}, or {@link JsonNull}
     */
    public boolean isPrimitive() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonObject}
     * @return this instance cast to a {@link JsonObject}
     * @throws ClassCastException if this instance is not a {@link JsonObject}
     */
    public JsonObject asObject() {
        return (JsonObject) this;
    }
}
