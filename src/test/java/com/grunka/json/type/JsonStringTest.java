package com.grunka.json.type;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonStringTest {
    @Test
    public void shouldMakeJsonStrings() {
        assertEquals("\"\\\\\"", new JsonString("\\").toString());
        assertEquals("\"hello world\"", new JsonString("hello world").toString());
        assertEquals("\"hello \\\"world\\\"\"", new JsonString("hello \"world\"").toString());
        assertEquals("\"hello\\r\\n\\t\\b\\fworld\\\\\"", new JsonString("hello\r\n\t\b\fworld\\").toString());
    }
}
