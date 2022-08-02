package com.grunka.json.type;

import com.grunka.json.JsonParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonString extends JsonValue {
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("\\\\([\"\\\\/bfnrtu])([0-9a-f]{4})?");
    private final String value;

    public JsonString(String value) {
        this.value = value;
    }

    public static String decodeRawString(String contents) {
        Matcher escaping = ESCAPE_PATTERN.matcher(contents);
        return escaping.replaceAll(result -> {
            String first = result.group(1);
            String second = result.group(2);
            switch (first) {
                case "\"":
                case "\\":
                case "/":
                    break;
                case "b":
                    first = "\b";
                    break;
                case "f":
                    first = "\f";
                    break;
                case "n":
                    first = "\n";
                    break;
                case "r":
                    first = "\r";
                    break;
                case "t":
                    first = "\t";
                    break;
                case "u":
                    if (second == null) {
                        throw new JsonParseException("Cannot decode raw json string " + contents);
                    }
                    first = Character.toString(Integer.parseInt(second, 16));
                    second = "";
                    break;
            }
            if (second == null) {
                second = "";
            }
            return first + second;
        });
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
