package org.iguana.utils.collections.rangemap;

import java.util.Arrays;

public class BinarySearchIntRangeMap implements IntRangeMap {

    protected final int[] points;
    protected final boolean[] starts;
    protected final int[] values;

    public BinarySearchIntRangeMap(int[] points, boolean[] starts, int[] values) {
        this.points = points;
        this.starts = starts;
        this.values = values;
    }

    @Override
    public int get(int key) {
        if (key < points[0] || key > points[points.length - 1])
            return EMPTY_VALUE;

        int index = Arrays.binarySearch(points, key);

        if (index >= 0 && !starts[index]) {
            return values[index - 1];
        }

        if (index < 0) {
            index = -index - 1 - 1;
        }

        return values[index];
    }

}
