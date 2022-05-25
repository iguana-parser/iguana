package org.iguana.utils.collections.rangemap;

import java.util.List;

public abstract class AbstractRangeMap<T> implements RangeMap<T> {

    protected final int[] points;
    protected final boolean[] starts;
    protected final List<T>[] values;

    public AbstractRangeMap(int[] points, boolean[] starts, List<T>[] values) {
        this.points = points;
        this.starts = starts;
        this.values = values;
    }

}
