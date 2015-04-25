package org.iguana.parser;

import org.iguana.util.hashing.hashfunction.HashFunction;
import org.iguana.util.hashing.hashfunction.MurmurHash3;
import org.iguana.util.hashing.hashfunction.PrimeMultiplication;
import org.iguana.util.hashing.hashfunction.XXHash;

public class HashFunctions {

	public static HashFunction murmur3 = new MurmurHash3();
	
	public static HashFunction xxhash = new XXHash();
	
	public static HashFunction primeMultiplication = new PrimeMultiplication();
	
	public static HashFunction defaulFunction =  primeMultiplication;
	
	public static HashFunction murmur3(int seed) {
		return new MurmurHash3(seed);
	}
}
