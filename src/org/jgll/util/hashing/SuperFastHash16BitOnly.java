package org.jgll.util.hashing;

public class SuperFastHash16BitOnly implements HashFunction {

	private final int seed;
	public SuperFastHash16BitOnly() {
		seed = 5;
	}
	
	public SuperFastHash16BitOnly(int seed) {
		this.seed = seed;
	}

	@Override
	public int hash(int k) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int hash(int k1, int k2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int hash(int k1, int k2, int k3) {
		int hash = seed, tmp;
		
		hash += k1 & 0xFFFF;
		tmp = ((k2 & 0xFFFF) << 11) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
		
		hash += k3;
		hash += hash >> 11;
        
        
        hash ^= hash << 3;
        hash += hash >> 5;
        hash ^= hash << 4;
        hash += hash >> 17;
        hash ^= hash << 25;
        hash += hash >> 6;
		return hash;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		int hash = seed, tmp;
		
		hash += k1 & 0xFFFF;
		tmp = ((k2 & 0xFFFF) << 11) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
		
		hash += k3 & 0xFFFF;
		tmp = ((k4 & 0xFFFF) << 11) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
        hash ^= hash << 3;
        hash += hash >> 5;
        hash ^= hash << 4;
        hash += hash >> 17;
        hash ^= hash << 25;
        hash += hash >> 6;
		return hash;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		int hash = seed, tmp;
		
		hash += k1 & 0xFFFF;
		tmp = ((k2 & 0xFFFF) << 11) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
		
		hash += k3 & 0xFFFF;
		tmp = ((k4 & 0xFFFF) << 11) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
		hash += k5;
		hash += hash >> 11;
        
        
        hash ^= hash << 3;
        hash += hash >> 5;
        hash ^= hash << 4;
        hash += hash >> 17;
        hash ^= hash << 25;
        hash += hash >> 6;
		return hash;
	}

	@Override
	public int hash(int... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

}
