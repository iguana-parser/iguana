package org.jgll.util.hashing;

import java.util.Random;

/**
 * 
 * Multiplication-shift plain universal hash function.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class TabulationUniversalHashFunction implements UniversalHashFunction {

	private final static int maxA = Integer.MAX_VALUE >> 1;
	
	private final int a;
	private final int p;

	public TabulationUniversalHashFunction(int p) {
		this.p = p;

		Random rand = new Random();
		a = 2 * rand.nextInt(maxA) + 1;   // random odd number
	}
	
	@Override
	public int hash(int key) {
		return (a * key) >>> (32 - p);
	}
	
}
