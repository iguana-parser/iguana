package org.jgll.util.hashing;

public class StringDecomposer implements Decomposer<String> {
	
	private static Decomposer<String> instance;
	
	public static Decomposer<String> getInstance() {
		if(instance == null) {
			instance = new StringDecomposer();
		}
		return instance;
	}

	@Override
	public int[] toIntArray(String s) {
		int[] array = new int[s.length() / 2 + 1];
		
		for (int i = 1; i < s.length(); i += 2) {
			array[i - 1] = s.charAt(i - 1) | (s.charAt(i) << 16);
		}
		
		return array;
	}
 
}
