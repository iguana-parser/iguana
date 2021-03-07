package iguana.utils.collections.rangemap;

import java.util.Arrays;
import java.util.List;

public class ArrayIntRangeMap<T> implements IntRangeMap {

    private final int start;
    private final int end;
    private final int[] values;

    ArrayIntRangeMap(int start, int end, List<Range> ranges, List<T> rangeValues) {
        this.start = start;
        this.end = end;
        this.values = new int[end - start + 1];
        Arrays.fill(this.values, EMPTY_VALUE);

        for (int i = 0; i < ranges.size(); i++) {
            Range range = ranges.get(i);
            for (int j = range.getStart(); j <= range.getEnd(); j++) {
                if (!(rangeValues.get(i) instanceof Integer)) {
                    throw new RuntimeException("");
                }
                values[j - start] = (Integer) rangeValues.get(i);
            }
        }
    }


    @Override
    public int get(int key) {
        if (key < start || key > end) return EMPTY_VALUE;
        return values[key - start];
    }
}
