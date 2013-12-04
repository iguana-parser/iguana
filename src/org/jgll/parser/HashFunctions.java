package org.jgll.parser;

import org.jgll.util.hashing.hashfunction.HashFunction;
import org.jgll.util.hashing.hashfunction.MurmurHash3;
import org.jgll.util.hashing.hashfunction.XXHash;

public class HashFunctions {

	private static HashFunction murmur3 = new MurmurHash3();
	
	private static HashFunction xxhash = new XXHash();
	
	public static HashFunction defaulFunction() {
		return murmur3;
	}
	
	public static HashFunction xxHash() {
		return xxhash;
	}
	
	public static HashFunction murmurHash3() {
		return murmur3;
	}
	
	public static HashFunction murmurHash3(int seed) {
		return new MurmurHash3(seed);
	}
	
	public static HashFunction xxHash(int seed) {
		return new XXHash(seed);
	}
	
}
