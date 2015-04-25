package org.iguana.util.collections;

import org.iguana.util.hashing.hashfunction.IntHash2;


public class IntKey2 implements Key, Comparable<IntKey2> {
	
	private final int k1;
	private final int k2;
	private final int hash;

	private IntKey2(int k1, int k2, IntHash2 f) {
		this.k1 = k1;
		this.k2 = k2;
		this.hash = f.hash(k1, k2);
	}
	
	public static IntKey2 from(int k1, int k2, IntHash2 f) {
		return new IntKey2(k1, k2, f);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof IntKey2))
			return false;
		
		IntKey2 other = (IntKey2) obj;
		return k1 == other.k1 && k2 == other.k2;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public int[] components() {
		return new int[] {k1, k2};
	}

	@Override
	public int compareTo(IntKey2 o) {
		int r;
		return (r = k1 - o.k1) != 0 ? r : k2 - o.k2;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", k1, k2);
	}

}
