package com.grunka.json;

import com.grunka.json.type.JsonArray;
import com.grunka.json.type.JsonBoolean;
import com.grunka.json.type.JsonNull;
import com.grunka.json.type.JsonNumber;
import com.grunka.json.type.JsonString;
import com.grunka.json.type.JsonValue;

import java.math.BigDecimal;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Json {
    public static JsonValue parse(String json) {
        int position = 0;
        Stack<JsonValue> stack = new Stack<>();
        while (position < json.length()) {
            JsonValue value = null;
            position = skipWhileWhitespace(json, position);
            if (json.startsWith("null", position)) {
                position += 4;
                value = JsonNull.NULL;
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
            } else if ("-0123456789".indexOf(json.charAt(position)) != -1) {
                Pattern numberPattern = Pattern.compile("^-?(0|[1-9][0-9]*)([.][0-9]+)?([eE][+-]?[0-9]+)?");
                Matcher matcher = numberPattern.matcher(json.substring(position));
                if (matcher.find()) {
                    String number = matcher.group();
                    position += number.length();
                    value = new JsonNumber(new BigDecimal(number));
                } else {
                    throw new JsonParseException("Could not parse number");
                }
            } else if (json.charAt(position) == '[') {
                position++;
                stack.push(new JsonArray());
            } else if (json.charAt(position) == ',') {
                //continue
                if (stack.isEmpty() || !stack.peek().isArray()) {
                    throw new JsonParseException("No array on stack");
                }
                position++;
            } else if (json.charAt(position) == ']') {
                if (stack.isEmpty() || !stack.peek().isArray()) {
                    throw new JsonParseException("End of array encountered without array on stack");
                }
                position++;
                value = stack.pop();
            } else {
                throw new JsonParseException("Was expecting a JSON value");
            }
            position = skipWhileWhitespace(json, position);
            if (stack.isEmpty()) {
                if (position == json.length()) {
                    return value;
                } else {
                    throw new JsonParseException("Did not read full json string");
                }
            } else if (stack.peek().isArray()) {
                if (value != null) {
                    stack.peek().asArray().add(value);
                }
            }
        }
        throw new JsonParseException("Ended parsing unexpectedly");
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
