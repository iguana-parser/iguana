package iguana.utils.collections.key;

public class ObjectKey2 implements Key {

    private final Object o1;
    private final Object o2;
    private final int hash;

    public ObjectKey2(Object o1, Object o2, int hash) {
        this.o1 = o1;
        this.o2 = o2;
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ObjectKey2)) return false;

        ObjectKey2 that = (ObjectKey2) o;
        return hash == that.hash &&
                o1.equals(that.o1) &&
                o2.equals(that.o2);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", o1, o2);
    }
}