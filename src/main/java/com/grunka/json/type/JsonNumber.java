package com.grunka.json.type;

import java.math.BigDecimal;

public class JsonNumber extends JsonValue {
    private final String number;

    public JsonNumber(String number) {
        this.number = number;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    public int toInt() {
        return new BigDecimal(number).intValue();
    }
}
