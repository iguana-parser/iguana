package org.iguana.util.hashing.hashfunction;

public class MurmurHash2 implements HashFunction {

	private static final long serialVersionUID = 1L;
	
	private final int m = 0x5bd1e995;
	private final int r = 24;

	private int seed;
	
	public MurmurHash2() {
		this(0);
	}
	
	public MurmurHash2(int seed) {
		this.seed = seed;
	}
	
	@Override
	public int hash(int a, int b, int c, int d) {
		
		int h = seed ^ 4;

		// a
		int k = a;
		k = mixK(k);
		h = mixH(h, k);
		
		// b
		k = b;
		k = mixK(k);
		h = mixH(h, k);
		
		// c
		k = c;
		k = mixK(k);
		h = mixH(h, k);
		
		// d
		k = d;
		k = mixK(k);
		h = mixH(h, k);

		// last mix
		h *= m;
		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;
		
		return h;
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
	public int hash(int a, int b, int c) {
		return hash(0, a, b, c);
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
	public int hash(int... keys) {
		return 0;
	}

	@Override
	public int hash(int a, int b, int c, int d, int e) {
		int h = seed ^ 4;

		// a
		int k = a;
		k = mixK(k);
		h = mixH(h, k);
		
		// b
		k = b;
		k = mixK(k);
		h = mixH(h, k);
		
		// c
		k = c;
		k = mixK(k);
		h = mixH(h, k);
		
		// d
		k = d;
		k = mixK(k);
		h = mixH(h, k);
		
		// e
		k = e;
		k = mixK(k);
		h = mixH(h, k);

		// last mix
		h *= m;
		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;
		
		return h;
	}

}
