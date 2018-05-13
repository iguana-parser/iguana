package iguana.utils.collections.rangemap;

import static iguana.utils.collections.CollectionsUtil.empty;

public class SingeRangeRangeMap<T> implements RangeMap<T> {

    private final int start;
    private final int end;
    private final T[] values;

    public SingeRangeRangeMap(int start, int end, T value) {
        this.start = start;
        this.end = end;
        this.values = (T[]) new Object[] {value};
    }

    @Override
    public T[] get(int key) {
        if (key < start || key > end)
            return empty();

        return values;
    }
}
