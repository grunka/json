package com.grunka.json.type;

import com.grunka.json.Json;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Representation of a JSON object
 */
public class JsonObject implements JsonValue, Map<String, JsonValue> {
    private final LinkedHashMap<String, JsonValue> values;

    /**
     * Constructs an empty {@link JsonObject}
     */
    public JsonObject() {
        this.values = new LinkedHashMap<>();
    }

    /**
     * Constructs a {@link JsonObject} containing the values in the supplied map
     *
     * @param values a populated map of values
     */
    public JsonObject(Map<String, JsonValue> values) {
        this.values = new LinkedHashMap<>(values);
    }

    @Override
    public String toString() {
        return Json.stringify(this);
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public JsonValue get(Object key) {
        return values.get(key);
    }

    /**
     * Get a field from the object and directly converts it to an JsonObject
     * @param key the key in the object
     * @return the value represented by the key as an JsonObject
     * @throws IllegalArgumentException if the value is not a JsonObject
     */
    public JsonObject getObject(String key) {
        JsonValue jsonValue = values.get(key);
        if (jsonValue == null || jsonValue == JsonNull.NULL) {
            return null;
        }
        if (!jsonValue.isObject()) {
            throw new IllegalArgumentException("Key is not object");
        }
        return jsonValue.asObject();
    }

    /**
     * Get a field from the object and directly converts it to an JsonArray
     * @param key the key in the object
     * @return the value represented by the key as a JsonArray
     * @throws IllegalArgumentException if the value is not a JsonArray
     */
    public JsonArray getArray(String key) {
        JsonValue jsonValue = values.get(key);
        if (jsonValue == null || jsonValue == JsonNull.NULL) {
            return null;
        }
        if (!jsonValue.isArray()) {
            throw new IllegalArgumentException("Key is not array");
        }
        return jsonValue.asArray();
    }

    /**
     * Get a field from the object and directly converts it to an JsonString and gets the string value from it
     * @param key the key in the object
     * @return the value represented by the key as a String
     * @throws IllegalArgumentException if the value is not a JsonString
     */
    public String getString(String key) {
        JsonValue jsonValue = values.get(key);
        if (jsonValue == null || jsonValue == JsonNull.NULL) {
            return null;
        }
        if (!jsonValue.isString()) {
            throw new IllegalArgumentException("Key is not a string");
        }
        return jsonValue.asString().getString();
    }

    /**
     * Get a field from the object and directly converts it to an JsonNumber and gets the BigDecimal value from it
     * @param key the key in the object
     * @return the value represented by the key as a BigDecimal
     * @throws IllegalArgumentException if the value is not a JsonNumber
     */
    public BigDecimal getBigDecimal(String key) {
        JsonValue jsonValue = values.get(key);
        if (jsonValue == null || jsonValue == JsonNull.NULL) {
            return null;
        }
        if (!jsonValue.isNumber()) {
            throw new IllegalArgumentException("Key is not a number");
        }
        return jsonValue.asNumber().getBigDecimal();
    }

    /**
     * Get a field from the object and directly converts it to an JsonBoolean and gets the Boolean value from it
     * @param key the key in the object
     * @return the value represented by the key as a Boolean
     * @throws IllegalArgumentException if the value is not a JsonBoolean
     */
    public Boolean getBoolean(String key) {
        JsonValue jsonValue = values.get(key);
        if (jsonValue == null || jsonValue == JsonNull.NULL) {
            return null;
        }
        if (!jsonValue.isBoolean()) {
            throw new IllegalArgumentException("Key is not a number");
        }
        return jsonValue.asBoolean().getBoolean();
    }

    @Override
    public JsonValue put(String key, JsonValue value) {
        return values.put(key, value);
    }

    @Override
    public JsonValue remove(Object key) {
        return values.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends JsonValue> m) {
        values.putAll(m);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<JsonValue> values() {
        return values.values();
    }

    @Override
    public Set<Entry<String, JsonValue>> entrySet() {
        return values.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JsonObject that = (JsonObject) o;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
