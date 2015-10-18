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

public class SuperFastHash implements HashFunction {
	private final int seed;
	
	public SuperFastHash() {
		seed = 5;
	}
	
	public SuperFastHash(int seed) {
		this.seed = seed;
	}
	
	@Override
	public int hash(int k) {
		return 0;
	}

	@Override
	public int hash(int k1, int k2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int hash(int k1, int k2, int k3) {
		int hash = seed, tmp;
		
		hash += k1 & 0xFFFF;
		tmp = ((k1 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
		
		hash += k2 & 0xFFFF;
		tmp = ((k2 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k3 & 0xFFFF;
		tmp = ((k3 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
        hash ^= hash << 3;
        hash += hash >> 5;
        hash ^= hash << 4;
        hash += hash >> 17;
        hash ^= hash << 25;
        hash += hash >> 6;
		return hash;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4) {
		int hash = seed, tmp;
		
		hash += k1 & 0xFFFF;
		tmp = ((k1 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
		
		hash += k2 & 0xFFFF;
		tmp = ((k2 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k3 & 0xFFFF;
		tmp = ((k3 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k4 & 0xFFFF;
		tmp = ((k4 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        hash ^= hash << 3;
        hash += hash >> 5;
        hash ^= hash << 4;
        hash += hash >> 17;
        hash ^= hash << 25;
        hash += hash >> 6;
		return hash;
	}

	@Override
	public int hash(int k1, int k2, int k3, int k4, int k5) {
		int hash = seed, tmp;
		
		hash += k1 & 0xFFFF;
		tmp = ((k1 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
		
		hash += k2 & 0xFFFF;
		tmp = ((k2 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k3 & 0xFFFF;
		tmp = ((k3 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k4 & 0xFFFF;
		tmp = ((k4 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        
		hash += k5 & 0xFFFF;
		tmp = ((k5 & 0xFFFF0000) >> 5) ^ hash;
        hash = (hash << 16) ^ tmp;
		hash += hash >> 11;
        
        hash ^= hash << 3;
        hash += hash >> 5;
        hash ^= hash << 4;
        hash += hash >> 17;
        hash ^= hash << 25;
        hash += hash >> 6;
		return hash;
	}

	@Override
	public int hash(int... keys) {
		return 0;
	}

}
