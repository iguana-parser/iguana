package org.iguana.utils.collections.primitive;

public class IntKeyEntry<T> {

    int key;
    T val;
    IntKeyEntry<T> next;

    public IntKeyEntry(int key, T val) {
        this.key = key;
        this.val = val;
    }

    @Override
    public String toString() {
        return "(" + key + ", " + val + ")";
    }
}
