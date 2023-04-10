package org.iguana.utils.collections.primitive;

/**
 * A specialized set interface for non-negative int values. Operations on this set work with primitive values and
 * therefore avoid (un)boxing.
 */
public interface IntSet extends IntIterable {

    /**
     * Returns true if this set contains the given element.
     */
    boolean contains(int element);

    /**
     * Adds the given element to the set. Only accepts non-negative integer values.
     * @return true if the set does not contain the given element
     * @throws IllegalArgumentException when the given element is negative.
     */
    boolean add(int element);

    /**
     * Returns the number of elements in this set
     */
    int size();

    /**
     * Clears the set by removing all the elements
     */
    void clear();

    default boolean isEmpty() {
        return size() == 0;
    }
}
