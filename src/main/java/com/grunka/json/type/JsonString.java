package com.grunka.json.type;

public class JsonString extends JsonValue {
    private final String value;

    public JsonString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return '"' + value.replaceAll("\"", "\\\\\"") + '"';
    }

    public String getString() {
        return value;
    }
}
