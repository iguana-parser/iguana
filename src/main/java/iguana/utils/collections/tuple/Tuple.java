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

package iguana.utils.collections.tuple;

import java.io.Serializable;


public class Tuple<T, K> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected T t;
	protected K k;

	public Tuple(T t, K k) {
		this.t = t;
		this.k = k;
	}
	
	public T getFirst() {
		return t;
	}
	
	public K getSecond() {
		return k;
	}
	
	public static <T, K> Tuple<T, K> of(T t, K k) {
		return new Tuple<>(t, k);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Tuple)) {
			return false;
		}
		
		@SuppressWarnings("unchecked")
		Tuple<T, K> other = (Tuple<T, K>) obj;
		
		return t == null ? other.t == null : t.equals(other.t) &&
			   k == null ? other.k == null : k.equals(other.k);
	}
	
	@Override
	public int hashCode() {
		return (t == null ? 0 : t.hashCode()) + 31 * (k == null ? 0 : k.hashCode());
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", t, k);
	}
}
