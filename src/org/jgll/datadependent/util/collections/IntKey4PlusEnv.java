package org.jgll.datadependent.util.collections;

import org.jgll.datadependent.env.Environment;
import org.jgll.util.collections.Key;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class IntKey4PlusEnv implements Key {
	
	private final int k1;
	private final int k2;
	private final int k3;
	private final int k4;
	private final Environment env;	
	private final HashFunction f;

	private IntKey4PlusEnv(int k1, int k2, int k3, int k4, Environment env, HashFunction f) {
		this.k1 = k1;
		this.k2 = k2;
		this.k3 = k3;
		this.k4 = k4;
		this.env = env;
		this.f = f;
	}
	
	public static IntKey4PlusEnv from(int k1, int k2, int k3, int k4, Environment env, HashFunction f) {
		return new IntKey4PlusEnv(k1, k2, k3, k4, env, f);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof IntKey4PlusEnv)) return false;
		
		IntKey4PlusEnv that = (IntKey4PlusEnv) other;
		return k1 == that.k1 && k2 == that.k2 
				&& k3 == that.k3 && k4 == that.k4
				&& env.equals(that.env);
	}
	
	@Override
	public int hashCode() {
		return f.hash(k1, k2, k3, k4);
	}

	@Override
	public int[] components() {
		return new int[] {k1, k2, k3, k4};
	}

	@Override
	public String toString() {
		return String.format("(%d, %d, %d, %d, env)", k1, k2, k3, k4);
	}

}
