package iguana.utils.collections.rangemap;

public class Range implements Comparable<Range> {

    private final int start;
    private final int end;

    public static Range in(int start, int end) {
        return new Range(start, end);
    }

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public int compareTo(Range o) {
        return start == o.start ? end - o.end : start - o.start;
    }

    boolean contains(int c) {
        return start <= c && c <= end;
    }

    boolean contains(Range other) {
        return start <= other.start && end >= other.end;
    }

    boolean overlaps(Range other) {
        return !(end < other.start || other.end < start);
    }
}
