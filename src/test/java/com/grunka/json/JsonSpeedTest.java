package com.grunka.json;

import com.google.gson.Gson;
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

        String arrayJson = Stopwatch.run("Array stringify", originalArray::toString);
        JsonValue parsedArray = Stopwatch.run("Array parse", () -> Json.parse(arrayJson));

        com.google.gson.JsonArray parsedGson = Stopwatch.run("GSON Array parse", () -> new Gson().fromJson(arrayJson, com.google.gson.JsonArray.class));
        assertEquals(arrayJson, Stopwatch.run("GSON Array stringify", parsedGson::toString));

        System.out.println("Array JSON size " + (arrayJson.length() / (1024 * 1024)) + "MB");
        assertEquals(originalArray, parsedArray);
    }

    @Test
    public void shouldHandleObjectsQuickly() {
        Random random = new Random(11);

        JsonObject originalObject = makeObject(random, 1_200_000, 1);

        String objectJson = Stopwatch.run("Object stringify", originalObject::toString);
        JsonValue parsedObject = Stopwatch.run("Object parse", () -> Json.parse(objectJson));

        com.google.gson.JsonObject parsedGson = Stopwatch.run("GSON Object parse", () -> new Gson().fromJson(objectJson, com.google.gson.JsonObject.class));
        assertEquals(objectJson, Stopwatch.run("GSON Object stringify", parsedGson::toString));

        System.out.println("Object JSON size " + (objectJson.length() / (1024 * 1024)) + "MB");
        assertEquals(originalObject, parsedObject);
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

    private static class Stopwatch {
        private Stopwatch() {
        }

        public static <T, E extends Throwable> T run(String name, StopwatchTask<T, E> task) throws E {
            long before = System.currentTimeMillis();
            try {
                return task.run();
            } finally {
                long duration = System.currentTimeMillis() - before;
                System.out.println(name + " duration " + duration + "ms");
            }
        }
    }

    private interface StopwatchTask<T, E extends Throwable> {
        T run() throws E;
    }
}
