package org.iguana.utils.collections.rangemap;

public class LinearSerachIntRangeMap<T> implements IntRangeMap {

    private final int[] points;
    private final boolean[] starts;
    private final int[] values;

    public LinearSerachIntRangeMap(int[] points, boolean[] starts, int[] values) {
        this.points = points;
        this.starts = starts;
        this.values = values;
    }

    @Override
    public int get(int key) {
        if (key < points[0] || key > points[points.length - 1])
            return EMPTY_VALUE;

        for (int index = 0; index < points.length; index++) {
            if (points[index] == key) {
                if (!starts[index]) {
                    return values[index - 1];
                }
                return values[index];
            }

            if (points[index] > key) {
                return values[index - 1];
            }
        }

        throw new RuntimeException("Should not reach here!");
    }

}
