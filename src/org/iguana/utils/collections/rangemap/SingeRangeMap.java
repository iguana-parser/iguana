package org.iguana.utils.collections.rangemap;

import java.util.Collections;
import java.util.List;

import static org.iguana.utils.collections.CollectionsUtil.list;

public class SingeRangeMap<T> implements RangeMap<T> {

    private final int start;
    private final int end;
    private final List<T> values;

    SingeRangeMap(int start, int end, T value) {
        this.start = start;
        this.end = end;
        this.values = list(value);
    }

    @Override
    public List<T> get(int key) {
        if (key < start || key > end)
            return Collections.emptyList();

        return values;
    }
}
