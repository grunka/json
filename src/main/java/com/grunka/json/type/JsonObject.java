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
