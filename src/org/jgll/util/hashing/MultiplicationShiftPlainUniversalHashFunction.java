package org.jgll.util.hashing;

import java.util.Random;

/**
 * 
 * Multiplication-shift plain universal hash function based on the paper
 * See "Tabulation Based 5-Universal Hashing and Linear Probing" for more info.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class MultiplicationShiftPlainUniversalHashFunction implements UniversalHashFunction {

	private final static int maxA = Integer.MAX_VALUE >> 1;
	
	private final int a;
	private final int p;

	public MultiplicationShiftPlainUniversalHashFunction(int p) {
		this.p = p;

		Random rand = new Random();
		a = 2 * rand.nextInt(maxA) + 1;   // random odd number
	}
	
	@Override
	public int hash(int key) {
		return (a * key) >>> (32 - p);
	}
	
}
