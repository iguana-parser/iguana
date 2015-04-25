package org.jgll.datadependent.util.collections;

import org.jgll.util.collections.Key;
import org.jgll.util.hashing.hashfunction.IntHash3;

public class IntKey2PlusObject implements Key {
	
	private final int k1;
	private final int k2;
	private final Object obj;
	
	private final int hash;

	private IntKey2PlusObject(Object obj, int k1, int k2, IntHash3 f) {
		this.k1 = k1;
		this.k2 = k2;
		this.obj = obj;
		this.hash = f.hash(obj.hashCode(), k1, k2);
	}
	
	public static IntKey2PlusObject from(Object obj, int k1, int k2, IntHash3 f) {
		return new IntKey2PlusObject(obj, k1, k2, f);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof IntKey2PlusObject)) return false;
		
		IntKey2PlusObject that = (IntKey2PlusObject) other;
		return hash == that.hash 
				&& k1 == that.k1 && k2 == that.k2 
				&& obj.equals(that.obj);
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
	public String toString() {
		return String.format("(%d, %d, %s)", k1, k2, obj);
	}

}
