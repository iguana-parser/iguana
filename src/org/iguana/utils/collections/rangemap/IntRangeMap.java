package org.iguana.utils.collections.rangemap;

@FunctionalInterface
public interface IntRangeMap {

    int EMPTY_VALUE = -2;

    int get(int key);
}
