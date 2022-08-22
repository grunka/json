package com.grunka.json.type;

import java.math.BigDecimal;
import java.util.Objects;

public class JsonNumber extends JsonValue implements Comparable<JsonNumber> {
    private final BigDecimal number;

    public JsonNumber(BigDecimal number) {
        this.number = Objects.requireNonNull(number, "Value cannot be null");
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
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public int compareTo(JsonNumber o) {
        return number.compareTo(o.number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonNumber that = (JsonNumber) o;
        return number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
