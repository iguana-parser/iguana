package org.jgll.util.hashing.hashfunction;

import java.io.Serializable;

public interface HashFunction extends Serializable {
	
	public int hash(int k);
	
	public int hash(int k1, int k2);
	
	public int hash(int k1, int k2, int k3);
	
	public int hash(int k1, int k2, int k3, int k4);
	
	public int hash(int k1, int k2, int k3, int k4, int k5);
	
	public int hash(int...keys);
	
}
