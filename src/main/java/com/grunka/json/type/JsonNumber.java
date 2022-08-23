package com.grunka.json.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class JsonNumber extends JsonValue implements Comparable<JsonNumber> {
    private final BigDecimal number;

    public JsonNumber(Number number) {
        Objects.requireNonNull(number, "Value cannot be null");
        if (number instanceof BigDecimal) {
            this.number = (BigDecimal) number;
        } else if (number instanceof BigInteger) {
            this.number = new BigDecimal((BigInteger) number);
        } else if (number instanceof Integer) {
            this.number = BigDecimal.valueOf((Integer) number);
        } else if (number instanceof Long) {
            this.number = BigDecimal.valueOf((Long) number);
        } else if (number instanceof Double) {
            this.number = BigDecimal.valueOf((Double) number);
        } else {
            throw new IllegalArgumentException("Cannot create JsonNumber from " + number.getClass().getSimpleName());
        }
    }

    public JsonNumber(String number) {
        this(new BigDecimal(number));
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
