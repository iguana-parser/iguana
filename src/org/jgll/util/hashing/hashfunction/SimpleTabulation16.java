package org.jgll.util.hashing.hashfunction;

import org.jgll.util.RandomUtil;


public class SimpleTabulation16 implements HashFunction {
	
	private static final long serialVersionUID = 1L;

	private int[][] table;
	
	/**
	 * Number of target bits
	 */
	private int bitMask;
	
	private static SimpleTabulation16 instance;
	
	public static SimpleTabulation16 getInstance() {
		if(instance == null) {
			instance = new SimpleTabulation16();
		}
		return instance;
	}
	
	private SimpleTabulation16(int d) {
		this.bitMask = (int) Math.pow(2, d) - 1;
		
		table = new int[10][65536];
		
		fillInTable(table);
	}
	
	private SimpleTabulation16() {
		this(25);
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
		return (table[0][k1 & 0xffff]
			  ^ table[1][(k1 >>> 16) & 0xffff]
			  ^ table[2][k2 & 0xffff]
			  ^ table[3][(k2 >>> 16) & 0xffff]);	
	}

	@Override
	public int hash(int k1, int k2, int k3) {
		return (table[0][k1 & 0xffff]
			  ^ table[1][(k1 >>> 16) & 0xffff]
			  ^ table[2][k2 & 0xffff]
			  ^ table[3][(k2 >>> 16) & 0xffff]
			  ^ table[4][k3 & 0xffff]
			  ^ table[5][(k3 >>> 16) & 0xffff]);	
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		return (table[0][k1 & 0xffff]
			  ^ table[1][(k1 >>> 16) & 0xffff]
		      ^ table[2][k2 & 0xffff]
		      ^ table[3][(k2 >>> 16) & 0xffff]
		      ^ table[4][k3 & 0xffff]
		      ^ table[5][(k3 >>> 16) & 0xffff]
		      ^ table[6][k4 & 0xffff]
		      ^ table[7][(k4 >>> 16) & 0xffff]);
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		return (table[0][k1 & 0xffff]
			  ^ table[1][(k1 >>> 16) & 0xffff]
			  ^ table[2][k2 & 0xffff]
			  ^ table[3][(k2 >>> 16) & 0xffff]
			  ^ table[4][k3 & 0xffff]
			  ^ table[5][(k3 >>> 16) & 0xffff]
			  ^ table[6][k4 & 0xffff]
			  ^ table[7][(k4 >>> 16) & 0xffff] 
			  ^ table[8][k5 & 0xffff]
			  ^ table[9][(k5 >>> 16) & 0xffff]);
	}

	@Override
	public int hash(int... keys) {
		return 0;
	}

}
