package org.jgll.util;

import java.util.BitSet;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class BitSetUtil {
	
	public static final BitSet EMPTY_BITSET = new BitSet();
	
	public static BitSet from(int...values) {
		BitSet bitSet = new BitSet();
		for(int val : values) {
			bitSet.set(val);
		}
		return bitSet;
	}
}
