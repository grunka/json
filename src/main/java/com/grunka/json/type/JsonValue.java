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
}
