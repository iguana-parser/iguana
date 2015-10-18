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
