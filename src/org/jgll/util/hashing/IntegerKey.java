package org.jgll.util.hashing;

public class IntegerKey implements HashKey {
	
	private int k;
	
	public IntegerKey(int k) {
		this.k = k;
	}
	
	public static IntegerKey from(int k) {
		return new IntegerKey(k);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof IntegerKey)) {
			return false;
		}
		
		IntegerKey other = (IntegerKey) obj;
		
		return k == other.k;
	}
	
	public int getInt() {
		return k;
	}

	@Override
	public int hash(HashFunction f) {
		return f.hash(k);
	}
	
	@Override
	public String toString() {
		return k + "";
	}
}
