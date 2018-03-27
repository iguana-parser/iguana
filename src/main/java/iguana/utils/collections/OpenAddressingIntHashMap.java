package iguana.utils.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.IntFunction;

public class OpenAddressingIntHashMap<T> implements IntHashMap<T> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.7f;

    private final int initialCapacity;
    private final float loadFactor;

    private int capacity;

    private int size;

    private int threshold;

    private int rehashCount;

    private int collisionsCount;

    /**
     * capacity - 1
     * The bitMask is used to adj the p most-significant bytes of the multiplicaiton.
     */
    private int bitMask;

    private int[] keys;

    private T[] values;

    public OpenAddressingIntHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public OpenAddressingIntHashMap(int initalCapacity) {
        this(initalCapacity, DEFAULT_LOAD_FACTOR);
    }

    public OpenAddressingIntHashMap(int initialCapacity, float loadFactor) {
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
        keys = new int[capacity];
        Arrays.fill(keys, -1);

        values = (T[]) new Object[capacity];

        size = 0;
        rehashCount = 0;
        collisionsCount = 0;
    }

    @Override
    public boolean containsKey(int key) {
        return get(key) != null;
    }

    @Override
    public T computeIfAbsent(int key, IntFunction<T> f) {
        int j = 0;
        int index = hash(key, j);

        do {
            if (keys[index] == -1) {
                keys[index] = key;
                T val = f.apply(key);
                values[index] = val;
                size++;
                if (size >= threshold) {
                    rehash();
                }
                return val;
            } else if (keys[index] == key) {
                return values[index];
            }

            collisionsCount++;

            index = hash(key, ++j);

        } while (true);
    }

    @Override
    public T compute(int key, IntKeyMapper<T> f) {
        int j = 0;
        int index = hash(key, j);

        do {
            if (keys[index] == -1) {    // Key is not in the map
                keys[index] = key;
                T val = f.apply(key, null);
                values[index] = val;
                size++;
                if (size >= threshold) {
                    rehash();
                }
                return val;
            } else if (keys[index] == key) {
                T val = values[index];
                val = f.apply(key, val);
                values[index] = val;
                return null;
            }

            collisionsCount++;

            index = hash(key, ++j);

        } while (true);
    }

    @Override
    public T put(int key, T value) {
        int j = 0;
        int index = hash(key, j);

        do {
            if (keys[index] == -1) {
                keys[index] = key;
                values[index] = value;
                size++;
                if (size >= threshold) {
                    rehash();
                }
                return null;
            } else if (keys[index] == key) {
                T oldValue = values[index];
                values[index] = value;
                return oldValue;
            }

            collisionsCount++;

            index = hash(key, ++j);

        } while (true);
    }

    @Override
    public T remove(int key) {
        int j = 0;
        int index = hash(key, j);

        while (keys[index] != -1 && keys[index] != key) {
            index = hash(key, ++j);
        }

        T v = values[index];
        values[index] = null;
        keys[index] = -1;
        return v;
    }

    private void rehash() {

        capacity <<= 1;

        bitMask = capacity - 1;

        int[] newKeys = new int[capacity];
        Arrays.fill(newKeys, -1);

        @SuppressWarnings("unchecked")
        T[] newValues = (T[]) new Object[capacity];

        label:
        for (int i = 0; i < keys.length; i++) {
            int j = 0;
            int key = keys[i];

            T value = values[i];

            if (key != -1) {

                int index = hash(key, j);

                do {
                    if (newKeys[index] == -1) {
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
        rehashCount++;
    }

    @Override
    public T get(int key) {
        int j = 0;
        int index = hash(key, j);
        while (keys[index] != -1 && keys[index] != key) {
            index = hash(key, ++j);
        }
        return values[index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int getInitialCapacity() {
        return initialCapacity;
    }

    @Override
    public int getEnlargeCount() {
        return rehashCount;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        init();
    }

    public int getCollisionCount() {
        return collisionsCount++;
    }

    public String toString() {
        if (isEmpty()) return "{ }";

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        Iterator<Entry<T>> it = iterator();

        while (true) {
            Entry<T> next = it.next();
            sb.append(next);
            if (!it.hasNext()) {
                sb.append("}");
                break;
            }
            sb.append(", ");
        }

        return sb.toString();
    }

    private int hash(int h, int j) {
        h ^= 1;
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return (h + j) & bitMask;
    }

    private IntMapIterator it = new IntMapIterator();

    @Override
    public Iterable<T> values() {
        return () -> it.reset();
    }

    @Override
    public Iterator<Entry<T>> iterator() {
        return new Iterator<Entry<T>>() {
            int it = 0;
            int i = 0;

            @Override
            public boolean hasNext() {
                return it < size;
            }

            @Override
            public Entry<T> next() {
                while (values[i++] == null) ;
                it++;
                return new Entry<>(keys[i - 1], values[i - 1]);
            }
        };
    }

    private class IntMapIterator implements Iterator<T> {
        int count = 0;
        int i = 0;

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public T next() {
            while (values[i++] == null);
            count++;
            return values[i - 1];
        }

        public IntMapIterator reset() {
            count = 0;
            i = 0;
            return this;
        }
    }

}
