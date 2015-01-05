package org.jgll.util.collections;

import org.jgll.util.hashing.hashfunction.HashFunction;


public class IntKey2 implements Key, Comparable<IntKey2> {
	
	private final int k1;
	private final int k2;
	private final HashFunction f;

	private IntKey2(int k1, int k2, HashFunction f) {
		this.k1 = k1;
		this.k2 = k2;
		this.f = f;
	}
	
	public static IntKey2 from(int k1, int k2, HashFunction f) {
		return new IntKey2(k1, k2, f);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(this instanceof IntKey2))
			return false;
		
		IntKey2 other = (IntKey2) obj;
		return k1 == other.k1 && k2 == other.k2;
	}
	
	@Override
	public int hashCode() {
		return f.hash(k1, k2);
	}

	@Override
	public int[] components() {
		return new int[] {k1, k2};
	}

	@Override
	public int compareTo(IntKey2 o) {
		return k1 - o.k2 > 0 ? 1 : k2 - o.k2;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", k1, k2);
	}

}
