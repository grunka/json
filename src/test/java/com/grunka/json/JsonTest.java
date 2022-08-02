package com.grunka.json;

import com.grunka.json.type.JsonString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonTest {
    @Test
    public void shouldParseNull() {
        assertTrue(Json.parse("null").isNull());
        assertTrue(Json.parse(" \r\nnull\t ").isNull());
    }

    @Test
    public void shouldParseBoolean() {
        assertTrue(Json.parse("true").isBoolean());
        assertTrue(Json.parse(" \r\ntrue\t ").asBoolean().isTrue());
        assertTrue(Json.parse("false").isBoolean());
        assertTrue(Json.parse(" \r\nfalse\t ").asBoolean().isFalse());
    }

    @Test
    public void shouldParseString() {
        assertTrue(Json.parse("\"hello world\"").isString());
        assertEquals("hello world", Json.parse("\"hello world\"").asString().getString());
        assertTrue(Json.parse("   \t\r\n  \"hello world\" \t\r   \n  ").isString());
        assertEquals("hello world", Json.parse("   \t\r\n  \"hello world\" \t\r   \n  ").asString().getString());
        assertTrue(Json.parse("\"hello \\\"world\\\"\"").isString());
        assertEquals("hello \"world\"", Json.parse("\"hello \\\"world\\\"\"").asString().getString());
        assertTrue(Json.parse("   \t\r\n  \"hello \\\"world\\\"\" \t\r   \n  ").isString());
        assertEquals("hello \"world\"", Json.parse("   \t\r\n  \"hello \\\"world\\\"\" \t\r   \n  ").asString().getString());
        assertEquals("hello\n\t\r world", Json.parse("\"hello\\n\\t\\r\\u0020world\"").asString().getString());
    }

    @Test
    public void shouldParseNumber() {
        assertTrue(Json.parse("1").isNumber());
        assertTrue(Json.parse("\n\t1    \t  ").isNumber());
        assertTrue(Json.parse("-1").isNumber());
        assertTrue(Json.parse("-1.1").isNumber());
        assertTrue(Json.parse("1.1").isNumber());
        assertTrue(Json.parse("0.1").isNumber());
        assertTrue(Json.parse("-0.1").isNumber());
        assertTrue(Json.parse("-0.1e11").isNumber());
        assertTrue(Json.parse("-0.1e+11").isNumber());
        assertTrue(Json.parse("-0.1e-11").isNumber());
        assertTrue(Json.parse("0.1e-11").isNumber());
        assertTrue(Json.parse("0.1E11").isNumber());
        assertEquals(1, Json.parse("1").asNumber().getBigDecimal().intValue());
    }

    @Test
    public void shouldParseArray() {
        assertTrue(Json.parse("[]").isArray());
        assertTrue(Json.parse("[]").asArray().isEmpty());
        assertTrue(Json.parse(" \n [\n\r]  \t ").isArray());
        assertTrue(Json.parse(" \n [\n\r]  \t ").asArray().isEmpty());
        assertTrue(Json.parse("[[]]").isArray());
        assertEquals(1, Json.parse("[[]]").asArray().size());
        assertTrue(Json.parse("[[]]").asArray().get(0).asArray().isEmpty());
        assertTrue(Json.parse("[[], []]").isArray());
        assertEquals(2, Json.parse("[[], []]").asArray().size());
        assertTrue(Json.parse("[[], []]").asArray().get(0).asArray().isEmpty());
        assertTrue(Json.parse("[[], []]").asArray().get(1).asArray().isEmpty());
        assertTrue(Json.parse("[1,2,3,\"a\",\"d\",\"c\"]").isArray());
        assertEquals(9, Json.parse("[1,2,3,\"a\",\"d\",\"c\",true,false,null]").asArray().size());
    }

    @Test
    public void shouldParseObjects() {
        assertTrue(Json.parse("{}").isObject());
        assertTrue(Json.parse("{}").asObject().isEmpty());
        assertTrue(Json.parse("{\"a\":\"A\"}").isObject());
        assertEquals(new JsonString("A"), Json.parse("{\"a\":\"A\"}").asObject().get(new JsonString("a")));
    }

    //TODO good error messages
}
