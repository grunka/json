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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Json {
    private static final Pattern PARSER_PATTERN = Pattern.compile("[ \\n\\r\\t]*(-?(?:0|[1-9][0-9]*)(?:[.][0-9]+)?(?:[eE][+-]?[0-9]+)?|null|true|false|[,:\"\\[\\]{}])");

    private Json() {
    }

    public static JsonValue parse(String json) {
        int position = 0;
        boolean expectsMore = false;
        boolean needsParserShift = false;
        Stack<JsonValue> stack = new Stack<>();
        Matcher parser = PARSER_PATTERN.matcher(json);
        while (position < json.length()) {
            boolean found;
            if (needsParserShift) {
                found = parser.find(position);
                needsParserShift = false;
            } else {
                found = parser.find();
            }
            if (!found) {
                throw new JsonParseException("Did not find any JSON content after position " + position);
            }
            if (parser.start() != position) {
                throw new JsonParseException("Found non JSON content at position " + position);
            }
            String match = parser.group(1);
            position += parser.group().length();
            JsonValue value = switch (match) {
                case "null" -> JsonNull.NULL;
                case "true" -> JsonBoolean.TRUE;
                case "false" -> JsonBoolean.FALSE;
                case "," -> {
                    if (stack.isEmpty() || (!stack.peek().isArray() && !(stack.peek() instanceof JsonObjectKey))) {
                        throw new JsonParseException("Found comma at position " + parser.start(1) + " while not parsing an array or an object");
                    }
                    expectsMore = true;
                    yield null;
                }
                case ":" -> {
                    if (stack.isEmpty() || (!(stack.peek() instanceof JsonObjectKey))) {
                        throw new JsonParseException("Found colon at position " + parser.start(1) + " while not parsing an object");
                    }
                    if (((JsonObjectKey) stack.peek()).seenColon) {
                        throw new JsonParseException("Multiple colons at position " + parser.start(1));
                    }
                    ((JsonObjectKey) stack.peek()).seenColon = true;
                    yield null;
                }
                case "[" -> {
                    stack.push(new JsonArray());
                    yield null;
                }
                case "]" -> {
                    if (stack.isEmpty() || !stack.peek().isArray()) {
                        throw new JsonParseException("End of array encountered at position " + parser.start(1) + " while not parsing an array");
                    }
                    yield stack.pop();
                }
                case "{" -> {
                    stack.push(new JsonObject());
                    stack.push(new JsonObjectKey());
                    yield null;
                }
                case "}" -> {
                    if (stack.isEmpty()) {
                        throw new JsonParseException("End of object encountered at position " + parser.start(1) + " without having started parsing one");
                    }
                    JsonValue top = stack.pop();
                    if (!(top instanceof JsonObjectKey)) {
                        throw new JsonParseException("End of object encountered at position " + parser.start(1) + " while parsing something else");
                    }
                    if (((JsonObjectKey) top).key != null) {
                        throw new JsonParseException("End of object encountered at position " + parser.start(1) + " while expecting a value");
                    }
                    if (stack.isEmpty() || !stack.peek().isObject()) {
                        throw new JsonParseException("End of object encountered at position " + parser.start(1) + " without object on parsing stack");
                    }
                    yield stack.pop();
                }
                case "\"" -> {
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
                    needsParserShift = true;
                    yield new JsonString(builder.toString());
                }
                default -> new JsonNumber(new BigDecimal(match));
            };
            if (stack.isEmpty()) {
                position = skipWhileWhitespace(json, position);
                if (position == json.length()) {
                    if (expectsMore) {
                        throw new JsonParseException("Parsing ended with a dangling comma");
                    }
                    return value;
                } else {
                    throw new JsonParseException("Non parsable data found at end of input");
                }
            } else if (stack.peek().isArray()) {
                if (value != null) {
                    stack.peek().asArray().add(value);
                    expectsMore = false;
                }
            } else if (stack.peek() instanceof JsonObjectKey top) {
                if (value != null) {
                    if (top.key == null) {
                        if (!value.isString()) {
                            throw new JsonParseException("Key in object before position " + position + " is not a string");
                        }
                        top.key = (JsonString) value;
                    } else {
                        JsonObjectKey poppedKey = (JsonObjectKey) stack.pop();
                        if (stack.isEmpty() || !stack.peek().isObject()) {
                            throw new JsonParseException("Object not found at stack when adding value from before position " + position);
                        }
                        if (!poppedKey.seenColon) {
                            throw new JsonParseException("Expected colon before position " + position);
                        }
                        stack.peek().asObject().put(poppedKey.key.getString(), value);
                        stack.push(new JsonObjectKey());
                        expectsMore = false;
                    }
                }
            }
        }
        throw new JsonParseException("Reached end of input without parsing any value");
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
            map.forEach((key, value) -> object.put(String.valueOf(key), valuefy(value)));
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
                    object.put(field.getName(), valuefied);
                }
            }
            if (!canAccess) {
                field.setAccessible(false);
            }
        }
        return object;
    }

    public static String stringify(Object input) {
        StringBuilder output = new StringBuilder();
        List<Object> queue = new LinkedList<>();
        queue.add(valuefy(input));
        while (!queue.isEmpty()) {
            Object popped = queue.remove(0);
            if (popped instanceof String text) {
                output.append(text);
            } else if (popped instanceof JsonValue value) {
                if (value.isPrimitive()) {
                    output.append(value);
                } else if (value.isArray()) {
                    output.append("[");
                    Iterator<JsonValue> iterator = value.asArray().iterator();
                    int arrayPosition = 0;
                    while (iterator.hasNext()) {
                        queue.add(arrayPosition++, iterator.next());
                        if (iterator.hasNext()) {
                            queue.add(arrayPosition++, ",");
                        }
                    }
                    queue.add(arrayPosition, "]");
                } else if (value.isObject()) {
                    output.append("{");
                    Iterator<Map.Entry<String, JsonValue>> iterator = value.asObject().entrySet().iterator();
                    int mapPosition = 0;
                    while (iterator.hasNext()) {
                        Map.Entry<String, JsonValue> entry = iterator.next();
                        queue.add(mapPosition++, new JsonString(entry.getKey()) + ":");
                        queue.add(mapPosition++, entry.getValue());
                        if (iterator.hasNext()) {
                            queue.add(mapPosition++, ",");
                        }
                    }
                    queue.add(mapPosition, "}");
                } else {
                    throw new JsonStringifyException("Could not convert " + value + " to string");
                }
            }
        }
        return output.toString();
    }
}
