package org.jgll.util.hashing;

import org.jgll.util.RandomUtil;

/**
 * 
 * Multiplication-shift 2-universal hash function.
 * See "Tabulation Based 5-Universal Hashing and Linear Probing" for more info.
 * 
 * @author Ali Afroozeh
 *
 */
public class MultiplicationShift2UniversalHashFunction implements UniversalHashFunction {

	private final static int maxA = Integer.MAX_VALUE >> 1;
	
	private final int a;
	private final int b;
	private final int p;

	public MultiplicationShift2UniversalHashFunction(int p) {
		this.p = p;

		final int maxB = 1 << (32 - p);
		
		a = 2 * RandomUtil.random.nextInt(maxA) + 1;   // random odd number
		b = RandomUtil.random.nextInt(maxB);
	}
	
	@Override
	public int hash(int key) {
		return (a * key + b) >>> (32 - p);
	}
	
}
