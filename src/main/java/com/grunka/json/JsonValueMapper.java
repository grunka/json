package com.grunka.json;

import com.grunka.json.type.JsonValue;

public interface JsonValueMapper {
    JsonValue map(String path, JsonValue jsonValue);
}
