package com.grunka.json;

import com.grunka.json.type.JsonArray;
import com.grunka.json.type.JsonBoolean;
import com.grunka.json.type.JsonNull;
import com.grunka.json.type.JsonNumber;
import com.grunka.json.type.JsonObject;
import com.grunka.json.type.JsonString;
import com.grunka.json.type.JsonValue;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

public class JsonSpeedTest {
    @Test
    public void shouldParseQuickly() {
        Random random = new Random(11);
        JsonValue root = fill(random, 0, new JsonObject());
        System.out.println("root = " + root);
    }

    private JsonValue fill(Random random, int depth, JsonValue value) {
        if (depth > 10) {
            return value;
        }
        int size = random.nextInt(10);
        if (value instanceof JsonArray array) {
            for (int i = 0; i < size; i++) {
                array.add(fill(random, depth + 1, createValue(random)));
            }
        } else if (value instanceof JsonObject object) {
            for (int i = 0; i < size; i++) {
                object.put(createString(random, 4), fill(random, depth + 1, createValue(random)));
            }
        }
        return value;
    }

    private JsonValue createValue(Random random) {
        return switch (random.nextInt(6)) {
            case 0 -> new JsonString(createString(random, 8));
            case 1 -> random.nextBoolean() ? JsonBoolean.TRUE : JsonBoolean.FALSE;
            case 2 -> new JsonNumber(BigDecimal.valueOf(random.nextInt(9000)));
            case 3 -> new JsonArray();
            case 4 -> new JsonObject();
            case 5 -> JsonNull.NULL;
            default -> throw new IllegalArgumentException();
        };
    }

    private String createString(Random random, int length) {
        final char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(characters[random.nextInt(characters.length)]);
        }
        return builder.toString();
    }

    @Test
    public void shouldStringifyQuickly() {

    }
}
