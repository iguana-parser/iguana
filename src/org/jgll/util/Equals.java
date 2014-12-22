package org.jgll.util;

@FunctionalInterface
public interface Equals<T> {
	public boolean equals(T node, T other);
}