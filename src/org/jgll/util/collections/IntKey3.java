package org.jgll.util.collections;

import org.jgll.util.hashing.hashfunction.HashFunction;



public class IntKey3 implements Key, Comparable<IntKey3> {
	
	private final int k1;
	private final int k2;
	private final int k3;
	private final HashFunction f;

	private IntKey3(int k1, int k2, int k3, HashFunction f) {
		this.k1 = k1;
		this.k2 = k2;
		this.k3 = k3;
		this.f = f;
	}
	
	public static IntKey3 from(int k1, int k2, int k3, HashFunction f) {
		return new IntKey3(k1, k2, k3, f);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(this instanceof IntKey3))
			return false;
		
		IntKey3 other = (IntKey3) obj;
		return k1 == other.k1 && k2 == other.k2 && k3 == other.k3;
	}
	
	@Override
	public int hashCode() {
		return f.hash(k1, k2, k3);
	}

	@Override
	public int[] components() {
		return new int[] {k1, k2, k3};
	}

	@Override
	public int compareTo(IntKey3 o) {
		return k1 - o.k1 > 0 ? 1 : k2 - o.k2 > 0 ? 1 : k3 - o.k3;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d, %d)", k1, k2, k3);
	}

}
