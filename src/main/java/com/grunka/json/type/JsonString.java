package com.grunka.json.type;

import java.util.Objects;

public class JsonString extends JsonValue {
    private final String value;

    public JsonString(String value) {
        this.value = Objects.requireNonNull(value, "Value cannot be null");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\"' -> builder.append("\\\"");
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                case '\\' -> builder.append("\\\\");
                default -> {
                    if (c >= 0x20) {
                        builder.append(c);
                    } else {
                        String hex = Integer.toHexString(c);
                        builder.append("\\u");
                        builder.append("0".repeat((4 - hex.length())));
                        builder.append(hex);
                    }
                }
            }
        }
        builder.append('"');
        return builder.toString();
    }

    @Override
    public boolean isPrimitive() {
        return true;
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
