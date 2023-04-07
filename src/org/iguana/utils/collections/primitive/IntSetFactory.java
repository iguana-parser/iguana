package org.iguana.utils.collections.primitive;

public class IntSetFactory {

    public static IntSet create(int... elements) {
        if (elements == null || elements.length == 0)
            throw new IllegalArgumentException("cannot be a null or empty array");

        if (elements.length == 1) {
            return new ImmutableSingleElementIntSet(elements[0]);
        }
        if (elements.length == 2) {
            return new ImmutableTwoElementIntSet(elements[0], elements[1]);
        }

        return OpenAddressingIntHashSet.from(elements);
    }

    public static class ImmutableSingleElementIntSet implements IntSet {

        private final int value;

        public ImmutableSingleElementIntSet(int value) {
            this.value = value;
        }

        @Override
        public boolean contains(int element) {
            return value == element;
        }

        @Override
        public boolean add(int element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IntSet)) return false;
            IntSet other = (IntSet) obj;
            if (other.size() != 1) return false;
            return other.contains(value);
        }

        @Override
        public IntIterator iterator() {
            return new IntIterator() {
                private int count = 0;

                @Override
                public boolean hasNext() {
                    if (count < 1) {
                        count++;
                        return true;
                    }
                    return false;
                }

                @Override
                public int next() {
                    return value;
                }
            };
        }
    }

    public static class ImmutableTwoElementIntSet implements IntSet {

        private final int first;
        private final int second;

        public ImmutableTwoElementIntSet(int first, int second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean contains(int element) {
            return first == element || second == element;
        }

        @Override
        public boolean add(int element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return first + 31 * second;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IntSet)) return false;
            IntSet other = (IntSet) obj;
            if (other.size() != 2) return false;
            return other.contains(first) && other.contains(second);
        }

        @Override
        public IntIterator iterator() {
            return new IntIterator() {
                private int count = 0;

                @Override
                public boolean hasNext() {
                    return count < 2;
                }

                @Override
                public int next() {
                    if (count == 0) {
                        count++;
                        return first;
                    }
                    if (count == 1) {
                        count++;
                        return second;
                    }
                    throw new IllegalStateException("There is no more element");
                }
            };
        }
    }
}
