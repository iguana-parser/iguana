package org.jgll.util.hashing;

import org.jgll.util.hashing.hashfunction.HashFunction;



public class IntKey4 implements Key, Comparable<IntKey4> {
	
	private final int k1;
	private final int k2;
	private final int k3;
	private final int k4;
	
	private final HashFunction f;

	public IntKey4(int k1, int k2, int k3, int k4, HashFunction f) {
		this.k1 = k1;
		this.k2 = k2;
		this.k3 = k3;
		this.k4 = k4;
		this.f = f;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(this instanceof IntKey4))
			return false;
		
		IntKey4 other = (IntKey4) obj;
		return k1 == other.k1 && 
			   k2 == other.k2 &&
			   k3 == other.k3 &&
			   k4 == other.k4;
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
	public int compareTo(IntKey4 o) {
		return k1 - o.k1 > 0 ? 1 : k2 - o.k2 > 0 ? 1 : k3 - o.k3 > 0 ? 1 : k4 - o.k4;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d, %d, %d)", k1, k2, k3, k4);
	}

}
