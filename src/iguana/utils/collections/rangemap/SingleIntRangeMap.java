package iguana.utils.collections.rangemap;

public class SingleIntRangeMap implements IntRangeMap {

    private final int start;
    private final int end;
    private final int value;

    SingleIntRangeMap(int start, int end, int value) {
        this.start = start;
        this.end = end;
        this.value = value;
    }

    @Override
    public int get(int key) {
        if (key < start || key > end) return EMPTY_VALUE;
        return value;
    }
}
