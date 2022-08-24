package com.grunka.json;

import com.grunka.json.type.JsonArray;
import com.grunka.json.type.JsonValue;

class JsonArrayBuilder implements JsonValueBuilder {
    private final JsonArray array = new JsonArray();
    private JsonArrayBuilderState state = JsonArrayBuilderState.EXPECTING_VALUE_OR_END_ARRAY;

    @Override
    public void accept(JsonValue jsonValue) {
        if (state != JsonArrayBuilderState.EXPECTING_VALUE_OR_END_ARRAY && state != JsonArrayBuilderState.EXPECTING_VALUE) {
            throw new JsonParseException("Got value in state " + state);
        }
        array.add(jsonValue);
        state = JsonArrayBuilderState.EXPECTING_COMMA_OR_END_ARRAY;
    }

    @Override
    public void acceptComma() {
        if (state != JsonArrayBuilderState.EXPECTING_COMMA_OR_END_ARRAY) {
            throw new JsonParseException("Got comma in state " + state);
        }
        state = JsonArrayBuilderState.EXPECTING_VALUE;
    }

    @Override
    public void acceptColon() {
        throw new JsonParseException("Got colon inside of array");
    }

    @Override
    public JsonValue build() {
        if (state != JsonArrayBuilderState.EXPECTING_VALUE_OR_END_ARRAY && state != JsonArrayBuilderState.EXPECTING_COMMA_OR_END_ARRAY) {
            throw new JsonParseException("Attempting to build array in state " + state);
        }
        state = JsonArrayBuilderState.ENDED;
        return array;
    }

    private enum JsonArrayBuilderState {
        EXPECTING_VALUE_OR_END_ARRAY, EXPECTING_COMMA_OR_END_ARRAY, EXPECTING_VALUE, ENDED
    }
}
