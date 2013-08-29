package org.jgll.util.hashing.hashfunction;


public class JenkinsCWI implements HashFunction {
	
	private int seed;

	public JenkinsCWI() {
		this.seed = 5;
	}
	public JenkinsCWI(int seed) {
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
		int a,b,c;

		a = b = 0x9e3779b9; 
		c = seed;

		a += k1; b += k2; c += k3;

		a -= b; a -= c; a ^= (c >> 13);
		b -= c; b -= a; b ^= (a << 8);
		c -= a; c -= b; c ^= (b >> 13);
		a -= b; a -= c; a ^= (c >> 12);
		b -= c; b -= a; b ^= (a << 16);
		c -= a; c -= b; c ^= (b >> 5);
		a -= b; a -= c; a ^= (c >> 3);
		b -= c; b -= a; b ^= (a << 10);
		c -= a; c -= b; c ^= (b >> 15);
		
		return mix(a,b,c);
	}
	
	
	private static int mix(int a, int b, int c) {
		a -= b; a -= c; a ^= (c >> 13);
		b -= c; b -= a; b ^= (a << 8);
		c -= a; c -= b; c ^= (b >> 13);
		a -= b; a -= c; a ^= (c >> 12);
		b -= c; b -= a; b ^= (a << 16);
		c -= a; c -= b; c ^= (b >> 5);
		a -= b; a -= c; a ^= (c >> 3);
		b -= c; b -= a; b ^= (a << 10);
		c -= a; c -= b; c ^= (b >> 15);

		return c;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		int a,b,c;

		a = b = 0x9e3779b9; 
		c = seed;

		a += k1; b += k2; c += k3;

		a -= b; a -= c; a ^= (c >> 13);
		b -= c; b -= a; b ^= (a << 8);
		c -= a; c -= b; c ^= (b >> 13);
		a -= b; a -= c; a ^= (c >> 12);
		b -= c; b -= a; b ^= (a << 16);
		c -= a; c -= b; c ^= (b >> 5);
		a -= b; a -= c; a ^= (c >> 3);
		b -= c; b -= a; b ^= (a << 10);
		c -= a; c -= b; c ^= (b >> 15);
		
		a += k4;
		
		return mix(a,b,c);
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		int a,b,c;

		a = b = 0x9e3779b9; 
		c = seed;

		a += k1; b += k2; c += k3;

		a -= b; a -= c; a ^= (c >> 13);
		b -= c; b -= a; b ^= (a << 8);
		c -= a; c -= b; c ^= (b >> 13);
		a -= b; a -= c; a ^= (c >> 12);
		b -= c; b -= a; b ^= (a << 16);
		c -= a; c -= b; c ^= (b >> 5);
		a -= b; a -= c; a ^= (c >> 3);
		b -= c; b -= a; b ^= (a << 10);
		c -= a; c -= b; c ^= (b >> 15);
		
		a += k4;
		b += k5;
		
		return mix(a,b,c);
	}

	@Override
	public int hash(int... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

}
