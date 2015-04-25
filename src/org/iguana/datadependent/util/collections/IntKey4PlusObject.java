package org.iguana.datadependent.util.collections;

import org.iguana.util.collections.Key;
import org.iguana.util.hashing.hashfunction.IntHash5;

public class IntKey4PlusObject implements Key {
	
	private final int k1;
	private final int k2;
	private final int k3;
	private final int k4;
	private final Object obj;	
	
	private final int hash;

	private IntKey4PlusObject(Object obj, int k1, int k2, int k3, int k4, IntHash5 f) {
		this.k1 = k1;
		this.k2 = k2;
		this.k3 = k3;
		this.k4 = k4;
		this.obj = obj;
		this.hash = f.hash(obj.hashCode(), k1, k2, k3, k4);
	}
	
	public static IntKey4PlusObject from(Object obj, int k1, int k2, int k3, int k4, IntHash5 f) {
		return new IntKey4PlusObject(obj, k1, k2, k3, k4, f);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof IntKey4PlusObject)) return false;
		
		IntKey4PlusObject that = (IntKey4PlusObject) other;
		return hash == that.hash 
				&& k1 == that.k1 && k2 == that.k2 
				&& k3 == that.k3 && k4 == that.k4
				&& obj.equals(that.obj);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public int[] components() {
		return new int[] {k1, k2, k3, k4};
	}

	@Override
	public String toString() {
		return String.format("(%d, %d, %d, %d, %s)", k1, k2, k3, k4, obj);
	}

}
