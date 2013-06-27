package org.jgll.util;

import java.util.HashMap;
import java.util.Map;

public class SparseBitSet {

	private Map<Long, Long> wordsMap;
	
	public SparseBitSet() {
		wordsMap = new HashMap<>();
	}
	
	public void set(long index) {
		long wordIndex = index >> 6;
		Long l = wordsMap.get(wordIndex);
		if(l == null) {
			l = 0L;
		}
		int bitIndex = (int) index & 0x3f;  // mod 64, 0x3f = 63
		long bitmask = 1L << bitIndex;         
		l |= bitmask;
		wordsMap.put(wordIndex, l);
	}
	
	public boolean get(long index) {
		long wordIndex = index >> 6;
		
		Long value = wordsMap.get(wordIndex);
		if(value == null) {
			return false;
		}
		
		int bitIndex = (int) index & 0x3f;
		long bitmask = 1L << bitIndex;  
		return (value & bitmask) != 0;
	}
	
}
