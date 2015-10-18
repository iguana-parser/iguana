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

public class XXHash implements HashFunction {
	
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private static final int PRIME1 = (int) 2654435761L;
	private static final int PRIME2 = (int) 2246822519L;
	private static final int PRIME3 = (int) 3266489917L;
	private static final int PRIME4 = 668265263;
	private static final int PRIME5 = 0x165667b1;
	
	private int seed;
	
	public XXHash() {
		this(0);
	}
	
	public XXHash(int seed) {
		this.seed = seed + PRIME5;
	}

	@Override
	public int hash(int k) {
		int h = seed;
		
		h += k * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h ^= h >>> 15;
		h *= PRIME2;
		h ^= h >>> 13;
		h *= PRIME3;
		h ^= h >>> 16;

		return h;
	}

	@Override
	public int hash(int k1, int k2) {
		int h = seed;
		
		h += k1 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k2 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h ^= h >>> 15;
		h *= PRIME2;
		h ^= h >>> 13;
		h *= PRIME3;
		h ^= h >>> 16;

		return h;
	}
	

	@Override
	public int hash(int k1, int k2, int k3) {
		int h = seed;
		
		h += k1 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k2 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k3 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;

		h ^= h >>> 15;
		h *= PRIME2;
		h ^= h >>> 13;
		h *= PRIME3;
		h ^= h >>> 16;

		return h;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		int h = seed;
		
		h += k1 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k2 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k3 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k4 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;

		h ^= h >>> 15;
		h *= PRIME2;
		h ^= h >>> 13;
		h *= PRIME3;
		h ^= h >>> 16;

		return h;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		int h = seed;
		
		h += k1 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k2 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k3 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k4 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;
		
		h += k5 * PRIME3;
		h = Integer.rotateLeft(h, 17) * PRIME4;


		h ^= h >>> 15;
		h *= PRIME2;
		h ^= h >>> 13;
		h *= PRIME3;
		h ^= h >>> 16;

		return h;
	}

	@Override
	public int hash(int... keys) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
