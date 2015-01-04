package org.jgll.parser;

import org.jgll.util.hashing.hashfunction.CoefficientHash;
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

	public static HashFunction coefficientHash(int coef1) {
		return new CoefficientHash(coef1);
	}
	
	public static HashFunction coefficientHash(int coef1, int coef2) {
		return new CoefficientHash(coef1, coef2);
	}
	
	public static HashFunction coefficientHash(int coef1, int coef2, int coef3) {
		return new CoefficientHash(coef1, coef2, coef3);
	}	

	public static HashFunction coefficientHash(int coef1, int coef2, int coef3, int coef4) {
		return new CoefficientHash(coef1, coef2, coef3, coef4);
	}
}
