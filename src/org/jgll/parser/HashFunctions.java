package org.jgll.parser;

import org.jgll.util.hashing.hashfunction.HashFunction;
import org.jgll.util.hashing.hashfunction.MurmurHash3;

public class HashFunctions {

	private static HashFunction murmur3 = new MurmurHash3();
	
	public static HashFunction defaulFunction() {
		return murmur3;
	}
	
}
