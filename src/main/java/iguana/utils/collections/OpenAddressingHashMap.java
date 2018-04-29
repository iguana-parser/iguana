package iguana.utils.collections;

import java.util.Iterator;
import java.util.function.Function;

public class OpenAddressingHashMap {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.7f;

    private final float loadFactor;

    private int size;

    private int threshold;

    private Object[] table;

    public OpenAddressingHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public OpenAddressingHashMap(float loadFactor) {
        this(DEFAULT_INITIAL_CAPACITY, loadFactor);
    }

    public OpenAddressingHashMap(int initalCapacity) {
        this(initalCapacity, DEFAULT_LOAD_FACTOR);
    }

    public OpenAddressingHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) throw new IllegalArgumentException("Initial capacity cannot be negative");
        if (loadFactor < 0 || loadFactor > 1.0f) throw new IllegalArgumentException("Load factor must be between 0 and 1");

        int capacity = 1;

        // Find the smallest power of 2 that is greater than or equal to initial capacity
        while (capacity < initialCapacity) capacity <<= 1;

        threshold = (int) (loadFactor * capacity);
        table = new Object[capacity * 2];
        this.loadFactor = (loadFactor < 0 || loadFactor > 1) ? DEFAULT_LOAD_FACTOR : loadFactor;
    }

//    public Object computeIfAbsent(Key key, BiFunction<? super K, ? super V, ? extends V> f) {
//        int j = 0;
//        int index = hash(key, j);
//
//        do {
//            if (table[index] == null) {
//                table[index] = new Entry<>(key, f.apply(key));
//                size++;
//                if (size >= threshold) {
//                    rehash();
//                }
//                return val;
//            } else if (table[index] == key) {
//                return values[index];
//            }
//
//            index = hash(key, ++j);
//
//        } while (true);
//    }

    public Object compute(Object key, Function<Object, Object> f) {
//        int j = 0;
//        int index = hash(key, j);
//
//        do {
//            if (table[index] == -1) {    // Key is not in the map
//                table[index] = key;
//                K val = f.apply(key, null);
//                values[index] = val;
//                size++;
//                if (size >= threshold) {
//                    rehash();
//                }
//                return val;
//            } else if (table[index] == key) {
//                K val = values[index];
//                val = f.apply(key, val);
//                values[index] = val;
//                return null;
//            }
//
//            collisionsCount++;
//
//            index = hash(key, ++j);
//
//        } while (true);
        return null;
    }

    public Object get(Object key) {
        Object[] table = this.table;
        int length = table.length;
        int i = hash(key, length);
        while (true) {
            Object item = table[i];
            if (item == null)
                return null;
            if (item.equals(key))
                return table[i + 1];
            i = nextKeyIndex(i, length);
        }
    }

    private static int nextKeyIndex(int i, int length) {
        return (i + 2 < length ? i + 2 : 0);
    }


    public Object put(Object key, Object object) {
        Object[] table = this.table;
        int length = table.length;
        int i = hash(key, length);

        while (true) {
            Object item = table[i];
            if (table[i] == null) {
                table[i] = key;
                table[i + 1] = object;
                size++;
                if (size > threshold) {
                    resize();
                }
                return null;
            }
            if (item.equals(key)) {
                Object oldValue = table[i + 1];
                table[i + 1] = object;
                return oldValue;
            }

            i = nextKeyIndex(i, length);
        }
    }

    private void resize() {
        int length = table.length;

        int newLength = length * 2;
        Object[] newTable = new Object[newLength];

        for (int i = 0; i < length; i += 2) {
            Object key = table[i];
            if (table[i] != null) {
                int index = hash(key, newLength);
                newTable[index] = key;
                newTable[index + 1] = table[i + 1];
            }
        }

        table = newTable;
        threshold = (int) (loadFactor * length);
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return table.length / 2;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Iterable<Object> values() {
        return () -> valueIterator.reset();
    }

    private ValueIterator valueIterator = new ValueIterator();

    private class ValueIterator implements Iterator<Object> {

        int current = 0;
        int count = 0;

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public Object next() {
            int length = table.length;

            while (true) {
                if (table[current] != null) {
                    count++;
                    Object next = table[current + 1];
                    current = nextKeyIndex(current, length);
                    return next;
                }
                current = nextKeyIndex(current, length);
            }
        }

        private ValueIterator reset() {
            count = 0;
            return this;
        }
    }

    private int hash(Object key, int length) {
        int h = key.hashCode();
        return ((h << 1) - (h << 8)) & (length - 1);
    }

    public int getThreshold() {
        return threshold;
    }
}
