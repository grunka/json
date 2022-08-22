package com.grunka.json.type;

public class JsonValue {
    public boolean isNull() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public JsonBoolean asBoolean() {
        return (JsonBoolean) this;
    }

    public boolean isString() {
        return false;
    }

    public JsonString asString() {
        return (JsonString) this;
    }

    public boolean isNumber() {
        return false;
    }

    public JsonNumber asNumber() {
        return (JsonNumber) this;
    }

    public boolean isArray() {
        return false;
    }

    public JsonArray asArray() {
        return (JsonArray) this;
    }

    public boolean isObject() {
        return false;
    }

    public boolean isPrimitive() {
        return isNull() || isBoolean() || isNumber() || isString();
    }

    public JsonObject asObject() {
        return (JsonObject) this;
    }
}
