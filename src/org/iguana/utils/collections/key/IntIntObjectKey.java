package org.iguana.utils.collections.key;

public class IntIntObjectKey implements Key {

    private final int i1;
    private final int i2;
    private final Object object;

    public IntIntObjectKey(int i1, int i2, Object object) {
        this.i1 = i1;
        this.i2 = i2;
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntIntObjectKey)) return false;

        IntIntObjectKey that = (IntIntObjectKey) o;
        return i1 == that.i1 && i2 == that.i2 && object.equals(that.object);
    }

    @Override
    public int hashCode() {
        // Combine fields using a large odd multiplier.
        int hash = i1;
        hash = 0x9E3779B9 * hash + i2;
        hash = 0x9E3779B9 * hash + object.hashCode();
        return hash;
    }
}
