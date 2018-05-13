package iguana.utils.collections.rangemap;

import static iguana.utils.collections.CollectionsUtil.empty;

public class LinearSerachRangeMap<T> extends AbstractRangeMap<T> {

    public LinearSerachRangeMap(int[] points, boolean[] starts, T[][] values) {
        super(points, starts, values);
    }

    @Override
    public T[] get(int key) {
        if (key < points[0] || key > points[points.length - 1])
            return empty();

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
