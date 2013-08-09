package org.jgll.util.hashing;

public class Jenkins implements HashFunction {

	private int seed;

	public Jenkins() {
		seed = 5;
	}
	
	public Jenkins(int seed) {
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

		a = b = c = 0xdeadbeef + seed;

		a += k1; b += k2; c += k3; 
		a -= c;
		a ^= Integer.rotateLeft(c, 4);
		c += b;
		b -= a;
		b ^= Integer.rotateLeft(a, 6);
		a += c;
		c -= b;
		c ^= Integer.rotateLeft(b, 8);
		b += a;
		a -= c;
		a ^= Integer.rotateLeft(c, 16);
		c += b;
		b -= a;
		b ^= Integer.rotateLeft(a, 19);
		a += c;
		c -= b;
		c ^= Integer.rotateLeft(b, 4);
		b += a;


		c ^= b;
		c -= Integer.rotateLeft(b, 14);
		a ^= c;
		a -= Integer.rotateLeft(c, 11);
		b ^= a;
		b -= Integer.rotateLeft(a, 25);
		c ^= b;
		c -= Integer.rotateLeft(b, 16);
		a ^= c;
		a -= Integer.rotateLeft(c, 4);
		b ^= a;
		b -= Integer.rotateLeft(a, 14);
		c ^= b;
		c -= Integer.rotateLeft(b, 24);

		return c;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		int a,b,c;

		a = b = c = 0xdeadbeef + seed;

		a += k1; b += k2; c += k3; 
		a -= c;
		a ^= Integer.rotateLeft(c, 4);
		c += b;
		b -= a;
		b ^= Integer.rotateLeft(a, 6);
		a += c;
		c -= b;
		c ^= Integer.rotateLeft(b, 8);
		b += a;
		a -= c;
		a ^= Integer.rotateLeft(c, 16);
		c += b;
		b -= a;
		b ^= Integer.rotateLeft(a, 19);
		a += c;
		c -= b;
		c ^= Integer.rotateLeft(b, 4);
		b += a;
		
		a += k4;


		c ^= b;
		c -= Integer.rotateLeft(b, 14);
		a ^= c;
		a -= Integer.rotateLeft(c, 11);
		b ^= a;
		b -= Integer.rotateLeft(a, 25);
		c ^= b;
		c -= Integer.rotateLeft(b, 16);
		a ^= c;
		a -= Integer.rotateLeft(c, 4);
		b ^= a;
		b -= Integer.rotateLeft(a, 14);
		c ^= b;
		c -= Integer.rotateLeft(b, 24);

		return c;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		int a,b,c;

		a = b = c = 0xdeadbeef + seed;

		a += k1; b += k2; c += k3; 
		a -= c;
		a ^= Integer.rotateLeft(c, 4);
		c += b;
		b -= a;
		b ^= Integer.rotateLeft(a, 6);
		a += c;
		c -= b;
		c ^= Integer.rotateLeft(b, 8);
		b += a;
		a -= c;
		a ^= Integer.rotateLeft(c, 16);
		c += b;
		b -= a;
		b ^= Integer.rotateLeft(a, 19);
		a += c;
		c -= b;
		c ^= Integer.rotateLeft(b, 4);
		b += a;
		
		a += k4; b += k5;


		c ^= b;
		c -= Integer.rotateLeft(b, 14);
		a ^= c;
		a -= Integer.rotateLeft(c, 11);
		b ^= a;
		b -= Integer.rotateLeft(a, 25);
		c ^= b;
		c -= Integer.rotateLeft(b, 16);
		a ^= c;
		a -= Integer.rotateLeft(c, 4);
		b ^= a;
		b -= Integer.rotateLeft(a, 14);
		c ^= b;
		c -= Integer.rotateLeft(b, 24);

		return c;
	}

	@Override
	public int hash(int... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

}
