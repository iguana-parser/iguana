package iguana.utils.collections.rangemap;

public abstract class AbstractRangeMap<T> implements RangeMap<T> {

    protected final int[] points;
    protected final boolean[] starts;
    protected final T[][] values;

    public AbstractRangeMap(int[] points, boolean[] starts, T[][] values) {
        this.points = points;
        this.starts = starts;
        this.values = values;
    }
}
