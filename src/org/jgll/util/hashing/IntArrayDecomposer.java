package org.jgll.util.hashing;

public class IntArrayDecomposer implements Decomposer<int[]>{

	private static Decomposer<int[]> instance;
	
	public static Decomposer<int[]> getInstance() {
		if(instance == null) {
			instance = new IntArrayDecomposer();
		}
		
		return instance;
	}
	
	
	@Override
	public int[] toIntArray(int[] t) {
		return t;
	}

}
