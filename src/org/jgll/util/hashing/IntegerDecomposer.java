package org.jgll.util.hashing;

public class IntegerDecomposer implements Decomposer<Integer> {

	private static IntegerDecomposer instance;
	
	public static IntegerDecomposer getInstance() {
		if(instance == null) {
			instance = new IntegerDecomposer();
		}
		return instance;
	}
	
	private IntegerDecomposer() {}
	
	private int[] components = new int[1];
	
	@Override
	public int[] toIntArray(Integer t) {
		components[0] = t;
		return components;
	}

}
