package org.jgll.util.hashing;

import org.jgll.util.hashing.hashfunction.HashFunction;


public class IntegerHashKey4ExternalHasher implements ExternalHasher<IntegerHashKey4> {

	private static final long serialVersionUID = 1L;

	@Override
	public int hash(IntegerHashKey4 key, HashFunction f) {
		return f.hash(key.k1, key.k2, key.k3, key.k4);
	}

	@Override
	public boolean equals(IntegerHashKey4 key1, IntegerHashKey4 key2) {
		return key1.k1 == key2.k1 &&
			   key1.k2 == key2.k2 &&
			   key1.k3 == key2.k3 &&
			   key1.k4 == key1.k4;
	}
}

