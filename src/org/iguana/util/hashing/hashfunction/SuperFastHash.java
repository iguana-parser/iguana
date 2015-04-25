package org.iguana.util.hashing.hashfunction;

public class SuperFastHash implements HashFunction {
	private final int seed;
	
	public SuperFastHash() {
		seed = 5;
	}
	
	public SuperFastHash(int seed) {
		this.seed = seed;
	}
	
	@Override
	public int hash(int k) {
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
		tmp = ((k1 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
		
		hash += k2 & 0xFFFF;
		tmp = ((k2 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k3 & 0xFFFF;
		tmp = ((k3 & 0xFFFF0000) >> 5) ^ hash;
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
	public int hash(int k1, int k2, int k3, int k4) {
		int hash = seed, tmp;
		
		hash += k1 & 0xFFFF;
		tmp = ((k1 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
		
		hash += k2 & 0xFFFF;
		tmp = ((k2 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k3 & 0xFFFF;
		tmp = ((k3 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k4 & 0xFFFF;
		tmp = ((k4 & 0xFFFF0000) >> 5) ^ hash;
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
		tmp = ((k1 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
		
		hash += k2 & 0xFFFF;
		tmp = ((k2 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k3 & 0xFFFF;
		tmp = ((k3 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k4 & 0xFFFF;
		tmp = ((k4 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k5 & 0xFFFF;
		tmp = ((k5 & 0xFFFF0000) >> 5) ^ hash;
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
	public int hash(int... keys) {
		return 0;
	}

}
