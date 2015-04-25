package org.iguana.util.hashing.hashfunction;

@FunctionalInterface
public interface IntHash4 {
	public int hash(int x, int y, int z, int w);
}
