package com.tui.next.tuils;

import java.io.Serializable;
import java.util.Map;

public class SimpleMutableEntry<K, V> implements Map.Entry<K, V>, Serializable {

    private final K key;
    private V value;

    public SimpleMutableEntry(K theKey, V theValue) {
        key = theKey;
        value = theValue;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public V setValue(V object) {
        V result = value;
        value = object;
        return result;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
