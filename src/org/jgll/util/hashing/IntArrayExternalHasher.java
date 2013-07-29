package org.jgll.util.hashing;

public class IntArrayExternalHasher implements ExternalHasher<int[]>{

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
}
