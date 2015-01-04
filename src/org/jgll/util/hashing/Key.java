package org.jgll.util.hashing;

import org.jgll.util.hashing.hashfunction.HashFunction;

public interface Key {

	default int hash(HashFunction f) {
		return f.hash(components());
	}
	
	public int[] components();
	
}
