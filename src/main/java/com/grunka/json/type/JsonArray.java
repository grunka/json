package com.grunka.json.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonArray extends JsonValue implements List<JsonValue> {
    private final List<JsonValue> values = new ArrayList<>();

    @Override
    public String toString() {
        return "[" + values.stream().map(Object::toString).collect(Collectors.joining(",")) + "]";
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return values.contains(o);
    }

    @Override
    public Iterator<JsonValue> iterator() {
        return values.iterator();
    }

    @Override
    public Object[] toArray() {
        return values.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        //noinspection SuspiciousToArrayCall
        return values.toArray(a);
    }

    @Override
    public boolean add(JsonValue jsonValue) {
        return values.add(jsonValue);
    }

    @Override
    public boolean remove(Object o) {
        return values.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        //noinspection SlowListContainsAll
        return values.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends JsonValue> c) {
        return values.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends JsonValue> c) {
        return values.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return values.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return values.retainAll(c);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public JsonValue get(int index) {
        return values.get(index);
    }

    @Override
    public JsonValue set(int index, JsonValue element) {
        return values.set(index, element);
    }

    @Override
    public void add(int index, JsonValue element) {
        values.add(index, element);
    }

    @Override
    public JsonValue remove(int index) {
        return values.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return values.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return values.lastIndexOf(o);
    }

    @Override
    public ListIterator<JsonValue> listIterator() {
        return values.listIterator();
    }

    @Override
    public ListIterator<JsonValue> listIterator(int index) {
        return values.listIterator(index);
    }

    @Override
    public List<JsonValue> subList(int fromIndex, int toIndex) {
        return values.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JsonArray that = (JsonArray) o;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
