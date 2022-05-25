package org.iguana.utils.collections.rangemap;

public interface Range extends Comparable<Range> {

    int getStart();

    int getEnd();

    static Range in(int start, int end) {
        return new Range() {

            @Override
            public int getStart() {
                return start;
            }

            @Override
            public int getEnd() {
                return end;
            }

            @Override
            public String toString() {
                return "[" + start + ", " + end + "]";
            }
        };
    }

    @Override
    default int compareTo(Range o) {
        return getStart() == o.getStart() ? getEnd() - o.getEnd() : getStart() - o.getStart();
    }

    default boolean contains(int c) {
        return getStart() <= c && c <= getEnd();
    }

    default boolean contains(Range other) {
        return getStart() <= other.getStart() && getEnd() >= other.getEnd();
    }

    default boolean overlaps(Range other) {
        return !(getEnd() < other.getStart() || other.getEnd() < getStart());
    }
}
