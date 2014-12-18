package org.jgll.util;

@FunctionalInterface
public interface Hash<T> {
  public int hash(T node);
}
