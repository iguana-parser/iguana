package iguana.utils.collections.key;

public class IntKey1 implements Key, Comparable<IntKey1> {

    private final int k;

    public IntKey1(int k) {
        this.k = k;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof IntKey1))
            return false;

        IntKey1 other = (IntKey1) obj;
        return k == other.k;
    }

    @Override
    public int hashCode() {
        return k;
    }


    @Override
    public int compareTo(IntKey1 other) {
        return k - other.k;
    }
}
