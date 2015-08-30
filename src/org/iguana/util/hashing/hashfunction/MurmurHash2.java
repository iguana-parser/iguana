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