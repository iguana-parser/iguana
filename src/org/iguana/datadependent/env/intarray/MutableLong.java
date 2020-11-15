package org.iguana.datadependent.env.intarray;

public class MutableLong {

    private long value;

    public void setValue(long value) {
        this.value = value;
    }

    public final int getHigherOrderInt() {
        return (int) (value >> 32);
    }

    public final int getLowerOrderInt() {
        return (int) (value & 0xffffffffL);
    }

}
