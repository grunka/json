package com.grunka.json.type;

public class JsonString extends JsonValue {
    private final String value;

    public JsonString(String value) {
        this.value = value;
    }

    private static String encodeString(String contents) {
        return contents.replaceAll("\"", "\\\\\"");
    }

    @Override
    public String toString() {
        return '"' + encodeString(value) + '"';
    }

    public String getString() {
        return value;
    }

    @Override
    public boolean isString() {
        return true;
    }
}
