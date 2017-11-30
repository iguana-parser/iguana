package iguana.utils.collections.primitive;

public interface IntIterable {

    static final IntIterable EMPTY = () -> new IntIterator() {
        @Override public boolean hasNext() { return false; }
        @Override public int next() { return 0; }
    };

    IntIterator iterator();
}
