package com.grunka.json;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JsonTest {
    @Test
    public void shouldParseJson() {
        assertTrue(Json.parse("null").isNull());
        assertTrue(Json.parse(" \r\nnull\t ").isNull());
        assertTrue(Json.parse("true").isBoolean());
        assertTrue(Json.parse(" \r\ntrue\t ").asBoolean().isTrue());
        assertTrue(Json.parse("false").isBoolean());
        assertTrue(Json.parse(" \r\nfalse\t ").asBoolean().isFalse());
    }
}
