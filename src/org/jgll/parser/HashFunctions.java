package org.jgll.parser;

import org.jgll.util.hashing.HashFunction;
import org.jgll.util.hashing.MurmurHash3;

public class HashFunctions {

	private static HashFunction murmur3 = new MurmurHash3();
	
	public static HashFunction defaulFunction() {
		return murmur3;
	}
	
}
