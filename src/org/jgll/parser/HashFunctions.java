package org.jgll.parser;

import org.jgll.util.hashing.hashfunction.HashFunction;
import org.jgll.util.hashing.hashfunction.MurmurHash3;
import org.jgll.util.hashing.hashfunction.PrimeMultiplication;
import org.jgll.util.hashing.hashfunction.XXHash;

public class HashFunctions {

	public static HashFunction murmur3 = new MurmurHash3();
	
	public static HashFunction xxhash = new XXHash();
	
	public static HashFunction primeMultiplication = new PrimeMultiplication();
	
	public static HashFunction defaulFunction =  primeMultiplication;
	
	public static HashFunction murmur3(int seed) {
		return new MurmurHash3(seed);
	}
	
}
