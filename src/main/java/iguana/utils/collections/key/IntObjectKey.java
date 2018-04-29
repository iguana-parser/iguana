package iguana.utils.collections.key;

public class IntObjectKey implements Key {

    private final int i;
    private final Object o;

    public IntObjectKey(int i, Object o) {
        this.i = i;
        this.o = o;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IntObjectKey)) return false;

        IntObjectKey other = (IntObjectKey) obj;
        return i == other.i && o.equals(other.o);
    }

    @Override
    public int hashCode() {
        return i * 31 + o.hashCode();
    }
}
