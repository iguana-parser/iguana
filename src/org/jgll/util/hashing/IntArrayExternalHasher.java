package org.jgll.util.hashing;

import java.util.Arrays;

public class IntArrayExternalHasher implements ExternalHasher<int[]>{

	private static final long serialVersionUID = 1L;
	
	private static ExternalHasher<int[]> instance;
	
	public static ExternalHasher<int[]> getInstance() {
		if(instance == null) {
			instance = new IntArrayExternalHasher();
		}
		
		return instance;
	}
	
	@Override
	public int hash(int[] t, HashFunction f) {
		return f.hash(t);
	}

	@Override
	public boolean equals(int[] t1, int[] t2) {
		return Arrays.equals(t1, t2);
	}
}
