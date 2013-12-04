package org.jgll.util.hashing;

import org.jgll.parser.HashFunctions;

public class IntegerHashKey4 {

	int k1;
	int k2;
	int k3;
	int k4;

	public IntegerHashKey4(int k1, int k2, int k3, int k4) {
		this.k1 = k1;
		this.k2 = k2;
		this.k3 = k3;
		this.k4 = k4;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof IntegerHashKey4)) {
			return false;
		}
		
		IntegerHashKey4 other = (IntegerHashKey4) obj;
		
		return k1 == other.k1 && k2 == other.k2 && k3 == other.k3 && k4 == other.k4;
	}
	
	
	public int getK1() {
		return k1;
	}
	
	
	public int getK2() {
		return k2;
	}
	
	
	public int getK3() {
		return k3;
	}
	
	
	public int getK4() {
		return k4;
	}
	
	@Override
	public String toString() {
		return "(" + k1 + ", " + k2 + ", " + k3 + ", " + k4 + ")";
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(k1, k2, k3, k4);
	}
	
}
