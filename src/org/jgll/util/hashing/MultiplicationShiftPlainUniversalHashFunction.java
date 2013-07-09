package org.jgll.util.hashing;

import java.util.Random;

/**
 * 
 * Multiplication-shift plain universal hash function as described in the paper
 * "Tabulation Based 5-Universal Hashing and Linear Probing".
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class MultiplicationShiftPlainUniversalHashFunction implements UniversalHashFunction {

	private final static int maxA = Integer.MAX_VALUE >> 1;
	private final static Random rand = new Random();
	
	private final int a;
	private final int p;

	public MultiplicationShiftPlainUniversalHashFunction(int p) {
		this.p = p;

		a = 2 * rand.nextInt(maxA) + 1;   // random odd number
	}
	
	@Override
	public int hash(int key) {
		return (a * key) >>> (32 - p);
	}
	
}
