package com.grunka.json.type;

public class JsonNull extends JsonValue {
    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String toString() {
        return "null";
    }
}
