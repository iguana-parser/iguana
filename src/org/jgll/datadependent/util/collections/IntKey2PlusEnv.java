package org.jgll.datadependent.util.collections;

import org.jgll.datadependent.env.Environment;
import org.jgll.util.collections.Key;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class IntKey2PlusEnv implements Key {
	
	private final int k1;
	private final int k2;
	private final Environment env;
	private final HashFunction f;

	private IntKey2PlusEnv(int k1, int k2, Environment env, HashFunction f) {
		this.k1 = k1;
		this.k2 = k2;
		this.env = env;
		this.f = f;
	}
	
	public static IntKey2PlusEnv from(int k1, int k2, Environment env, HashFunction f) {
		return new IntKey2PlusEnv(k1, k2, env, f);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof IntKey2PlusEnv)) return false;
		
		IntKey2PlusEnv that = (IntKey2PlusEnv) other;
		return k1 == that.k1 && k2 == that.k2 
				&& env.equals(that.env);
	}
	
	@Override
	public int hashCode() {
		return f.hash(k1, k2, env.hashCode());
	}

	@Override
	public int[] components() {
		return new int[] {k1, k2};
	}

	@Override
	public String toString() {
		return String.format("(%d, %d, env)", k1, k2);
	}

}
