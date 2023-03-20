package org.iguana.util;

public class Holder<T> {

    private T val;

    public void set(T val) { this.val = val; }

    public T get() { return val; }

    public boolean has() { return val != null; }
}
