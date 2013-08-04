package org.jgll.util.hashing;

public class IntegerDecomposer implements ExternalHasher<Integer> {

	private static final long serialVersionUID = 1L;
	
	private static IntegerDecomposer instance;
	
	public static IntegerDecomposer getInstance() {
		if(instance == null) {
			instance = new IntegerDecomposer();
		}
		return instance;
	}
	
	private IntegerDecomposer() {}

	@Override
	public int hash(Integer t, HashFunction f) {
		return f.hash(t);
	}

}
