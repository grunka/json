package com.grunka.json;

import com.grunka.json.type.JsonArray;
import com.grunka.json.type.JsonBoolean;
import com.grunka.json.type.JsonNull;
import com.grunka.json.type.JsonNumber;
import com.grunka.json.type.JsonObject;
import com.grunka.json.type.JsonString;
import com.grunka.json.type.JsonValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Json {
    private static final Pattern PARSER_PATTERN = Pattern.compile("[ \\n\\r\\t]*(-?(?:0|[1-9][0-9]*)(?:[.][0-9]+)?(?:[eE][+-]?[0-9]+)?|null|true|false|[,:\"\\[\\]{}])");

    private Json() {
    }

    private static class State {
        int position = 0;
        boolean expectsMoreEntries = false;
        boolean shiftParsingToPosition = false;
        private final LinkedList<JsonValueBuilder> parsingStack = new LinkedList<>();
        final Matcher parser;

        private State(Matcher parser) {
            this.parser = parser;
        }

        public JsonValueBuilder currentBuilder() {
            return parsingStack.peek();
        }

        public JsonValue buildFromStack() {
            return parsingStack.pop().build();
        }

        public void pushBuilder(JsonValueBuilder value) {
            parsingStack.push(value);
        }

        public boolean isBuilderMissing() {
            return parsingStack.isEmpty();
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
            if (state.isBuilderMissing()) {
                return parsingCompleted(json, state, value);
            }
            updateStateForArrayAndObjectParsing(state, value);
        }
        throw new JsonParseException("Reached end of input without parsing any value");
    }

    private static String nextMatch(State state) {
        boolean found;
        if (state.shiftParsingToPosition) {
            found = state.parser.find(state.position);
            state.shiftParsingToPosition = false;
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

    private static JsonValue parsingCompleted(String json, State state, JsonValue value) {
        state.position = skipWhileWhitespace(json, state.position);
        if (state.position == json.length()) {
            if (state.expectsMoreEntries) {
                throw new JsonParseException("Parsing ended with a dangling comma");
            }
            return value;
        } else {
            throw new JsonParseException("Non parsable data found at end of input");
        }
    }

    private static void updateStateForArrayAndObjectParsing(State state, JsonValue value) {
        if (state.isBuilderMissing()) {
            throw new JsonParseException("Found value at position " + state.parser.start(1) + " while not parsing an array or an object");
        }
        if (value != null) {
            state.currentBuilder().accept(value);
        }
    }

    private static JsonString handleString(String json, State state) {
        StringBuilder builder = new StringBuilder();
        boolean escape = false;
        while (true) {
            char c = json.charAt(state.position++);
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
        state.shiftParsingToPosition = true;
        return new JsonString(builder.toString());
    }

    private static JsonValue handleComma(State state) {
        if (state.isBuilderMissing()) {
            throw new JsonParseException("Found comma at position " + state.parser.start(1) + " while not parsing an array or an object");
        }
        state.currentBuilder().acceptComma();
        return null;
    }

    private static JsonValue handleEndObject(State state) {
        if (state.isBuilderMissing()) {
            throw new JsonParseException("End of object encountered at position " + state.parser.start(1) + " without having started parsing one");
        }
        JsonValue value = state.buildFromStack();
        if (!(value instanceof JsonObject)) {
            throw new JsonParseException("End of object encountered at position " + state.parser.start(1) + " but did not have object at top of stack");
        }
        return value;
    }

    private static JsonValue handleStartObject(State state) {
        state.pushBuilder(new JsonObjectBuilder());
        return null;
    }

    private static JsonValue handleEndArray(State state) {
        if (state.isBuilderMissing()) {
            throw new JsonParseException("End of array encountered at position " + state.parser.start(1) + " while not parsing any array");
        }
        JsonValue value = state.buildFromStack();
        if (!(value instanceof JsonArray)) {
            throw new JsonParseException("End of array encountered at position " + state.parser.start(1) + " while array not on top of stack");
        }
        return value;
    }

    private static JsonValue handleStartArray(State state) {
        state.pushBuilder(new JsonArrayBuilder());
        return null;
    }

    private static JsonValue handleColon(State state) {
        if (state.isBuilderMissing()) {
            throw new JsonParseException("Found colon at position " + state.parser.start(1) + " while not parsing any object");
        }
        state.currentBuilder().acceptColon();
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
        if (input instanceof Number) {
            return new JsonNumber((Number) input);
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

    public static <T> T objectify(String json, Class<? extends T> type) {
        return objectify(parse(json), type);
    }

    public static <T> T objectify(JsonValue value, Class<? extends T> type) {
        if (type == null) {
            throw new NullPointerException("Type cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        if (value.isNull()) {
            return null;
        }
        if (type == String.class) {
            if (!value.isString()) {
                throw new JsonObjectifyException("Trying to get a string from " + value.getClass().getSimpleName());
            }
            //noinspection unchecked
            return (T) value.asString().getString();
        }
        if (type == boolean.class || type == Boolean.class) {
            if (!value.isBoolean()) {
                throw new JsonObjectifyException("Trying to get boolean value from " + value.getClass().getSimpleName());
            }
            //noinspection unchecked
            return (T) value.asBoolean().getBoolean();
        }
        if (Number.class.isAssignableFrom(type) || type == int.class || type == float.class || type == double.class || type == long.class) {
            return convertNumber(value, type);
        }
        if (Temporal.class.isAssignableFrom(type)) {
            return convertTemporal(value, type);
        }
        return convertObject(value, type);
    }

    private static <T> T convertObject(JsonValue value, Class<? extends T> type) {
        if (!value.isObject()) {
            throw new JsonObjectifyException("Cannot create object of type " + type.getSimpleName() + " from " + value.getClass().getSimpleName() + " instead of JsonObject");
        }
        int numberOfConstructorParameters = Integer.MAX_VALUE;
        Constructor<?> selectedConstructor = null;
        for (Constructor<?> declaredConstructor : type.getDeclaredConstructors()) {
            if (declaredConstructor.getParameterCount() < numberOfConstructorParameters) {
                numberOfConstructorParameters = declaredConstructor.getParameterCount();
                selectedConstructor = declaredConstructor;
            }
        }
        if (selectedConstructor == null) {
            throw new JsonObjectifyException("Could not find a constructor for " + type.getSimpleName());
        }
        Object[] constructorParameters = new Object[numberOfConstructorParameters];
        Class<?>[] parameterTypes = selectedConstructor.getParameterTypes();
        for (int i = 0; i < numberOfConstructorParameters; i++) {
            if (parameterTypes[i] == int.class) {
                constructorParameters[i] = 0;
            } else if (parameterTypes[i] == long.class) {
                constructorParameters[i] = 0L;
            } else if (parameterTypes[i] == float.class) {
                constructorParameters[i] = 0f;
            } else if (parameterTypes[i] == double.class) {
                constructorParameters[i] = 0.0;
            } else if (parameterTypes[i] == boolean.class) {
                constructorParameters[i] = false;
            } else if (parameterTypes[i] == byte.class) {
                constructorParameters[i] = (byte) 0;
            } else if (parameterTypes[i] == short.class) {
                constructorParameters[i] = (short) 0;
            }
        }
        Object instance;
        try {
            selectedConstructor.trySetAccessible();
            instance = selectedConstructor.newInstance(constructorParameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonObjectifyException("Could not instantiate a " + type.getSimpleName(), e);
        }
        for (Field field : type.getDeclaredFields()) {
            if (!field.trySetAccessible()) {
                continue;
            }
            String name = field.getName();
            JsonValue jsonValue = value.asObject().getOrDefault(name, JsonNull.NULL);
            Class<?> fieldType = field.getType();
            if (Optional.class.isAssignableFrom(fieldType)) {
                Type[] typeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                try {
                    field.set(instance, Optional.ofNullable(objectify(jsonValue, (Class<?>) typeArguments[0])));
                } catch (IllegalAccessException e) {
                    throw new JsonObjectifyException("Failed to set the field named " + field.getName() + " in " + type.getSimpleName(), e);
                }
            } else if (List.class.isAssignableFrom(fieldType)) {
                Type[] typeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                try {
                    field.set(instance, objectifyList(jsonValue, (Class<?>) typeArguments[0]));
                } catch (IllegalAccessException e) {
                    throw new JsonObjectifyException("Failed to set the field named " + field.getName() + " in " + type.getSimpleName(), e);
                }
            } else if (Map.class.isAssignableFrom(fieldType)) {
                Type[] typeArguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                if (typeArguments[0] != String.class) {
                    throw new JsonObjectifyException("Encountered map named " + field.getName() + " in " + type.getSimpleName() + " with non string key");
                }
                try {
                    field.set(instance, typeArguments[1] instanceof ParameterizedType ? objectifyMap(jsonValue, (ParameterizedType) typeArguments[1]) : objectifyMap(jsonValue, (Class<?>) typeArguments[1]));
                } catch (IllegalAccessException e) {
                    throw new JsonObjectifyException("Failed to set the field named " + field.getName() + " in " + type.getSimpleName(), e);
                }
            } else {
                try {
                    field.set(instance, objectify(jsonValue, fieldType));
                } catch (IllegalAccessException e) {
                    throw new JsonObjectifyException("Failed to set the field named " + field.getName() + " in " + type.getSimpleName(), e);
                }
            }
        }
        //noinspection unchecked
        return (T) instance;
    }

    private static <T> T convertTemporal(JsonValue value, Class<? extends T> type) {
        if (!value.isString()) {
            throw new JsonObjectifyException("Trying to get a temporal value from " + value.getClass().getSimpleName() + " instead of a JsonString");
        }
        if (type == Instant.class) {
            //noinspection unchecked
            return (T) Instant.parse(value.asString().getString());
        }
        if (type == LocalDateTime.class) {
            //noinspection unchecked
            return (T) LocalDateTime.parse(value.asString().getString());
        }
        if (type == LocalDate.class) {
            //noinspection unchecked
            return (T) LocalDate.parse(value.asString().getString());
        }
        if (type == LocalTime.class) {
            //noinspection unchecked
            return (T) LocalTime.parse(value.asString().getString());
        }
        if (type == OffsetDateTime.class) {
            //noinspection unchecked
            return (T) OffsetDateTime.parse(value.asString().getString());
        }
        if (type == Year.class) {
            //noinspection unchecked
            return (T) Year.parse(value.asString().getString());
        }
        if (type == YearMonth.class) {
            //noinspection unchecked
            return (T) YearMonth.parse(value.asString().getString());
        }
        if (type == ZonedDateTime.class) {
            //noinspection unchecked
            return (T) ZonedDateTime.parse(value.asString().getString());
        }
        throw new JsonObjectifyException("Do not have an implementation that converts a string to " + type.getSimpleName());
    }

    private static <T> T convertNumber(JsonValue value, Class<? extends T> type) {
        if (!value.isNumber()) {
            throw new JsonObjectifyException("Trying to get number from " + value.getClass().getSimpleName());
        }
        if (type == int.class || type == Integer.class) {
            //noinspection unchecked
            return (T) (Integer) value.asNumber().getBigDecimal().intValueExact();
        }
        if (type == long.class || type == Long.class) {
            //noinspection unchecked
            return (T) (Long) value.asNumber().getBigDecimal().longValueExact();
        }
        if (type == float.class || type == Float.class) {
            //noinspection unchecked
            return (T) (Float) value.asNumber().getBigDecimal().floatValue();
        }
        if (type == double.class || type == Double.class) {
            //noinspection unchecked
            return (T) (Double) value.asNumber().getBigDecimal().doubleValue();
        }
        if (type == BigInteger.class) {
            //noinspection unchecked
            return (T) value.asNumber().getBigDecimal().toBigIntegerExact();
        }
        if (type == BigDecimal.class) {
            //noinspection unchecked
            return (T) value.asNumber().getBigDecimal();
        }
        throw new JsonObjectifyException("Do not have an implementation that converts a number to " + type.getSimpleName());
    }

    public static <T> List<T> objectifyList(String json, Class<? extends T> type) {
        return objectifyList(parse(json), type);
    }

    public static <T> List<T> objectifyList(JsonValue value, Class<? extends T> type) {
        if (!value.isArray()) {
            throw new JsonObjectifyException("Supplied value was not an array");
        }
        return value.asArray().stream().map(v -> objectify(v, type)).collect(Collectors.toList());
    }

    private static <T> List<T> objectifyList(JsonValue value, ParameterizedType type) {
        if (!value.isArray()) {
            throw new JsonObjectifyException("Supplied value was not an array");
        }
        boolean isList = ensureListOrMap(type);
        Type subType = type.getActualTypeArguments()[isList ? 0 : 1];
        if (isList) {
            //noinspection unchecked
            return value.asArray().stream().map(v -> subType instanceof ParameterizedType ? (T) objectifyList(v, (ParameterizedType) subType) : (T) objectifyList(v, (Class<?>) subType)).collect(Collectors.toList());
        } else {
            //noinspection unchecked
            return value.asArray().stream().map(v -> subType instanceof ParameterizedType ? (T) objectifyMap(v, (ParameterizedType) subType) : (T) objectifyMap(v, (Class<?>) subType)).collect(Collectors.toList());
        }
    }

    public static <V> Map<String, V> objectifyMap(String json, Class<? extends V> type) {
        return objectifyMap(parse(json), type);
    }

    public static <V> Map<String, V> objectifyMap(JsonValue value, Class<? extends V> type) {
        if (!value.isObject()) {
            throw new JsonObjectifyException("Supplied value is not an object");
        }
        Map<String, V> result = new LinkedHashMap<>();
        for (Map.Entry<String, JsonValue> entry : value.asObject().entrySet()) {
            result.put(entry.getKey(), objectify(entry.getValue(), type));
        }
        return result;
    }

    private static <V> Map<String, V> objectifyMap(JsonValue value, ParameterizedType type) {
        if (!value.isObject()) {
            throw new JsonObjectifyException("Supplied value is not an object");
        }
        boolean isList = ensureListOrMap(type);
        Type subType = type.getActualTypeArguments()[isList ? 0 : 1];
        Map<String, V> result = new LinkedHashMap<>();
        for (Map.Entry<String, JsonValue> entry : value.asObject().entrySet()) {
            if (isList) {
                //noinspection unchecked
                result.put(entry.getKey(), subType instanceof ParameterizedType ? (V) objectifyList(entry.getValue(), (ParameterizedType) subType) : (V) objectifyList(entry.getValue(), (Class<?>) subType));
            } else {
                //noinspection unchecked
                result.put(entry.getKey(), subType instanceof ParameterizedType ? (V) objectifyMap(entry.getValue(), (ParameterizedType) subType) : (V) objectifyMap(entry.getValue(), (Class<?>) subType));
            }
        }
        return result;
    }

    private static boolean ensureListOrMap(ParameterizedType type) {
        Type rawType = type.getRawType();
        boolean isList = List.class.isAssignableFrom((Class<?>) rawType);
        boolean isMap = Map.class.isAssignableFrom((Class<?>) rawType);
        if (!isList && !isMap) {
            throw new JsonObjectifyException("Only generic classes accepted are lists and maps");
        }
        return isList;
    }
}
