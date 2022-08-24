package com.grunka.json;

import com.grunka.json.type.JsonValue;

interface JsonValueBuilder {
    void accept(JsonValue jsonValue);

    void acceptComma();

    void acceptColon();

    JsonValue build();
}
