package org.jgll.util.hashing;

import org.jgll.util.RandomUtil;

/**
 * 
 * Multiplication-shift plain universal hash function as described in the paper
 * "Tabulation Based 5-Universal Hashing and Linear Probing".
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class SimpleHashFunction implements UniversalHashFunction {

	private final int n;
	private int size;

	public SimpleHashFunction(int size, int p) {
		this.size = size;

		n = RandomUtil.random.nextInt(32 - p);
	}
	
	@Override
	public int hash(int key) {
		return (key >> n) & (size - 1);
	}
	
}
