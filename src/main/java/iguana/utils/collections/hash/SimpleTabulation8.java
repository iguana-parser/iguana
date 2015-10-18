/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package iguana.utils.collections.hash;


import java.util.Random;

public class SimpleTabulation8 implements HashFunction {
	
	private static final long serialVersionUID = 1L;

    static final Random random = new Random();

	private int[][] table;
	
	/**
	 * Number of target bits
	 */
	private int bitMask;
	
	private static SimpleTabulation8 instance;
	
	public static SimpleTabulation8 getInstance() {
		if(instance == null) {
			instance = new SimpleTabulation8();
		}
		return instance;
	}
	
	private SimpleTabulation8(int d) {
		this.bitMask = (int) Math.pow(2, d) - 1;
		
		table = new int[20][256];
		
		fillInTable(table);
	}
	
	private SimpleTabulation8() {
		this(25);
	}
	
	private void fillInTable(int[][] table) {
		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table[i].length; j++) {
				int nextInt = random.nextInt(bitMask);
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
		      ^ table[7][(k2 >>> 24) & 0xff]);	
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
		      ^ table[11][(k3 >>> 24) & 0xff]);	
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
		      ^ table[15][(k4 >>> 24) & 0xff]);
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
		      ^ table[19][(k5 >>> 24) & 0xff]);
	}

	@Override
	public int hash(int... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

}
