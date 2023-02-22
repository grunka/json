package com.grunka.json.type;

import java.util.Objects;

/**
 * Representation of a JSON string value
 */
public class JsonString implements JsonValue, CharSequence, Comparable<JsonString> {
    private final String value;

    /**
     * Constructs a {@link JsonString} containing the supplied string value
     *
     * @param value the string value
     */
    public JsonString(String value) {
        this.value = Objects.requireNonNull(value, "Value cannot be null");
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start, end);
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

    /**
     * Gets the decoded string value
     *
     * @return the decoded string value in the {@link JsonString}
     */
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

    @Override
    public int compareTo(JsonString o) {
        return this.value.compareTo(o.value);
    }
}
