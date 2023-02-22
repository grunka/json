package com.grunka.json.type;

/**
 * Base class representing any JSON value
 */
public interface JsonValue {
    /**
     * Checks if the value is a JSON null
     *
     * @return true if the value is {@link JsonNull}
     */
    default boolean isNull() {
        return false;
    }

    /**
     * Checks if the value is a JSON boolean value
     *
     * @return true if this instance is a {@link JsonBoolean}
     */
    default boolean isBoolean() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonBoolean}
     *
     * @return this instance cast to a {@link JsonBoolean}
     * @throws ClassCastException if this instance is not a {@link JsonBoolean}
     */
    default JsonBoolean asBoolean() {
        return (JsonBoolean) this;
    }

    /**
     * Checks if the value is a JSON string value
     *
     * @return true if this instance is a {@link JsonString}
     */
    default boolean isString() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonString}
     *
     * @return this instance cast to a {@link JsonString}
     * @throws ClassCastException if this instance is not a {@link JsonString}
     */
    default JsonString asString() {
        return (JsonString) this;
    }

    /**
     * Checks if the value is a JSON number value
     *
     * @return true if this instance is a {@link JsonNumber}
     */
    default boolean isNumber() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonNumber}
     *
     * @return this instance cast to a {@link JsonNumber}
     * @throws ClassCastException if this instance is not a {@link JsonNumber}
     */
    default JsonNumber asNumber() {
        return (JsonNumber) this;
    }

    /**
     * Checks if the value is a JSON array
     *
     * @return true if this instance is a {@link JsonArray}
     */
    default boolean isArray() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonArray}
     *
     * @return this instance cast to a {@link JsonArray}
     * @throws ClassCastException if this instance is not a {@link JsonArray}
     */
    default JsonArray asArray() {
        return (JsonArray) this;
    }

    /**
     * Checks if the value is a JSON object
     *
     * @return true if this instance is a {@link JsonObject}
     */
    default boolean isObject() {
        return false;
    }

    /**
     * Checks if the value is a JSON primitive
     *
     * @return true if this instance is a {@link JsonBoolean}, {@link JsonString}, {@link JsonNumber}, or {@link JsonNull}
     */
    default boolean isPrimitive() {
        return false;
    }

    /**
     * Casts this instance to a {@link JsonObject}
     *
     * @return this instance cast to a {@link JsonObject}
     * @throws ClassCastException if this instance is not a {@link JsonObject}
     */
    default JsonObject asObject() {
        return (JsonObject) this;
    }
}
