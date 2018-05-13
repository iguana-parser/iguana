package iguana.utils.collections.rangemap;

import java.util.*;

import static iguana.utils.collections.CollectionsUtil.empty;

public class RangeMapBuilder<T> {

    private List<Range> ranges = new ArrayList<>();
    private List<T> values = new ArrayList<>();

    public RangeMapBuilder<T> put(Range range, T value) {
        ranges.add(range);
        values.add(value);
        return this;
    }

    public RangeMap<T> build() {
        if (ranges.isEmpty()) {
            throw new RuntimeException("Cannot build an empty range map");
        }

        if (ranges.size() == 1) {
            Range range = ranges.get(0);
            return new SingeRangeRangeMap<>(range.getStart(), range.getEnd(), values.get(0));
        }

        List<Point<T>> points = new ArrayList<>(ranges.size() * 2);

        for (int i = 0; i < ranges.size(); i++) {
            Range range = ranges.get(i);
            points.add(new Point<>(range, true, values.get(i)));
            points.add(new Point<>(range, false, values.get(i)));
        }

        Collections.sort(points);

        Map<Point<T>, Set<T>> cumulativeValues = new LinkedHashMap<>(ranges.size() * 2);

        for (int i = 0; i < ranges.size() * 2; i++) {
            Point<T> point = points.get(i);
            Set<T> values = cumulativeValues.computeIfAbsent(point, k -> new HashSet<>());
            if (i > 0)
                values.addAll(cumulativeValues.get(points.get(i - 1)));

            if (point.isStart()) {
                values.add(point.value);
            } else {
                values.remove(point.value);
            }
        }

        int size = cumulativeValues.size();
        int[] keys = new int[size];
        boolean[] starts = new boolean[size];
        T[][] vals = (T[][]) new Object[size][];

        int i = 0;
        for (Map.Entry<Point<T>, Set<T>> entry : cumulativeValues.entrySet()) {
            Point<T> point = entry.getKey();
            keys[i] = point.index;
            if (point.isStart()) {
                starts[i] = true;
            }

            if (cumulativeValues.size() == 0) {
                vals[i] = empty();
            } else {
                vals[i] = (T[]) cumulativeValues.get(point).toArray();
            }
            i++;
        }

        if (size < 8) {
            return new LinearSerachRangeMap<>(keys, starts, vals);
        }

        return new BinarySearchRangeMap<>(keys, starts, vals);
    }

    static class Point<T> implements Comparable<Point<T>> {
        int index;
        boolean start;
        T value;

        Point(Range range, boolean start, T value) {
            if (start)
                this.index = range.getStart();
            else
                this.index = range.getEnd();

            this.start = start;
            this.value = value;
        }

        public boolean isStart() {
            return start;
        }

        public boolean isEnd() {
            return !start;
        }

        @Override
        public String toString() {
            return index + (isStart() ? "s" : "e");
        }

        @Override
        public int compareTo(Point<T> other) {
            int d = this.index - other.index;
            if (d != 0) return d;
            return - Boolean.compare(this.isStart(), other.isStart());
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, Boolean.hashCode(start));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Point<?>)) return false;
            Point<?> other = (Point<?>) obj;
            return this.index == other.index && this.start == other.start;
        }
    }
}
