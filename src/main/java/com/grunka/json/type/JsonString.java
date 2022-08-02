package com.grunka.json.type;

import com.grunka.json.JsonStringifyException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonString extends JsonValue {
    private final String value;

    public JsonString(String value) {
        this.value = Objects.requireNonNull(value, "Value cannot be null");
    }

    private static String encodeString(String contents) {
        Pattern specialCharacters = Pattern.compile("[\"\b\f\n\r\t\\\\]");
        Matcher matcher = specialCharacters.matcher(contents);
        return matcher.replaceAll(result -> {
            String replacement;
            switch (result.group()) {
                case "\"" -> replacement = "\\\\\"";
                case "\b" -> replacement = "\\\\b";
                case "\f" -> replacement = "\\\\f";
                case "\n" -> replacement = "\\\\n";
                case "\r" -> replacement = "\\\\r";
                case "\t" -> replacement = "\\\\t";
                case "\\" -> replacement = "\\\\\\\\";
                default -> throw new JsonStringifyException("Unrecognized replacement");
            }
            return replacement;
        });
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
