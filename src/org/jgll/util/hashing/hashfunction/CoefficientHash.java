package org.jgll.util.hashing.hashfunction;


public class CoefficientHash implements HashFunction {

	private static final long serialVersionUID = 1L;
	
	private final int coef1;
	private final int coef2;
	private final int coef3;
	private final int coef4;
	
	public CoefficientHash(int coef1) {
		this(coef1, 1);
	}
	
	public CoefficientHash(int coef1, int coef2) {
		this(coef1, coef2, 1);
	}
	
	public CoefficientHash(int coef1, int coef2, int coef3) {
		this(coef1, coef2, coef3, 1);
	}
	
	public CoefficientHash(int coef1, int coef2, int coef3, int coef4) {
		this.coef1 = coef1;
		this.coef2 = coef2;
		this.coef3 = coef3;
		this.coef4 = coef4;
	}
	
	@Override
	public int hash(int k) {
		return k;
	}

	@Override
	public int hash(int k1, int k2) {
		return k1 * coef1 + k2;
	}

	@Override
	public int hash(int k1, int k2, int k3) {
		return hash(k1, k2) * coef2 + k3;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		return hash(k1, k2, k3) * coef3 + k4;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		return hash(k1, k2, k3, k4) * coef4 + k5;
	}

	@Override
	public int hash(int... keys) {
		throw new UnsupportedOperationException();
	}

}
