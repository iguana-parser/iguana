package org.iguana.util.hashing.hashfunction;

@FunctionalInterface
public interface IntHash2 {
	
	public int hash(int x, int y);
	
	default int hash(int x, int y, Object obj) {
		return hash(x, y);
	}
}
