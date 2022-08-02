package com.grunka.json.type;

import java.util.regex.Pattern;

public class JsonString extends JsonValue {
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("\\\\([\"\\\\/bfnrtu])([0-9a-f]{4})?");
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
