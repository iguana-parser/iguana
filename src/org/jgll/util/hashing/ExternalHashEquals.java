package org.jgll.util.hashing;

import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.hashfunction.HashFunction;

public interface ExternalHashEquals<T> {
	
	default public int hash(T t) {
		return hash(t, HashFunctions.defaulFunction);
	}
	
	public int hash(T t, HashFunction f);
	
	public boolean equals(T t1, T t2);
}
