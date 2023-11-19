package org.iguana.utils.collections.primitive;


import java.util.Arrays;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class OpenAddressingIntHashSet implements IntSet {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.7f;

    private final int initialCapacity;

    private int capacity;

    private int size;

    private int threshold;

    private final float loadFactor;

    private int rehashCount;

    private int collisionsCount;

    /**
     * capacity - 1
     * The bitMask is used to adj the p most-significant bytes of the multiplicaiton.
     */
    private int bitMask;

    private int[] table;

    public OpenAddressingIntHashSet() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public OpenAddressingIntHashSet(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public OpenAddressingIntHashSet(int initialCapacity, float loadFactor) {

        this.initialCapacity = initialCapacity;

        this.loadFactor = loadFactor;

        capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        
        bitMask = capacity - 1;

        threshold = (int) (loadFactor * capacity);
        table = new int[capacity];
        Arrays.fill(table, -1);
    }

    public static OpenAddressingIntHashSet from(int... elements) {
        OpenAddressingIntHashSet set = new OpenAddressingIntHashSet(elements.length);
        for (int e : elements) {
            set.add(e);
        }
        return set;
    }

    @Override
    public boolean contains(int key) {
        return get(key) != -1;
    }

    @Override
    public boolean add(int element) {
        if (element < 0) throw new IllegalArgumentException("Element " + element + " is negative");

        int index = hash(element);

        do {
            if (table[index] == -1) {
                table[index] = element;
                size++;
                if (size >= threshold) {
                    rehash();
                }
                return true;
            }
            if (table[index] == element) {
                return false;
            }
            collisionsCount++;
            index = (index + 1) & bitMask;
        } while (true);
    }

    private void rehash() {
        capacity <<= 1;
        bitMask = capacity - 1;

        int[] newTable = new int[capacity];
        Arrays.fill(newTable, -1);

        label:
        for (int key : table) {
            if (key != -1) {
                int index = hash(key);
                do {
                    if (newTable[index] == -1) {
                        newTable[index] = key;
                        continue label;
                    }
                    index = (index + 1) & bitMask;
                } while (true);
            }
        }

        table = newTable;

        threshold = (int) (loadFactor * capacity);
        rehashCount++;
    }

    private int hash(int key) {
        return key * 31 & bitMask;
    }

    public int get(int key) {
        return table[hash(key)];
    }

    @Override
    public int size() {
        return size;
    }

    public int getInitialCapacity() {
        return initialCapacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getEnlargeCount() {
        return rehashCount;
    }

    @Override
    public void clear() {
        Arrays.fill(table, -1);
        size = 0;
    }

    public int getCollisionCount() {
        return collisionsCount++;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int t : table)
            if (t != -1)
                sb.append(t).append(", ");

        if (sb.length() > 2)
            sb.delete(sb.length() - 2, sb.length());

        sb.append("}");
        return sb.toString();
    }

    @Override
    public IntIterator iterator() {
        return new IntIterator() {
            int i = 0;      // index to the next element in the table to check
            int count = 0;  // the number of iterated elements

            @Override
            public boolean hasNext() {
                return count < size;
            }

            @Override
            public int next() {
                while (true) {
                    if (table[i++] != -1) break;
                }
                count++;
                return table[i - 1];
            }
        };
    }
}
