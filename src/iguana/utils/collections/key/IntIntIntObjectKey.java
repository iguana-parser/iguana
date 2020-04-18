package iguana.utils.collections.key;

public class IntIntIntObjectKey implements Key {

    private final int i1;
    private final int i2;
    private final int i3;
    private final Object object;

    public IntIntIntObjectKey(int i1, int i2, int i3, Object object) {
        this.i1 = i1;
        this.i2 = i2;
        this.i3 = i3;
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntIntIntObjectKey)) return false;

        IntIntIntObjectKey that = (IntIntIntObjectKey) o;
        return i1 == that.i1 && i2 == that.i2 && i3 == that.i3 && object.equals(that.object);
    }

    @Override
    public int hashCode() {
        int hash = i1;
        hash *= 31 + i2;
        hash *= 31 + i3;
        hash *= 31 + object.hashCode();
        return hash;
    }
}
