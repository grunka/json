package com.grunka.json;

import com.grunka.json.type.JsonString;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonTest {
    @Test
    public void shouldFailInvalidContent() {
        try {
            Json.parse("{true}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("{\"key\"}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("{true:true}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("{]");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("[{]}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("{[]}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("[}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("{:true}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("{\"key\"::true}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("{,}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("[,]");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse(",");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("[:]");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse(":");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("{\"key\":}");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("}{");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("[[]");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("]");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("abc []");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse(" ");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
        try {
            Json.parse("   ");
            fail();
        } catch (JsonParseException e) {
            System.out.println(e.getMessage());
        }
    }

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
        assertFalse(Json.parse(" \r\nfalse\t ").asBoolean().getBoolean());
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
        assertTrue(Json.parse("[1]").isArray());
        assertTrue(Json.parse("[\"a\"]").isArray());
        assertEquals(9, Json.parse("[1,2,3,\"a\",\"d\",\"c\",true,false,null]").asArray().size());
    }

    @Test
    public void shouldParseObjects() {
        assertTrue(Json.parse("{}").isObject());
        assertTrue(Json.parse("{}").asObject().isEmpty());
        assertTrue(Json.parse("{\"a\":\"A\"}").isObject());
        assertEquals(new JsonString("A"), Json.parse("{\"a\":\"A\"}").asObject().get("a"));
    }

    @Test
    public void shouldParseComplexValue() {
        String json = "[1,\"a\",{\"Abc\":123,\"hello\":\"world\",\"list\":[true,false,true,null],\"null\":null,\"another one\":{}}]";
        String unparsed = Json.parse(json).toString();
        assertEquals(json, unparsed);
    }

    @Test
    public void shouldStringifyObjects() {
        assertEquals("null", Json.stringify(null));
        assertEquals("\"hello\"", Json.stringify("hello"));
        assertEquals("[\"hello\",\"world\"]", Json.stringify(List.of("hello", "world")));
        assertEquals("{\"a\":\"ONE\",\"b\":\"TWO\",\"c\":3,\"e\":\"2022-08-03T19:23:00Z\",\"g\":\"G\"}", Json.stringify(new Thing("ONE", "TWO", 3, null, Instant.parse("2022-08-03T19:23:00Z"), Optional.empty(), Optional.of("G"))));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static class Thing {
        private final String a;
        public final String b;
        public final int c;
        public final BigDecimal d;
        public final Instant e;
        public final Optional<String> f;
        public final Optional<String> g;

        private Thing(String a, String b, int c, BigDecimal d, Instant e, Optional<String> f, Optional<String> g) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.f = f;
            this.g = g;
        }
    }

    @Test
    public void shouldObjectifyPrimitiveObjects() {
        assertEquals("hello world", Json.objectify("\"hello world\"", String.class));
        assertTrue(Json.objectify("true", Boolean.class));
        assertTrue(Json.objectify("true", boolean.class));
        assertNull(Json.objectify("null", String.class));
        assertEquals((Integer) 1, Json.objectify("1", Integer.class));
        assertEquals(1, (int) Json.objectify("1", int.class));
        assertEquals((Long) 1L, Json.objectify("1", Long.class));
        assertEquals(1, (long) Json.objectify("1", long.class));
        assertEquals((Double) 1.2, Json.objectify("1.2", Double.class));
        assertEquals(1.2, Json.objectify("1.2", double.class), 0);
        assertEquals((Float) 1.2f, Json.objectify("1.2", Float.class));
        assertEquals(1.2, Json.objectify("1.2", float.class), 0.0001);
        assertEquals(BigDecimal.ONE, Json.objectify("1", BigDecimal.class));
        assertEquals(BigInteger.ONE, Json.objectify("1", BigInteger.class));
        assertEquals(List.of("hello", "world"), Json.objectifyList("[\"hello\",\"world\"]", String.class));
        assertEquals(Map.of("hello", "world"), Json.objectifyMap("{\"hello\":\"world\"}", String.class));
    }

    @Test
    public void shouldObjectifyTemporalObjects() {
        Instant instant = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(instant, Json.objectify(Json.stringify(instant), Instant.class));
        OffsetDateTime offsetDateTime = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(offsetDateTime, Json.objectify(Json.stringify(offsetDateTime), OffsetDateTime.class));
        LocalDateTime localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(localDateTime, Json.objectify(Json.stringify(localDateTime), LocalDateTime.class));
        ZonedDateTime zonedDateTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(zonedDateTime, Json.objectify(Json.stringify(zonedDateTime), ZonedDateTime.class));
        LocalDate localDate = LocalDate.now();
        assertEquals(localDate, Json.objectify(Json.stringify(localDate), LocalDate.class));
        LocalTime localTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(localTime, Json.objectify(Json.stringify(localTime), LocalTime.class));
        Year year = Year.now();
        assertEquals(year, Json.objectify(Json.stringify(year), Year.class));
        YearMonth yearMonth = YearMonth.now();
        assertEquals(yearMonth, Json.objectify(Json.stringify(yearMonth), YearMonth.class));
    }

    @Test
    public void shouldObjectifyOtherObjects() {
        TestSubObject tso = new TestSubObject(List.of("hello", "world"), Map.of("a", List.of("A", "1"), "b", List.of("B", "2")));
        TestObject testObject = new TestObject("String", 5, 7L, Optional.of(BigDecimal.TEN), tso);
        String stringify = Json.stringify(testObject);
        System.out.println("stringify = " + stringify);
        TestObject objectify = Json.objectify(stringify, TestObject.class);
        assertEquals(testObject, objectify);
    }

    private static class TestObject {
        public final String s;
        public final int i;
        public final Long l;
        public final Optional<BigDecimal> b;
        public final TestSubObject tso;

        private TestObject(String s, int i, Long l, Optional<BigDecimal> b, TestSubObject tso) {
            this.s = s;
            this.i = i;
            this.l = l;
            this.b = b;
            this.tso = tso;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return i == that.i && Objects.equals(s, that.s) && Objects.equals(l, that.l) && Objects.equals(b, that.b) && Objects.equals(tso, that.tso);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s, i, l, b, tso);
        }
    }

    private static class TestSubObject {
        public final List<String> a;
        public final Map<String, List<String>> b;

        private TestSubObject(List<String> a, Map<String, List<String>> b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestSubObject that = (TestSubObject) o;
            return Objects.equals(a, that.a) && Objects.equals(b, that.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}
