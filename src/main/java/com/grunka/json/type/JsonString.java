package com.grunka.json.type;

import com.grunka.json.Json;

import java.util.Objects;

public class JsonString extends JsonValue {
    private final String value;

    public JsonString(String value) {
        this.value = Objects.requireNonNull(value, "Value cannot be null");
    }

    @Override
    public String toString() {
        return Json.stringify(this);
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
