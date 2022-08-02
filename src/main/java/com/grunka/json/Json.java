package com.grunka.json;

import com.grunka.json.type.JsonBoolean;
import com.grunka.json.type.JsonNull;
import com.grunka.json.type.JsonString;
import com.grunka.json.type.JsonValue;

public class Json {
    public static JsonValue parse(String json) {
        int position = 0;
        JsonValue value = null;
        position = skipWhileWhitespace(json, position);
        if (json.startsWith("null", position)) {
            position += 4;
            value = new JsonNull();
        } else if (json.startsWith("true", position)) {
            position += 4;
            value = JsonBoolean.TRUE;
        } else if (json.startsWith("false", position)) {
            position += 5;
            value = JsonBoolean.FALSE;
        } else if (json.charAt(position) == '"') {
            position++;
            StringBuilder builder = new StringBuilder();
            boolean escape = false;
            while (position < json.length()) {
                char c = json.charAt(position++);
                if (escape) {
                    switch (c) {
                        case '\\' -> builder.append('\\');
                        case '/' -> builder.append('/');
                        case '"' -> builder.append('"');
                        case 'b' -> builder.append('\b');
                        case 'f' -> builder.append('\f');
                        case 'n' -> builder.append('\n');
                        case 'r' -> builder.append('\r');
                        case 't' -> builder.append('\t');
                        case 'u' -> {
                            builder.append(Character.toString(Integer.parseInt(json.substring(position, position + 4), 16)));
                            position += 4;
                        }
                    }
                    escape = false;
                } else {
                    if (c == '\\') {
                        escape = true;
                    } else if (c == '"') {
                        break;
                    } else {
                        builder.append(c);
                    }
                }
            }
            value = new JsonString(builder.toString());
        } else {
            throw new JsonParseException("Was expecting a JSON value");
        }
        position = skipWhileWhitespace(json, position);
        if (position == json.length()) {
            return value;
        }
        throw new JsonParseException("Did not read full json string");
    }

    private static int skipWhileWhitespace(String json, int position) {
        int length = json.length();
        while (position < length && isWhitespace(json.charAt(position))) {
            position++;
        }
        return position;
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }
}
