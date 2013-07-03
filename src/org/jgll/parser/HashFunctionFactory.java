package org.jgll.parser;

import org.jgll.util.hashing.HashFunction;
import org.jgll.util.hashing.MurmurHash3;

public class HashFunctionFactory {

	private static HashFunction murmur3 = new MurmurHash3();
	
	public static HashFunction murmur3() {
		return murmur3;
	}
	
}
