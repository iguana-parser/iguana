package org.iguana.utils.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OpenAddressingHashMap<K, T> implements Map<K, T> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.7f;

    private final int initialCapacity;
    private final float loadFactor;

    private int capacity;

    private int size;

    private int threshold;

    /**
     * capacity - 1
     * The bitMask is used to adj the p most-significant bytes of the multiplicaiton.
     */
    private int bitMask;

    private K[] keys;

    private T[] values;

    public OpenAddressingHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public OpenAddressingHashMap(int initalCapacity) {
        this(initalCapacity, DEFAULT_LOAD_FACTOR);
    }

    public OpenAddressingHashMap(int initialCapacity, float loadFactor) {
        this.initialCapacity = initialCapacity < 0 ? DEFAULT_INITIAL_CAPACITY : initialCapacity;
        this.loadFactor = (loadFactor < 0 || loadFactor > 1) ? DEFAULT_LOAD_FACTOR : loadFactor;
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        capacity = 1;
        while (capacity < initialCapacity) capacity <<= 1;

        bitMask = capacity - 1;

        threshold = (int) (loadFactor * capacity);
        keys = (K[]) new Object[capacity];

        values = (T[]) new Object[capacity];

        size = 0;
    }

    @Override
    public T put(K key, T value) {
        int j = 0;
        int index = hash(key, j);

        do {
            if (keys[index] == null) {
                keys[index] = key;
                values[index] = value;
                size++;
                if (size >= threshold) {
                    rehash();
                }
                return null;
            } else if (keys[index].equals(key)) {
                T oldValue = values[index];
                values[index] = value;
                return oldValue;
            }

            index = hash(key, ++j);

        } while (true);
    }

    @Override
    public T remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends T> m) {

    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        capacity <<= 1;
        bitMask = capacity - 1;

        K[] newKeys = (K[]) new Object[capacity];
        T[] newValues = (T[]) new Object[capacity];

        label:
        for (int i = 0; i < keys.length; i++) {
            int j = 0;
            K key = keys[i];

            T value = values[i];

            if (key != null) {

                int index = hash(key, j);

                do {
                    if (newKeys[index] == null) {
                        newKeys[index] = key;
                        newValues[index] = value;
                        continue label;
                    }

                    index = hash(key, ++j);

                } while (true);
            }
        }

        keys = newKeys;
        values = newValues;
        threshold = (int) (loadFactor * capacity);
    }

    public T get(Object key) {
        int j = 0;
        int index = hash(key, j);
        while (keys[index] != null && !keys[index].equals(key)) {
            index = hash(key, ++j);
        }
        return values[index];
    }

    public int size() {
        return size;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        init();
    }

    public String toString() {
        if (isEmpty()) return "{ }";

        StringBuilder sb = new StringBuilder();
        sb.append("{");


        for (Entry<K, T> entry : entrySet()) {
            if (entry.getKey() != null) {
                sb.append("(" + entry.getKey() + ", " + entry.getValue() + ")");
                sb.append(", ");
            }
        }

        sb.delete(sb.length() - 2, sb.length());
        sb.append("}");

        return sb.toString();
    }

    private int hash(Object key, int j) {
        return (key.hashCode() + j) & bitMask;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) keySet.add(keys[i]);
        }
        return keySet;
    }

    @Override
    public Collection<T> values() {
        Set<T> values = new HashSet<>(size);

        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i] != null) values.add(this.values[i]);
        }

        return values;
    }

    @Override
    public Set<Entry<K, T>> entrySet() {
        Set<Entry<K, T>> entrySet = new HashSet<>();

        for (int i = 0; i < keys.length; i++) {
            final K key = keys[i];
            final T value = values[i];

            entrySet.add(new Entry<K, T>() {
                @Override
                public K getKey() {
                    return key;
                }

                @Override
                public T getValue() {
                    return value;
                }

                @Override
                public T setValue(T value) {
                    return null;
                }
            });
        }

        return entrySet;
    }
}
