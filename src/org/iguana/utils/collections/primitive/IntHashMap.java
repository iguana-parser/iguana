package org.iguana.utils.collections.primitive;


import org.iguana.utils.collections.IntKeyMapper;

import java.util.function.IntFunction;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface IntHashMap<T> extends Iterable<IntKeyEntry<T>> {

    boolean containsKey(int key);

    T computeIfAbsent(int key, IntFunction<T> f);

    /**
     * @return null if there is already a value associated with a key
     */
    T compute(int key, IntKeyMapper<T> mapper);

    T put(int key, T value);

    T remove(int key);

    T get(int key);

    int size();

    boolean isEmpty();

    void clear();

    Iterable<T> values();

}

