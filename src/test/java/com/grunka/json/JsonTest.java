package com.grunka.json;

import com.grunka.json.type.*;
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
import java.util.*;

import static org.junit.Assert.*;

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
        assertEquals("💀", Json.parse("\"💀\"").asString().getString());
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

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ClassCanBeRecord", "FieldCanBeLocal", "unused"})
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
        assertTrue(Json.objectify("true ", Boolean.class));
        assertTrue(Json.objectify("true", boolean.class));
        assertNull(Json.objectify("null", String.class));
        assertEquals((Integer) 0, Json.objectify("0", Integer.class));
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
        assertEquals(Set.of("hello", "world"), Json.objectifySet("[\"hello\",\"world\"]", String.class));
        assertEquals(Map.of("hello", "world"), Json.objectifyMap("{\"hello\":\"world\"}", String.class));
        assertEquals(Map.of("hello", "world"), Json.objectifyMap(Json.parse("{\"hello\":\"world\"}"), String.class));
        assertEquals(Map.of(new KeyObject("hello", 0), "world"), Json.objectifyMap("{\"{\\\"name\\\":\\\"hello\\\",\\\"value\\\":0}\":\"world\"}", KeyObject.class, String.class));
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
        TestSubObject tso = new TestSubObject(List.of("hello", "world"), Map.of("a", List.of("A", "1"), "b", List.of("B", "2")), Set.of("c"));
        TestObject testObject = new TestObject("String", 5, 7L, Optional.of(BigDecimal.TEN), tso);
        String stringify = Json.stringify(testObject);
        System.out.println("stringify = " + stringify);
        TestObject objectify = Json.objectify(stringify, TestObject.class);
        assertEquals(testObject, objectify);
    }

    @SuppressWarnings({"ClassCanBeRecord", "OptionalUsedAsFieldOrParameterType"})
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

    @SuppressWarnings("ClassCanBeRecord")
    private static class TestSubObject {
        public final List<String> a;
        public final Map<String, List<String>> b;

        public final Set<String> c;

        private TestSubObject(List<String> a, Map<String, List<String>> b, Set<String> c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestSubObject that = (TestSubObject) o;
            return Objects.equals(a, that.a) && Objects.equals(b, that.b) && Objects.equals(c, that.c);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b, c);
        }
    }

    @Test
    public void shouldHandleRecords() {
        TestRecord original = new TestRecord("Hello World", 42);
        String json = Json.stringify(original);
        assertEquals("{\"a\":\"Hello World\",\"b\":42}", json);
        TestRecord parsed = Json.objectify(json, TestRecord.class);
        assertEquals(original, parsed);
    }

    @Test
    public void shouldHandleRecordsWithNoContent() {
        TestRecord parsed = Json.objectify("{}", TestRecord.class);
        assertNull(parsed.a());
        assertEquals(0, parsed.b());
    }

    private record TestRecord(String a, int b) {
    }

    @Test
    public void shouldHandleComplexMapKeys() {
        ComplexMapKeyContainer input = new ComplexMapKeyContainer(Map.of(new KeyObject("Hello", 2), new ValueObject("World", 42)));
        String json = Json.stringify(input);
        System.out.println("json = " + json);
        ComplexMapKeyContainer parsed = Json.objectify(json, ComplexMapKeyContainer.class);
        assertEquals(input, parsed);
    }

    private record ComplexMapKeyContainer(Map<KeyObject, ValueObject> data) {
    }

    private record KeyObject(String name, int value) {
        @Override
        public String toString() {
            throw new UnsupportedOperationException();
        }
    }

    private record ValueObject(String value, int priority) {
    }

    @Test
    public void shouldHandleVeryComplexGenericStructure() {
        VeryComplexStructureContainer input = new VeryComplexStructureContainer(
                Map.of("root", List.of(Optional.empty(), Optional.of(Set.of(Map.of("deep", Set.of(List.of(Map.of("key", Optional.of("value"), "none", Optional.empty()))))))))
        );
        String json = Json.stringify(input);
        VeryComplexStructureContainer objectified = Json.objectify(json, VeryComplexStructureContainer.class);
        assertEquals(input, objectified);
    }

    private record VeryComplexStructureContainer(Map<String, List<Optional<Set<Map<String, Set<List<Map<String, Optional<String>>>>>>>>> values) {
    }

    @Test
    public void shouldHandleSimpleInternalStructure() {
        SimpleMapContainer input = new SimpleMapContainer(Map.of("hello", "world"), Set.of("hello", "world"), List.of("hello", "world"));
        String json = Json.stringify(input);
        SimpleMapContainer objectified = Json.objectify(json, SimpleMapContainer.class);
        assertEquals(input, objectified);
    }

    private record SimpleMapContainer(Map<String, String> a, Set<String> b, List<String> c) {
    }

    @Test
    public void shouldObjectifyIntoTheDifferentJsonValueTypes() {
        assertEquals(new JsonString("hello world"), Json.objectify(Json.stringify("hello world"), JsonString.class));
        try {
            Json.objectify(Json.stringify(Map.of("hello", "world")), JsonString.class);
            fail("Expected exception");
        } catch (JsonObjectifyException ignore) {
        }

        String json = Json.stringify(new ObjectContainingNormalValues("string", List.of("A", "B", "C"), Map.of("key", Map.of("subkey", "subvalue"))));
        JsonValue parsed = Json.parse(json);
        ObjectContainingJsonValues objectified = Json.objectify(parsed, ObjectContainingJsonValues.class);
        assertNotNull(objectified);
        assertEquals("string", objectified.s().getString());
        assertEquals("A", objectified.a().get(0).asString().getString());
        assertEquals("B", objectified.a().get(1).asString().getString());
        assertEquals("C", objectified.a().get(2).asString().getString());
        assertEquals(new JsonObject(Map.of("subkey", new JsonString("subvalue"))), objectified.m().get("key"));
    }

    private record ObjectContainingNormalValues(String s, List<String> a, Map<String, Map<String, String>> m) {
    }

    private record ObjectContainingJsonValues(JsonString s, JsonArray a, Map<String, JsonObject> m) {
    }

    @Test
    public void shouldMapJsonValueStructure() {
        assertEquals(new JsonNumber(42), Json.map(new JsonString("hello world"), (path, jsonValue) -> ".".equals(path) ? new JsonNumber(42) : jsonValue));
        JsonValue amountMapped = Json.map(new JsonObject(Map.of("amount", new JsonString("42.0"))), (path, jsonValue) -> ".amount".equals(path) ? new JsonNumber(jsonValue.asString().getString()) : jsonValue);
        MappedStructure mappedStructure = Json.objectify(amountMapped, MappedStructure.class);
        assertNotNull(mappedStructure);
        assertEquals(new BigDecimal("42.0"), mappedStructure.amount());
        JsonValue mappedListStructure = Json.map(new JsonArray(List.of(new JsonObject(Map.of("amount", new JsonString("123.123"))))), (path, jsonValue) -> ".[].amount".equals(path) ? new JsonNumber(jsonValue.asString().getString()) : jsonValue);
        List<MappedStructure> mappedStructures = Json.objectifyList(mappedListStructure, MappedStructure.class);
        assertEquals(List.of(new MappedStructure(new BigDecimal("123.123"))), mappedStructures);
    }

    private record MappedStructure(BigDecimal amount) {
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForRecursiveStructureList() {
        List<Object> recursive = new ArrayList<>();
        //noinspection CollectionAddedToSelf
        recursive.add(recursive);
        Json.stringify(recursive);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForRecursiveStructureMap() {
        Map<String, Object> recursive = new HashMap<>();
        //noinspection CollectionAddedToSelf
        recursive.put("A", recursive);
        Json.stringify(recursive);
    }

    @Test
    public void shouldAllowSameCollectionNonRecursively() {
        List<String> list = new ArrayList<>();
        list.add("a");
        List<List<String>> container = new ArrayList<>();
        container.add(list);
        container.add(list);
        assertEquals("[[\"a\"],[\"a\"]]", Json.stringify(container));
    }
}
