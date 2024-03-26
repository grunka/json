package com.grunka.json;

import com.grunka.json.type.JsonValue;

/**
 * Object representing potentially partially parsed JSON
 * @param jsonValue the JSON successfully parsed
 * @param remainder the remaining string of non-parsed content
 */
public record ParsedJson(JsonValue jsonValue, String remainder) {
}
