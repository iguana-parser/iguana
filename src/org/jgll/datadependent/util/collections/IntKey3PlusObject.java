package org.jgll.datadependent.util.collections;

import org.jgll.util.collections.Key;
import org.jgll.util.hashing.hashfunction.IntHash4;

public class IntKey3PlusObject implements Key {
	
	private final int k1;
	private final int k2;
	private final int k3;
	private final Object obj;
	
	private final int hash;

	private IntKey3PlusObject(Object obj, int k1, int k2, int k3, IntHash4 f) {
		this.k1 = k1;
		this.k2 = k2;
		this.k3 = k3;
		this.obj = obj;
		this.hash = f.hash(obj.hashCode(), k1, k2, k3);
	}
	
	public static IntKey3PlusObject from(Object obj, int k1, int k2, int k3, IntHash4 f) {
		return new IntKey3PlusObject(obj, k1, k2, k3, f);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof IntKey3PlusObject)) return false;
		
		IntKey3PlusObject that = (IntKey3PlusObject) other;
		return hash == that.hash && 
			   k1 == that.k1 && 
			   k2 == that.k2 &&
			   k3 == that.k3 && 
			   obj.equals(that.obj);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public int[] components() {
		return new int[] {k1, k2, k3};
	}

	@Override
	public String toString() {
		return String.format("(%d, %d, %s)", k1, k2, k3, obj);
	}

}
