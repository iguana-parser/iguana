package org.iguana.util.collections;

import org.iguana.util.hashing.hashfunction.HashFunction;

public interface Key {

	default int hash(HashFunction f) {
		return f.hash(components());
	}
	
	public int[] components();
	
}
