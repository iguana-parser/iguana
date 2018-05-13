package iguana.utils.collections.rangemap;

import java.util.List;

import static java.util.Collections.emptyList;

public class LinearSerachRangeMap<T> extends AbstractRangeMap<T> {

    public LinearSerachRangeMap(int[] points, boolean[] starts, List<T>[] values) {
        super(points, starts, values);
    }

    @Override
    public List<T> get(int key) {
        if (key < points[0] || key > points[points.length - 1])
            return emptyList();

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
