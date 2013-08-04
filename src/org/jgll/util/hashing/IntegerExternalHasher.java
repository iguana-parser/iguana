package org.jgll.util.hashing;

public class IntegerExternalHasher implements ExternalHasher<Integer> {

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

}
