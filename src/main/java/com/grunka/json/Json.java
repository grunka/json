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

    private static class State {
        int position = 0;
        boolean expectsMore = false;
        boolean needsParserShift = false;
        final Stack<JsonValue> stack = new Stack<>();
        final Matcher parser;

        private State(Matcher parser) {
            this.parser = parser;
        }
    }

    public static JsonValue parse(String json) {
        State state = new State(PARSER_PATTERN.matcher(json));
        while (state.position < json.length()) {
            String match = nextMatch(state);
            JsonValue value = switch (match) {
                case "null" -> JsonNull.NULL;
                case "true" -> JsonBoolean.TRUE;
                case "false" -> JsonBoolean.FALSE;
                case "," -> handleComma(state);
                case ":" -> handleColon(state);
                case "[" -> handleStartArray(state);
                case "]" -> handleEndArray(state);
                case "{" -> handleStartObject(state);
                case "}" -> handleEndObject(state);
                case "\"" -> handleString(json, state);
                default -> new JsonNumber(new BigDecimal(match));
            };
            if (state.stack.isEmpty()) {
                state.position = skipWhileWhitespace(json, state.position);
                if (state.position == json.length()) {
                    if (state.expectsMore) {
                        throw new JsonParseException("Parsing ended with a dangling comma");
                    }
                    return value;
                } else {
                    throw new JsonParseException("Non parsable data found at end of input");
                }
            } else if (state.stack.peek().isArray()) {
                if (value != null) {
                    state.stack.peek().asArray().add(value);
                    state.expectsMore = false;
                }
            } else if (state.stack.peek() instanceof JsonObjectKey top) {
                if (value != null) {
                    if (top.key == null) {
                        if (!value.isString()) {
                            throw new JsonParseException("Key in object before position " + state.position + " is not a string");
                        }
                        top.key = (JsonString) value;
                    } else {
                        JsonObjectKey poppedKey = (JsonObjectKey) state.stack.pop();
                        if (state.stack.isEmpty() || !state.stack.peek().isObject()) {
                            throw new JsonParseException("Object not found at stack when adding value from before position " + state.position);
                        }
                        if (!poppedKey.seenColon) {
                            throw new JsonParseException("Expected colon before position " + state.position);
                        }
                        state.stack.peek().asObject().put(poppedKey.key.getString(), value);
                        state.stack.push(new JsonObjectKey());
                        state.expectsMore = false;
                    }
                }
            } else {
                throw new JsonParseException("Unrecognized object at top of parsing stack " + state.stack.peek().getClass());
            }
        }
        throw new JsonParseException("Reached end of input without parsing any value");
    }

    private static JsonString handleString(String json, State state) {
        StringBuilder builder = new StringBuilder();
        boolean escape = false;
        while (true) {
            char c = json.charAt(state.position += 1);
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
                        builder.append(Character.toString(Integer.parseInt(json.substring(state.position, state.position + 4), 16)));
                        state.position += 4;
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
        state.needsParserShift = true;
        return new JsonString(builder.toString());
    }

    private static String nextMatch(State state) {
        boolean found;
        if (state.needsParserShift) {
            found = state.parser.find(state.position);
            state.needsParserShift = false;
        } else {
            found = state.parser.find();
        }
        if (!found) {
            throw new JsonParseException("Did not find any JSON content after position " + state.position);
        }
        if (state.parser.start() != state.position) {
            throw new JsonParseException("Found non JSON content at position " + state.position);
        }
        String match = state.parser.group(1);
        state.position += state.parser.group().length();
        return match;
    }

    private static JsonValue handleComma(State state) {
        if (state.stack.isEmpty() || (!state.stack.peek().isArray() && !(state.stack.peek() instanceof JsonObjectKey))) {
            throw new JsonParseException("Found comma at position " + state.parser.start(1) + " while not parsing an array or an object");
        }
        state.expectsMore = true;
        return null;
    }

    private static JsonValue handleEndObject(State state) {
        if (state.stack.isEmpty()) {
            throw new JsonParseException("End of object encountered at position " + state.parser.start(1) + " without having started parsing one");
        }
        JsonValue top = state.stack.pop();
        if (!(top instanceof JsonObjectKey)) {
            throw new JsonParseException("End of object encountered at position " + state.parser.start(1) + " while parsing something else");
        }
        if (((JsonObjectKey) top).key != null) {
            throw new JsonParseException("End of object encountered at position " + state.parser.start(1) + " while expecting a value");
        }
        if (state.stack.isEmpty() || !state.stack.peek().isObject()) {
            throw new JsonParseException("End of object encountered at position " + state.parser.start(1) + " without object on parsing stack");
        }
        return state.stack.pop();
    }

    private static JsonValue handleStartObject(State state) {
        state.stack.push(new JsonObject());
        state.stack.push(new JsonObjectKey());
        return null;
    }

    private static JsonValue handleEndArray(State state) {
        if (state.stack.isEmpty() || !state.stack.peek().isArray()) {
            throw new JsonParseException("End of array encountered at position " + state.parser.start(1) + " while not parsing an array");
        }
        return state.stack.pop();
    }

    private static JsonValue handleStartArray(State state) {
        state.stack.push(new JsonArray());
        return null;
    }

    private static JsonValue handleColon(State state) {
        if (state.stack.isEmpty() || (!(state.stack.peek() instanceof JsonObjectKey))) {
            throw new JsonParseException("Found colon at position " + state.parser.start(1) + " while not parsing an object");
        }
        if (((JsonObjectKey) state.stack.peek()).seenColon) {
            throw new JsonParseException("Multiple colons at position " + state.parser.start(1));
        }
        ((JsonObjectKey) state.stack.peek()).seenColon = true;
        return null;
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
