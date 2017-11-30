package iguana.utils.collections.tuple;

public class IntTuple {

    private final int first;
    private final int second;

    public IntTuple(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public int first() {
        return first;
    }

    public int second() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof IntTuple)) return false;
        IntTuple other = (IntTuple) obj;
        return first == other.first && second == other.second;
    }

    @Override
    public int hashCode() {
        return first + 31 * second;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }

    public static IntTuple of(int first, int second) {
        return new IntTuple(first, second);
    }
}
