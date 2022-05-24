package org.iguana.utils.collections.rangemap;

import java.util.*;

import static java.util.Collections.emptyList;

public class RangeMapBuilder<T> {

    private List<Range> ranges = new ArrayList<>();
    private List<T> values = new ArrayList<>();

    private static RangeMap<?> emptyRangeMap = (RangeMap<Object>) key -> emptyList();
    private static IntRangeMap emptyIntRangeMap = key -> -2;

    public RangeMapBuilder<T> put(Range range, T value) {
        ranges.add(range);
        values.add(value);
        return this;
    }

    public IntRangeMap buildIntRangeMap() {
        if (ranges.isEmpty()) {
            return emptyIntRangeMap;
        }

        if (ranges.size() == 1) {
            Range range = ranges.get(0);
            return new SingleIntRangeMap(range.getStart(), range.getEnd(), (Integer) values.get(0));
        }

        List<Point<T>> points = getPoints(ranges, values);
        if (points.get(points.size() - 1).index - points.get(0).index < 256) {
            return new ArrayIntRangeMap<>(points.get(0).index, points.get(points.size() - 1).index, ranges, values);
        }

        Result<T> result = getResult(points);

        if (!result.canBuildIntRangeMap()) {
            throw new RuntimeException("There are more than one values available for a range");
        }

        int[] intVals = new int[result.size];
        Arrays.fill(intVals, IntRangeMap.EMPTY_VALUE);
        for (int j = 0; j < result.vals.length; j++) {
            List<T> l = result.vals[j];
            if (l.size() == 1) {
                intVals[j] = (Integer) l.get(0);
            }
        }

        if (intVals.length < 8) {
            return new LinearSerachIntRangeMap(result.keys, result.starts, intVals);
        }

        return new BinarySearchIntRangeMap(result.keys, result.starts, intVals);
    }

    public RangeMap<T> buildRangeMap() {
        if (ranges.isEmpty()) {
            return (RangeMap<T>) emptyRangeMap;
        }

        if (ranges.size() == 1) {
            Range range = ranges.get(0);
            return new SingeRangeMap<>(range.getStart(), range.getEnd(), values.get(0));
        }

        List<Point<T>> points = getPoints(ranges, values);

        Result<T> result = getResult(points);

        if (result.size < 8) {
            return new LinearSerachRangeMap<>(result.keys, result.starts, result.vals);
        }

        return new BinarySearchRangeMap<>(result.keys, result.starts, result.vals);
    }

    private List<Point<T>> getPoints(List<Range> ranges, List<T> values) {
        List<Point<T>> points = new ArrayList<>(ranges.size() * 2);

        for (int i = 0; i < ranges.size(); i++) {
            Range range = ranges.get(i);
            points.add(new Point<>(range, true, values.get(i)));
            points.add(new Point<>(range, false, values.get(i)));
        }

        Collections.sort(points);
        return points;
    }

    private Result<T> getResult(List<Point<T>> points) {
        // A map from each point to a multiset of values (value, count)
        Map<Point<T>, Map<T, Integer>> cumulativeValues = new LinkedHashMap<>(ranges.size() * 2);

        for (int i = 0; i < ranges.size() * 2; i++) {
            Point<T> point = points.get(i);
            Map<T, Integer> valuesSet = cumulativeValues.computeIfAbsent(point, k -> new LinkedHashMap<>());

            if (i > 0) {
                valuesSet.putAll(cumulativeValues.get(points.get(i - 1)));
            }

            if (point.isStart()) {
                valuesSet.compute(point.value, (k, v) -> v == null ? 1 : v + 1);
            } else {
                valuesSet.compute(point.value, (k, v) -> v == null ? null : v - 1 == 0 ? null : v - 1);
            }
        }

        int size = cumulativeValues.size();
        int[] keys = new int[size];
        boolean[] starts = new boolean[size];
        List<T>[] vals = new List[size];

        int i = 0;
        for (Map.Entry<Point<T>, Map<T, Integer>> entry : cumulativeValues.entrySet()) {
            Point<T> point = entry.getKey();
            keys[i] = point.index;
            if (point.isStart()) {
                starts[i] = true;
            }

            Set<T> value = entry.getValue().keySet();
            if (value.size() == 0) {
                vals[i] = emptyList();
            } else {
                ArrayList<T> list = new ArrayList<>(cumulativeValues.get(point).keySet());
                list.trimToSize();
                vals[i] = list;
            }
            i++;
        }

        return new Result<>(size, keys, starts, vals);
    }

    private static class Result<T> {
        int size;
        int[] keys;
        boolean[] starts;
        List<T>[] vals;

        Result(int size, int[] keys, boolean[] starts, List<T>[] vals) {
            this.size = size;
            this.keys = keys;
            this.starts = starts;
            this.vals = vals;
        }

        /**
         * Returns true if the values are of type integer and there is at most one value for each key
         */
        boolean canBuildIntRangeMap() {
            boolean canBuildRangeMap = true;
            for (List<T> list : vals) {
                if (list.isEmpty()) continue;
                if (!(list.get(0) instanceof Integer) || list.size() > 1) {
                    canBuildRangeMap = false;
                    break;
                }
            }
            return canBuildRangeMap;
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
