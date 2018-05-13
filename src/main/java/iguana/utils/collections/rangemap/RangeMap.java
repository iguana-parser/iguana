package iguana.utils.collections.rangemap;

import java.util.*;

public class RangeMap<T> {

    private static Object[] empty = new Object[0];

    private final int[] points;
    private final boolean[] starts;
    private final T[][] values;

    public RangeMap(int[] points, boolean[] starts, T[][] values) {
        this.points = points;
        this.starts = starts;
        this.values = values;
    }

    public T[] get(int key) {
        if (key < points[0] || key > points[points.length - 1])
            return (T[]) empty;

        int index = Arrays.binarySearch(points, key);

        if (index >= 0 && !starts[index]) {
            return values[index - 1];
        }

        if (index < 0) {
            index = -index - 1 - 1;
        }

        return values[index];
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private List<Range> ranges = new ArrayList<>();
        private List<T> values = new ArrayList<>();

        public Builder<T> put(Range range, T value) {
            ranges.add(range);
            values.add(value);
            return this;
        }

        public RangeMap build() {
            if (ranges.isEmpty()) {
                throw new RuntimeException("Cannot build an empty range map");
            }

            int size = ranges.size() * 2;
            List<Point<T>> points = new ArrayList<>(size);

            for (int i = 0; i < ranges.size(); i++) {
                Range range = ranges.get(i);
                points.add(new Point<>(range, true, values.get(i)));
                points.add(new Point<>(range, false, values.get(i)));
            }

            Collections.sort(points);

            Map<Point<?>, Set<T>> cumulativeValues = new LinkedHashMap<>(size);

            for (int i = 0; i < size; i++) {
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

            int[] keys = new int[size];
            boolean[] starts = new boolean[size];
            T[][] vals = (T[][]) new Object[size][];

            for (int i = 0; i < size; i++) {
                Point<T> point = points.get(i);
                keys[i] = point.index;
                if (point.isStart()) {
                    starts[i] = true;
                }

                if (cumulativeValues.size() == 0) {
                    vals[i] = (T[]) empty;
                } else {
                    vals[i] = (T[]) cumulativeValues.get(point).toArray();
                }
            }

            return new RangeMap<>(keys, starts, vals);
        }
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
