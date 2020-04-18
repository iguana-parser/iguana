package iguana.utils.collections.primitive;

public class IntArray implements IntIterable {

    public static final IntArray EMPTY = new IntArray(new int[] {}, 0, 0);

    private final int[] arr;
    private final int start; // including
    private final int end;   // excluding

    public static IntArray of(int...arr) {
        return new IntArray(arr);
    }

    public IntArray(int[] arr) {
        this(arr, 0, arr.length);
    }

    public IntArray(int[] arr, int start, int end) {
        if (start < 0 || end > arr.length)
            throw new IllegalArgumentException("start and end are not in range.");
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    public int size() {
        return end - start;
    }

    public int get(int i) {
        if (i < 0 || i >= size())
            throw new ArrayIndexOutOfBoundsException();
        return arr[i + start];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof IntArray)) return false;
        IntArray other = (IntArray) obj;
        int size = size();
        if (size != other.size()) return false;
        for (int i = 0; i < size; i++)
            if (get(i) != other.get(i))
                return false;
        return true;
    }

    @Override
    public IntIterator iterator() {
        return new IntIterator() {
            int i = start;
            @Override public boolean hasNext() { return i < end; }
            @Override public int next() { return arr[i++]; }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = start; i < end; i++)
            sb.append(arr[i] + ",");
        if (size() > 0)
            sb.delete(sb.length() - 1, sb.length());
        sb.append("]");
        return sb.toString();
    }
}
