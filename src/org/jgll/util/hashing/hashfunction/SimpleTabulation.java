package org.jgll.util.hashing.hashfunction;

import org.jgll.util.RandomUtil;


public class SimpleTabulation implements HashFunction {
	
	private static final long serialVersionUID = 1L;

	private int[][] table4;
	
	/**
	 * Number of target bits
	 */
	private int bitMask;
	
	public SimpleTabulation(int d) {
		this.bitMask = (int) Math.pow(2, d) - 1;
		
		table4 = new int[16][256];
		
		for(int i = 0; i < table4.length; i++) {
			for(int j = 0; j < table4[i].length; j++) {
				int nextInt = RandomUtil.random.nextInt(64);
				table4[i][j] = nextInt;
			}
		}
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		return (table4[0][k1 & 0xff]
     		  ^ table4[1][(k1 >>> 8)  & 0xff]
			  ^ table4[2][(k1 >>> 16) & 0xff]
			  ^ table4[3][(k1 >>> 24) & 0xff]
		      ^ table4[4][k2 & 0xff]
		      ^ table4[5][(k2 >>> 8)  & 0xff]
		      ^ table4[6][(k2 >>> 16) & 0xff]
		      ^ table4[7][(k2 >>> 24) & 0xff]
		      ^ table4[8][k3 & 0xff]
		      ^ table4[9][(k3 >>> 8)  & 0xff]
		      ^ table4[10][(k3 >>> 16) & 0xff]
		      ^ table4[11][(k3 >>> 24) & 0xff]	 
		      ^ table4[12][k4 & 0xff]
		      ^ table4[13][(k4 >>> 8)  & 0xff]
		      ^ table4[14][(k4 >>> 16) & 0xff]
		      ^ table4[15][(k4 >>> 24) & 0xff]) & bitMask;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int hash(int... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

}
