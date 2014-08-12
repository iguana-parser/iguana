package org.jgll.util.hashing.hashfunction;

public class PrimeMultiplication implements HashFunction {

	private static final long serialVersionUID = 1L;

	@Override
	public int hash(int k) {
		return k;
	}

	@Override
	public int hash(int k1, int k2) {
		int result = 17;
		result = 31 * result + k1;
		result = 31 * result + k2;
		return result;
	}

	@Override
	public int hash(int k1, int k2, int k3) {
		int result = 17;
		result = 31 * result + k1;
		result = 31 * result + k2;
		result = 31 * result + k3;
		return result;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		int result = 17;
		result = 31 * result + k1;
		result = 31 * result + k2;
		result = 31 * result + k3;
		result = 31 * result + k4;
		return result;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		int result = 17;
		result = 31 * result + k1;
		result = 31 * result + k2;
		result = 31 * result + k3;
		result = 31 * result + k4;
		result = 31 * result + k5;
		return result;
	}

	@Override
	public int hash(int... keys) {
		int result = 17;
		for (int key : keys) {
			result = 31 * result + key;
		}
		return result;
	}

}
