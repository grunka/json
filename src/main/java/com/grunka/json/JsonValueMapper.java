package com.grunka.json;

import com.grunka.json.type.JsonValue;

/**
 * Interface for creating mappers that convert one {@link JsonValue} to another based on the path it has
 */
public interface JsonValueMapper {
    /**
     * Mapper function which takes a path and a value and optionally transforms it into another value
     *
     * @param path The path of the {@link JsonValue} passed in
     * @param jsonValue The value at the path
     * @return Either the same object passed in to do no changes or any other {@link JsonValue} to change it
     */
    JsonValue map(String path, JsonValue jsonValue);
}
