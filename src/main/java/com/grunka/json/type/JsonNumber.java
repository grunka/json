package com.grunka.json.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class JsonNumber extends JsonValue implements Comparable<JsonNumber> {
    private final String number;

    public JsonNumber(Number number) {
        Objects.requireNonNull(number, "Value cannot be null");
        if (number instanceof BigDecimal) {
            this.number = ((BigDecimal) number).toPlainString();
        } else if (number instanceof BigInteger) {
            this.number = number.toString();
        } else if (number instanceof Integer i) {
            this.number = Integer.toString(i);
        } else if (number instanceof Long l) {
            this.number = Long.toString(l);
        } else if (number instanceof Double d) {
            this.number = Double.toString(d);
        } else {
            throw new IllegalArgumentException("Cannot create JsonNumber from " + number.getClass().getSimpleName());
        }
    }

    public JsonNumber(String number) {
        Objects.requireNonNull(number, "Value cannot be null");
        this.number = number;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    public BigDecimal getBigDecimal() {
        return new BigDecimal(number);
    }

    @Override
    public String toString() {
        return number;
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
