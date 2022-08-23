package com.grunka.json;

import com.grunka.json.type.JsonArray;
import com.grunka.json.type.JsonNumber;
import com.grunka.json.type.JsonObject;
import com.grunka.json.type.JsonValue;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class JsonSpeedTest {
    @Test
    public void shouldHandleArraysQuickly() {
        Random random = new Random(11);

        JsonArray originalArray = makeArray(random, 2_000_000, 1);

        long beforeToString = System.currentTimeMillis();
        String arrayJson = originalArray.toString();
        long toStringDuration = System.currentTimeMillis() - beforeToString;
        System.out.println("Array stringify duration " + toStringDuration + "ms");

        long beforeParse = System.currentTimeMillis();
        JsonValue parsedArray = Json.parse(arrayJson);
        long parseDuration = System.currentTimeMillis() - beforeParse;
        System.out.println("Array parse duration " + parseDuration + "ms");

        System.out.println("Array JSON size " + (arrayJson.length() / (1024 * 1024)) + "MB");
        assertEquals(originalArray, parsedArray);
    }

    @Test
    public void shouldHandleObjectsQuickly() {
        Random random = new Random(11);

        JsonObject originalObject = makeObject(random, 1_200_000, 1);

        long beforeToString = System.currentTimeMillis();
        String arrayJson = originalObject.toString();
        long toStringDuration = System.currentTimeMillis() - beforeToString;
        System.out.println("Object stringify duration " + toStringDuration + "ms");

        long beforeParse = System.currentTimeMillis();
        JsonValue parsed = Json.parse(arrayJson);
        long parseDuration = System.currentTimeMillis() - beforeParse;
        System.out.println("Object parse duration " + parseDuration + "ms");

        System.out.println("Object JSON size " + (arrayJson.length() / (1024 * 1024)) + "MB");
        assertEquals(originalObject, parsed);
    }

    private static JsonArray makeArray(Random random, int size, int depth) {
        JsonArray array = new JsonArray();
        if (depth < 1) {
            return array;
        }
        for (int i = 0; i < size; i++) {
            if (random.nextDouble() < 0.4) {
                array.add(new JsonNumber(BigDecimal.valueOf(random.nextDouble())));
            } else {
                array.add(makeArray(random, size, depth - 1));
            }
        }
        return array;
    }

    private static JsonObject makeObject(Random random, int size, int depth) {
        JsonObject object = new JsonObject();
        if (depth < 1) {
            return object;
        }
        for (int i = 0; i < size; i++) {
            String key = makeString(random, 6);
            if (random.nextDouble() < 0.4) {
                object.put(key, new JsonNumber(BigDecimal.valueOf(random.nextDouble())));
            } else {
                object.put(key, makeObject(random, size, depth - 1));
            }
        }
        return object;
    }

    private static String makeString(Random random, int length) {
        final char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(characters[random.nextInt(characters.length)]);
        }
        return builder.toString();
    }

}
