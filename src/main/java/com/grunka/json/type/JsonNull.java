package com.grunka.json.type;

import com.grunka.json.Json;

public class JsonNull extends JsonValue {
    public static final JsonNull NULL = new JsonNull();

    private JsonNull() {
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String toString() {
        return Json.stringify(this);
    }
}
