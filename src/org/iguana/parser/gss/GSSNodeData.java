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

package org.iguana.parser.gss;

import java.util.Iterator;

import org.iguana.util.generator.GeneratorUtil;

public class GSSNodeData<T> implements Iterable<T> {
	
	private final T[] elements;
	
	public final int size;
	
	public GSSNodeData(T[] elements) {
		this.elements = elements;
		this.size = elements == null? 0 : elements.length;
	}
	
	public T[] getValues() {
		return elements;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof GSSNodeData)) {
			return false;
		}
		
		GSSNodeData<?> that = (GSSNodeData<?>) other;
		
		if (this.size != that.size) return false;
		
		Iterator<T> iter1 = iterator();
		Iterator<?> iter2 = that.iterator();
		
		while (iter1.hasNext()) {
			if (!iter1.next().equals(iter2.next())) {
				return false;
			};
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		
		for (T element : elements) {
			result = 31 * result + element.hashCode();
		}
		
		return result;
		
	}

	@Override
	public Iterator<T> iterator() {
		return new GSSNodeDataIterator<T>(this);
	}
	
	@Override
	public String toString() {
		return GeneratorUtil.listToString(elements, ",");
	}
	
	static private class GSSNodeDataIterator<T> implements Iterator<T> {
		
		private GSSNodeData<T> data;
		
		private int i = 0;
		
		GSSNodeDataIterator(GSSNodeData<T> data) {
			this.data = data;
		}

		@Override
		public boolean hasNext() {
			return i < data.size;
		}

		@Override
		public T next() {
			return data.elements[i++];
		}
		
	}

}
