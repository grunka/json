package com.grunka.json;

import com.grunka.json.type.JsonObject;
import com.grunka.json.type.JsonValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class JsonObjectBuilder implements JsonValueBuilder {
    private final List<JsonObjectEntry> entries = new ArrayList<>();
    private JsonObjectEntry currentEntry;
    private JsonObjectBuilderState state = JsonObjectBuilderState.EXPECTING_KEY_OR_OBJECT_END;

    @Override
    public void accept(JsonValue jsonValue) {
        if (state == JsonObjectBuilderState.EXPECTING_COLON) {
            throw new JsonParseException("Got value of type " + jsonValue.getClass().getSimpleName() + " when expecting colon");
        } else if (state == JsonObjectBuilderState.EXPECTING_KEY || state == JsonObjectBuilderState.EXPECTING_KEY_OR_OBJECT_END) {
            if (!jsonValue.isString()) {
                throw new JsonParseException("Got " + jsonValue.getClass().getSimpleName() + " instead of JsonString as key in map");
            }
            state = JsonObjectBuilderState.EXPECTING_COLON;
            currentEntry = new JsonObjectEntry(jsonValue.asString().getString());
        } else if (state == JsonObjectBuilderState.EXPECTING_VALUE) {
            currentEntry.value = jsonValue;
            entries.add(currentEntry);
            currentEntry = null;
            state = JsonObjectBuilderState.EXPECTING_COMMA_OR_OBJECT_END;
        } else {
            throw new JsonParseException("Got value of type " + jsonValue.getClass().getSimpleName() + " in state " + state);
        }
    }

    @Override
    public void acceptComma() {
        if (state != JsonObjectBuilderState.EXPECTING_COMMA_OR_OBJECT_END) {
            throw new JsonParseException("Got comma in state " + state);
        }
        state = JsonObjectBuilderState.EXPECTING_KEY;
    }

    @Override
    public void acceptColon() {
        if (state != JsonObjectBuilderState.EXPECTING_COLON) {
            throw new JsonParseException("Got colon in state " + state);
        }
        state = JsonObjectBuilderState.EXPECTING_VALUE;
    }

    @Override
    public JsonValue build() {
        if (state != JsonObjectBuilderState.EXPECTING_COMMA_OR_OBJECT_END && state != JsonObjectBuilderState.EXPECTING_KEY_OR_OBJECT_END) {
            throw new JsonParseException("Attempting to build object in state " + state);
        }
        LinkedHashMap<String, JsonValue> values = new LinkedHashMap<>(entries.size());
        for (JsonObjectEntry entry : entries) {
            values.put(entry.key, entry.value);
        }
        state = JsonObjectBuilderState.ENDED;
        return new JsonObject(values);
    }

    private enum JsonObjectBuilderState {
        EXPECTING_KEY, EXPECTING_COLON, EXPECTING_VALUE, EXPECTING_COMMA_OR_OBJECT_END, EXPECTING_KEY_OR_OBJECT_END, ENDED
    }

    private static class JsonObjectEntry {
        public final String key;
        public JsonValue value;

        private JsonObjectEntry(String key) {
            this.key = key;
        }
    }
}
