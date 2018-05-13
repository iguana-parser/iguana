package iguana.utils.collections.rangemap;

import java.util.Arrays;

import static iguana.utils.collections.CollectionsUtil.empty;

public class BinarySearchRangeMap<T> extends AbstractRangeMap<T> {

    public BinarySearchRangeMap(int[] points, boolean[] starts, T[][] values) {
        super(points, starts, values);
    }

    @Override
    public T[] get(int key) {
        if (key < points[0] || key > points[points.length - 1])
            return empty();

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
