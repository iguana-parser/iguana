package iguana.utils.collections.rangemap;

public class ImmutableRange implements Range {

    private final int start;
    private final int end;

    public static Range in(int start, int end) {
        return new ImmutableRange(start, end);
    }

    public ImmutableRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }
}
