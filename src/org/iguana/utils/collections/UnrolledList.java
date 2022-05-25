package org.iguana.utils.collections;

import java.util.*;

public class UnrolledList<T> implements List<T> {

    private final int segmentSize;

    private final List<T[]> segments;

    int currentSegmentIndex;

    /**
     * The index in a segment at which new elements should be inserted
     */
    int indexInSegment;

    int size;

    public UnrolledList(int segmentSize) {
        this.segments = new ArrayList<>(4);
        this.segmentSize = segmentSize;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        if (indexInSegment < segmentSize) {
            T[] currentSegment = currentSegmentIndex < segments.size() ? segments.get(this.currentSegmentIndex) : null;
            if (currentSegment == null) {
                currentSegment = (T[]) new Object[segmentSize];
                segments.add(currentSegment);
            }
            currentSegment[indexInSegment] = t;
            size++;
            indexInSegment++;
        } else {
            indexInSegment = 0;
            currentSegmentIndex++;
            add(t);
        }

        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        segments.clear();
        size = 0;
    }

    @Override
    public T get(int index) {
        if (index >= size) throw new IndexOutOfBoundsException();

        int i = index / segmentSize;
        int j = index % segmentSize;
        return segments.get(i)[j];
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}
