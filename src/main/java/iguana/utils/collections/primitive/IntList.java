package iguana.utils.collections.primitive;

import java.util.Arrays;
import java.util.function.IntConsumer;

public class IntList implements IntStack {

    private static final int INITIAL_CAPACITY = 4;

    private int capacity;
    private int[] arr;
    private int size;

    public static IntList of(int...elements) {
        IntList list = new IntList();
        for (int e : elements) list.add(e);
        return list;
    }

    public IntList() {
        this(INITIAL_CAPACITY);
    }

    public IntList(int initialCapacity) {
        capacity = initialCapacity;
        arr = new int[initialCapacity];
    }

    public int get(int i) {
        if (i < 0 || i >= size)
            throw new RuntimeException("Index is not in range");
        return arr[i];
    }

    public void set(int i, int v) {
        if (i < 0 || i >= size)
            throw new RuntimeException("Index is not in range");
        arr[i] = v;
    }

    public int getCapacity() {
        return capacity;
    }

    public void add(int val) {
        ensureSize(size);
        arr[size++] = val;
    }

    public int[] toArray() { return arr; }

    public IntArray toIntArray() {
        return new IntArray(arr, 0, size);
    }

    @Override
    public void push(int val) {
        ensureSize(size);
        arr[size++] = val;
    }

    @Override
    public int pop() {
        if (isEmpty()) throw new UnsupportedOperationException("Stack is empty.");
        return arr[--size];
    }

    @Override
    public int peek() {
        if (isEmpty()) throw new UnsupportedOperationException("Stack is empty.");
        return arr[size - 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    public void foreach(IntConsumer c) {
        for (int i = 0; i < size; i++)
            c.accept(arr[i]);
    }

    @Override
    public void popOrder(IntConsumer c) {
        for (int i = size - 1; i >= 0; i--)
            c.accept(arr[i]);
    }

    private void ensureSize(int i) {
        if (i >= capacity) {
            capacity = capacity << 1;
            arr = Arrays.copyOf(arr, capacity);
        }
    }

    @Override
    public IntIterator iterator() {
        return new IntIterator() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public int next() {
                return arr[i++];
            }
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IntList)) return false;
        IntList other = (IntList) obj;
        if (size != other.size) return false;
        for (int i = 0; i < size; i++)
            if (arr[i] != other.arr[i]) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++)
            sb.append(arr[i] + ", ");
        if (size > 0)
            sb.delete(sb.length() - 2, sb.length());
        sb.append("]");
        return sb.toString();
    }
}
