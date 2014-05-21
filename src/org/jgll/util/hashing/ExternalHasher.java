package org.jgll.util.hashing;

import java.io.Serializable;

import org.jgll.util.hashing.hashfunction.HashFunction;

public interface ExternalHasher<T> extends Serializable {
	
	public int hash(T t, HashFunction f);
	
	public boolean equals(T t1, T t2);
}
