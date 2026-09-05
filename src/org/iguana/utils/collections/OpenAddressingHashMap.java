package org.iguana.utils.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OpenAddressingHashMap<K, T> {

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

    public T put(K key, T value) {
        int index = hash(key);

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

            index = (index + 1) & bitMask;

        } while (true);
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        capacity <<= 1;
        bitMask = capacity - 1;

        K[] newKeys = (K[]) new Object[capacity];
        T[] newValues = (T[]) new Object[capacity];

        for (int i = 0; i < keys.length; i++) {
            K key = keys[i];
            if (key == null) continue;

            int index = hash(key);
            while (newKeys[index] != null) {
                index = (index + 1) & bitMask;
            }
            newKeys[index] = key;
            newValues[index] = values[i];
        }

        keys = newKeys;
        values = newValues;
        threshold = (int) (loadFactor * capacity);
    }

    public T get(K key) {
        int index = hash(key);
        while (keys[index] != null && !keys[index].equals(key)) {
            index = (index + 1) & bitMask;
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

    public void clear() {
        init();
    }

    public String toString() {
        if (isEmpty()) return "{ }";

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) sb.append(keys[i]).append("=").append(values[i]).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("}");
        return sb.toString();
    }

    private int hash(K key) {
        // Same finalizer as OpenAddressingIntHashMap: keys' hashCodes may have poor low bits.
        int h = key.hashCode();
        h ^= 1;
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return h & bitMask;
    }

    public void forEachValue(Consumer<? super T> action) {
        T[] values = this.values;
        for (int i = 0; i < values.length; i++) {
            T value = values[i];
            if (value != null) action.accept(value);
        }
    }

    public List<T> values() {
        List<T> result = new ArrayList<>(size);
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) result.add(values[i]);
        }
        return result;
    }

}
