package org.jgll.util.hashing;

import org.jgll.util.hashing.hashfunction.HashFunction;

public class IntegerExternalHasher implements ExternalHasher<Integer> {

	private static final long serialVersionUID = 1L;
	
	private static IntegerExternalHasher instance;
	
	public static IntegerExternalHasher getInstance() {
		if(instance == null) {
			instance = new IntegerExternalHasher();
		}
		return instance;
	}
	
	private IntegerExternalHasher() {}

	@Override
	public int hash(Integer t, HashFunction f) {
		return f.hash(t);
	}

	@Override
	public boolean equals(Integer t1, Integer t2) {
		return t1.intValue() == t2.intValue();
	}

}
