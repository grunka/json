package com.grunka.json;

import com.grunka.json.type.JsonArray;
import com.grunka.json.type.JsonBoolean;
import com.grunka.json.type.JsonNull;
import com.grunka.json.type.JsonNumber;
import com.grunka.json.type.JsonObject;
import com.grunka.json.type.JsonString;
import com.grunka.json.type.JsonValue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Json {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?(0|[1-9][0-9]*)([.][0-9]+)?([eE][+-]?[0-9]+)?");
    private static final Pattern STRING_ESCAPE_CHARACTERS = Pattern.compile("[\"\b\f\n\r\t\\\\]");

    private Json() {
    }

    public static JsonValue parse(String json) {
        int position = 0;
        boolean expectsMore = false;
        Stack<JsonValue> stack = new Stack<>();
        while (position < json.length()) {
            JsonValue value = null;
            position = skipWhileWhitespace(json, position);
            char firstCharacter = json.charAt(position);
            if (json.startsWith("null", position)) {
                position += 4;
                value = JsonNull.NULL;
            } else if (json.startsWith("true", position)) {
                position += 4;
                value = JsonBoolean.TRUE;
            } else if (json.startsWith("false", position)) {
                position += 5;
                value = JsonBoolean.FALSE;
            } else if (firstCharacter == '"') {
                position++;
                StringBuilder builder = new StringBuilder();
                boolean escape = false;
                while (true) {
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
            } else if (isNumeric(firstCharacter)) {
                Matcher matcher = NUMBER_PATTERN.matcher(json.substring(position));
                if (matcher.find()) {
                    String number = matcher.group();
                    position += number.length();
                    value = new JsonNumber(new BigDecimal(number));
                } else {
                    throw new JsonParseException("Could not parse number");
                }
            } else if (firstCharacter == ',') {
                if (stack.isEmpty() || (!stack.peek().isArray() && !(stack.peek() instanceof JsonObjectKey))) {
                    throw new JsonParseException("No array or object on stack");
                }
                expectsMore = true;
                position++;
            } else if (firstCharacter == ':') {
                if (stack.isEmpty() || (!(stack.peek() instanceof JsonObjectKey))) {
                    throw new JsonParseException("No object key on stack");
                }
                if (((JsonObjectKey) stack.peek()).seenColon) {
                    throw new JsonParseException("Multiple colons encountered");
                }
                ((JsonObjectKey) stack.peek()).seenColon = true;
                position++;
            } else if (firstCharacter == '[') {
                position++;
                stack.push(new JsonArray());
            } else if (firstCharacter == ']') {
                if (stack.isEmpty() || !stack.peek().isArray()) {
                    throw new JsonParseException("End of array encountered without array on stack");
                }
                position++;
                value = stack.pop();
            } else if (firstCharacter == '{') {
                position++;
                stack.push(new JsonObject());
                stack.push(new JsonObjectKey());
            } else if (firstCharacter == '}') {
                if (stack.isEmpty()) {
                    throw new JsonParseException("End of object encountered without object on stack");
                }
                JsonValue top = stack.pop();
                if (!(top instanceof JsonObjectKey)) {
                    throw new JsonParseException("Unexpected object on stack");
                }
                if (((JsonObjectKey) top).key != null) {
                    throw new JsonParseException("Partial object entry encountered");
                }
                if (stack.isEmpty() || !stack.peek().isObject()) {
                    throw new JsonParseException("End of object encountered without object on stack");
                }
                position++;
                value = stack.pop();
            } else {
                throw new JsonParseException("Was expecting a JSON value");
            }
            position = skipWhileWhitespace(json, position);
            if (stack.isEmpty()) {
                if (position == json.length()) {
                    if (expectsMore) {
                        throw new JsonParseException("Dangling comma");
                    }
                    return value;
                } else {
                    throw new JsonParseException("Did not fully read input");
                }
            } else if (stack.peek().isArray()) {
                if (value != null) {
                    stack.peek().asArray().add(value);
                    expectsMore = false;
                }
            } else if (stack.peek() instanceof JsonObjectKey) {
                if (value != null) {
                    JsonObjectKey top = (JsonObjectKey) stack.peek();
                    if (top.key == null) {
                        if (!value.isString()) {
                            throw new JsonParseException("Did not get string as key for object");
                        }
                        top.key = (JsonString) value;
                    } else {
                        JsonObjectKey poppedKey = (JsonObjectKey) stack.pop();
                        if (stack.isEmpty() || !stack.peek().isObject()) {
                            throw new JsonParseException("Object missing from stack when adding value");
                        }
                        if (!poppedKey.seenColon) {
                            throw new JsonParseException("Missing colon");
                        }
                        stack.peek().asObject().put(poppedKey.key, value);
                        stack.push(new JsonObjectKey());
                        expectsMore = false;
                    }
                }
            }
        }
        throw new JsonParseException("Ended parsing unexpectedly");
    }

    private static boolean isNumeric(char c) {
        return switch (c) {
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
            default -> false;
        };
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

    private static class JsonObjectKey extends JsonValue {
        public JsonString key;
        public boolean seenColon = false;
    }

    public static JsonValue valuefy(Object input) {
        if (input == null) {
            return JsonNull.NULL;
        }
        if (input instanceof JsonValue) {
            return (JsonValue) input;
        }
        if (input instanceof Boolean) {
            return ((Boolean) input) ? JsonBoolean.TRUE : JsonBoolean.FALSE;
        }
        if (input instanceof BigDecimal) {
            return new JsonNumber((BigDecimal) input);
        }
        if (input instanceof Number) {
            return new JsonNumber(new BigDecimal(String.valueOf(input)));
        }
        if (input instanceof String) {
            return new JsonString((String) input);
        }
        if (input instanceof Temporal) {
            return new JsonString(input.toString());
        }
        if (input instanceof Map<?, ?> map) {
            JsonObject object = new JsonObject();
            map.forEach((key, value) -> object.put(new JsonString(String.valueOf(key)), valuefy(value)));
            return object;
        }
        if (input instanceof Collection<?> collection) {
            JsonArray array = new JsonArray();
            collection.forEach(value -> array.add(valuefy(value)));
            return array;
        }
        if (input instanceof Optional<?> optional) {
            if (optional.isPresent()) {
                return valuefy(optional.get());
            } else {
                return JsonNull.NULL;
            }
        }
        JsonObject object = new JsonObject();
        for (Field field : input.getClass().getDeclaredFields()) {
            boolean canAccess = field.canAccess(input);
            if (!canAccess) {
                field.setAccessible(true);
            }
            Object value;
            try {
                value = field.get(input);
            } catch (IllegalAccessException e) {
                throw new JsonStringifyException("Can not access field " + field.getName(), e);
            }
            if (value != null) {
                JsonValue valuefied = valuefy(value);
                if (!valuefied.isNull()) {
                    object.put(new JsonString(field.getName()), valuefied);
                }
            }
            if (!canAccess) {
                field.setAccessible(false);
            }
        }
        return object;
    }

    public static String stringify(JsonValue value) {
        StringBuilder output = new StringBuilder();
        if (value == null || value.isNull()) {
            output.append("null");
        } else if (value.isString()) {
            output.append('"');
            output.append(encodeString(value.asString().getString()));
            output.append('"');
        } else if (value.isBoolean()) {
            if (value.asBoolean().isTrue()) {
                output.append("true");
            } else {
                output.append("false");
            }
        } else if (value.isNumber()) {
            output.append(value.asNumber().getBigDecimal().toPlainString());
        } else if (value.isArray()) {
            output.append("[");
            output.append(value.asArray().stream().map(Object::toString).collect(Collectors.joining(",")));
            output.append("]");
        } else if (value.isObject()) {
            output.append("{");
            output.append(value.asObject().entrySet().stream()
                    .map(e -> e.getKey().toString() + ":" + e.getValue().toString())
                    .collect(Collectors.joining(",")));
            output.append("}");
        } else {
            throw new JsonStringifyException("Could not convert " + value + " to string");
        }
        return output.toString();
    }

    public static String stringify(Object input) {
        return stringify(valuefy(input));
    }

    private static String encodeString(String contents) {
        Matcher matcher = STRING_ESCAPE_CHARACTERS.matcher(contents);
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
}
