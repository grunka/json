package com.grunka.json;

import com.grunka.json.type.JsonValue;

public record ParsedJson(JsonValue jsonValue, String remainder) {
}
