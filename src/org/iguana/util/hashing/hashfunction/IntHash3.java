package org.iguana.util.hashing.hashfunction;

@FunctionalInterface
public interface IntHash3 {
	public int hash(int x, int y, int z);
}
