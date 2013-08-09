package org.jgll.util.hashing;

public class DavyHash implements HashFunction {

	private int seed;

	private final int m = 0x5bd1e995;
	private final int r = 24;

	
	public DavyHash() {
		this(0);
	}
	
	public DavyHash(int seed) {
		this.seed = seed;
	}
	
	private int mixK(int k) {
		k *= m;
		k ^= k >>> r;
		k *= m;
		return k;
	}
	
	private int mixH(int h, int k) {
		h *= m;
		h ^= k;
		return h;
	}
	
	@Override
	public int hash(int k) {
		return 0;
	}

	@Override
	public int hash(int k1, int k2) {
		return 0;
	}

	
	@Override
	public int hash(int k1, int k2, int k3) {
		int h = seed;

		int k = k1;
		k *= m;
		k *= k2 << 10;
		k ^= k >>> 16;
		k *= k3 << 10;
		k ^= k >>> 8;
		h *= k;
		
		// last mix
		h *= m;
		h ^= h >>> 13;
		return h;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		int h = seed ^ 4;

		int k = (k1 & 0xFFFF) | ((k2 & 0xFFFF) << 16);
		k *= h;
		k ^= k >>> 16;
		h *= k;
		
		k = (k3 & 0xFFFF) | ((k4 & 0xFFFF) << 16);
		k *= h;
		k ^= k >>> 16;
		h *= k;
		
		// last mix
		h *= m;
		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;
		
		return h;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		int h = seed ^ 4;

		int k = (k1 & 0xFFFF) | ((k2 & 0xFFFF) << 16);
		k *= h;
		k ^= k >>> 16;
		h *= k;
		
		k = (k3 & 0x3FF) | ((k4 & 0x3FF) << 10) | ((k5 & 0x3FF) << 20);
		k *= h;
		k ^= k >>> 16;
		h *= k;
		
		// last mix
		h *= m;
		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;
		
		return h;
	}

	@Override
	public int hash(int... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

}
