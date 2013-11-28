package org.jgll.util.hashing.hashfunction;

import org.jgll.util.RandomUtil;


public class SimpleTabulation implements HashFunction {
	
	private static final long serialVersionUID = 1L;

	
	private int[][] table;
	
	/**
	 * Number of target bits
	 */
	private int bitMask;
	
	public SimpleTabulation(int d) {
		this.bitMask = (int) Math.pow(2, d) - 1;
		
		table = new int[20][256];
		
		fillInTable(table);
	}
	
	private void fillInTable(int[][] table) {
		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table[i].length; j++) {
				int nextInt = RandomUtil.random.nextInt(bitMask);
				table[i][j] = nextInt;
			}
		}
	}
	
	@Override
	public int hash(int k) {
		return k & bitMask;
	}

	@Override
	public int hash(int k1, int k2) {
		return (table[0][k1 & 0xff]
     		  ^ table[1][(k1 >>> 8)  & 0xff]
			  ^ table[2][(k1 >>> 16) & 0xff]
			  ^ table[3][(k1 >>> 24) & 0xff]
		      ^ table[4][k2 & 0xff]
		      ^ table[5][(k2 >>> 8)  & 0xff]
		      ^ table[6][(k2 >>> 16) & 0xff]
		      ^ table[7][(k2 >>> 24) & 0xff]) & bitMask;	
	}

	@Override
	public int hash(int k1, int k2, int k3) {
		return (table[0][k1 & 0xff]
     		  ^ table[1][(k1 >>> 8)  & 0xff]
			  ^ table[2][(k1 >>> 16) & 0xff]
			  ^ table[3][(k1 >>> 24) & 0xff]
		      ^ table[4][k2 & 0xff]
		      ^ table[5][(k2 >>> 8)  & 0xff]
		      ^ table[6][(k2 >>> 16) & 0xff]
		      ^ table[7][(k2 >>> 24) & 0xff]
		      ^ table[8][k3 & 0xff]
		      ^ table[9][(k3 >>> 8)  & 0xff]
		      ^ table[10][(k3 >>> 16) & 0xff]
		      ^ table[11][(k3 >>> 24) & 0xff]) & bitMask;	
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		return (table[0][k1 & 0xff]
     		  ^ table[1][(k1 >>> 8)  & 0xff]
			  ^ table[2][(k1 >>> 16) & 0xff]
			  ^ table[3][(k1 >>> 24) & 0xff]
		      ^ table[4][k2 & 0xff]
		      ^ table[5][(k2 >>> 8)  & 0xff]
		      ^ table[6][(k2 >>> 16) & 0xff]
		      ^ table[7][(k2 >>> 24) & 0xff]
		      ^ table[8][k3 & 0xff]
		      ^ table[9][(k3 >>> 8)  & 0xff]
		      ^ table[10][(k3 >>> 16) & 0xff]
		      ^ table[11][(k3 >>> 24) & 0xff]	 
		      ^ table[12][k4 & 0xff]
		      ^ table[13][(k4 >>> 8)  & 0xff]
		      ^ table[14][(k4 >>> 16) & 0xff]
		      ^ table[15][(k4 >>> 24) & 0xff]) & bitMask;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		return (table[0][k1 & 0xff]
     		  ^ table[1][(k1 >>> 8)  & 0xff]
			  ^ table[2][(k1 >>> 16) & 0xff]
			  ^ table[3][(k1 >>> 24) & 0xff]
		      ^ table[4][k2 & 0xff]
		      ^ table[5][(k2 >>> 8)  & 0xff]
		      ^ table[6][(k2 >>> 16) & 0xff]
		      ^ table[7][(k2 >>> 24) & 0xff]
		      ^ table[8][k3 & 0xff]
		      ^ table[9][(k3 >>> 8)  & 0xff]
		      ^ table[10][(k3 >>> 16) & 0xff]
		      ^ table[11][(k3 >>> 24) & 0xff]	 
		      ^ table[12][k4 & 0xff]
		      ^ table[13][(k4 >>> 8)  & 0xff]
		      ^ table[14][(k4 >>> 16) & 0xff]
		      ^ table[15][(k4 >>> 24) & 0xff]
		      ^ table[16][k5 & 0xff]
		      ^ table[17][(k5 >>> 8) & 0xff]
		      ^ table[18][(k5 >>> 16) & 0xff]
		      ^ table[19][(k5 >>> 24) & 0xff]) & bitMask;
	}

	@Override
	public int hash(int... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

}
