package org.iguana.util.collections;

@FunctionalInterface
public interface IntKeyMapper<T> {
	public T apply(int a, T t);
}
