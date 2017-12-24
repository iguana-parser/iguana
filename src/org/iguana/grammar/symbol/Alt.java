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

package org.iguana.grammar.symbol;

import org.iguana.traversal.ISymbolVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Alt<T extends Symbol> extends AbstractSymbol implements Iterable<T> {

	private static final long serialVersionUID = 1L;

	protected final List<T> symbols;
	
	public Alt(Builder<T> builder) {
		super(builder);
		this.symbols = builder.symbols;
	}

	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <T extends Symbol> Alt<T> from(T...symbols) {
		return from(Arrays.asList(symbols));
	}

	public static <T extends Symbol> Alt<T> from(List<T> list) {
		return builder(list).build();
	}
	
	private static <T extends Symbol> String getName(List<T> elements) {
		return "(" + elements.stream().map(a -> a.getName()).collect(Collectors.joining(" | ")) + ")";
	}

	@Override
	public Iterator<T> iterator() {
		return symbols.iterator();
	}
	
	public int size() {
		return symbols.size();
	}
	
	public T get(int index) {
		return symbols.get(index);
	}
	
	@Override
	public boolean equals(Object obj) {
	
		if(obj == this)
			return true;
		
		if(!(obj instanceof Alt))
			return false;
		
		Alt<?> other = (Alt<?>) obj;
		
		return other.symbols.equals(symbols);
	}
	
	@Override
	public int hashCode() {
		return symbols.hashCode();
	}

	@Override
	public Builder<T> copyBuilder() {
		return new Builder<T>(this);
	}
	
	public List<T> getSymbols() {
		return symbols;
	}

	public static <T extends Symbol> Builder<T> builder(T t1, T t2) {
		return builder(Arrays.asList(t1, t2));
	}
	
	public static <T extends Symbol> Builder<T> builder(List<T> symbols) {
		return new Builder<T>(symbols);
	}
	
	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <T extends Symbol> Builder<T> builder(T...symbols) {
		return new Builder<T>(Arrays.asList(symbols));
	}
	
	public static class Builder<T extends Symbol> extends SymbolBuilder<Alt<T>> {

		List<T> symbols = new ArrayList<>();
		
		public Builder(List<T> symbols) {
			super(getName(symbols));
			this.symbols = symbols;
		}
		
		public Builder(Alt<T> alt) {
			super(alt);
			this.symbols = alt.getSymbols();
		}

		public Builder<T> add(T symbol) {
			symbols.add(symbol);
			return this;
		}
				
		public Builder<T> add(List<T> l) {
			symbols.addAll(l);
			return this;
		}

		@Override
		public Alt<T> build() {
			return new Alt<>(this);
		}
	}

	@Override
	public <E> E accept(ISymbolVisitor<E> visitor) {
		return visitor.visit(this);
	}
	
}
