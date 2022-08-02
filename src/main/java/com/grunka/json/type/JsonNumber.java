package com.grunka.json.type;

import java.math.BigDecimal;

public class JsonNumber extends JsonValue implements Comparable<JsonNumber> {
    private final BigDecimal number;

    public JsonNumber(BigDecimal number) {
        this.number = number;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    public BigDecimal getBigDecimal() {
        return number;
    }

    @Override
    public String toString() {
        return number.toPlainString();
    }

    @Override
    public int compareTo(JsonNumber o) {
        return number.compareTo(o.number);
    }
}
