package com.grunka.json.type;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonObject extends JsonValue implements Map<JsonString, JsonValue> {
    private final Map<JsonString, JsonValue> values = new LinkedHashMap<>();

    @Override
    public String toString() {
        return "{" +
                values.entrySet().stream()
                        .map(e -> e.getKey().toString() + ":" + e.getValue().toString())
                        .collect(Collectors.joining(",")) +
                "}";
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

    @Override
    public JsonValue put(JsonString key, JsonValue value) {
        return values.put(key, value);
    }

    @Override
    public JsonValue remove(Object key) {
        return values.remove(key);
    }

    @Override
    public void putAll(Map<? extends JsonString, ? extends JsonValue> m) {
        values.putAll(m);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Set<JsonString> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<JsonValue> values() {
        return values.values();
    }

    @Override
    public Set<Entry<JsonString, JsonValue>> entrySet() {
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
