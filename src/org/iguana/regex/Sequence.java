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

package org.iguana.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import iguana.utils.input.Input;
import org.iguana.traversal.RegularExpressionVisitor;
import org.iguana.util.generator.GeneratorUtil;

public class Sequence<T extends RegularExpression> extends AbstractRegularExpression implements Iterable<T> {

	private static final long serialVersionUID = 1L;

	private final List<T> symbols;
	
	public static Sequence<Character> from(String s) {
		return from(Input.toIntArray(s));
	}
	
	public static Sequence<Character> from(int[] chars) {
		return builder(Arrays.stream(chars).mapToObj(Character::from).collect(Collectors.toList())).build();
	}
	
	public static <T extends RegularExpression> Sequence<T> from(List<T> symbols) {
		return builder(symbols).build();
	}
	
	@SafeVarargs
	public static <T extends RegularExpression> Sequence<T> from(T...elements) {
		return from(Arrays.asList(elements));
	}
	
	private Sequence(Builder<T> builder) {
		super(builder);
		this.symbols = builder.symbols;
	}
	
	private static <T extends RegularExpression> String getName(List<T> elements) {
		return "(" + elements.stream().map(a -> a.getName()).collect(Collectors.joining(" ")) + ")";
	}
	
	@Override
	public int length() {
		return symbols.stream().mapToInt(s -> ((RegularExpression)s).length()).sum();
	}
	
	@Override
	public boolean isNullable() {
		return symbols.stream().allMatch(e -> ((RegularExpression)e).isNullable()); 
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
		
		if(!(obj instanceof Sequence))
			return false;
		
		Sequence<?> other = (Sequence<?>) obj;
		
		return symbols.equals(other.symbols);
	}
	
	@Override
	public int hashCode() {
		return symbols.hashCode();
	}

	@Override
	public Iterator<T> iterator() {
		return symbols.iterator();
	}
	
	@Override
	public Set<CharacterRange> getFirstSet() {
		Set<CharacterRange> firstSet = new HashSet<>();
		for(RegularExpression regex : symbols) {
			firstSet.addAll(regex.getFirstSet());
			if(!regex.isNullable()) {
				break;
			}
		}
		return firstSet;
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	
	@Override
	public Builder<T> copyBuilder() {
		return new Builder<T>(this);
	}
	
	public List<T> getSymbols() {
		return symbols;
	}
	
	@Override
	public String toString() {
		return "(" + GeneratorUtil.listToString(symbols, " ") + ")";
	}
	
	public static <T extends RegularExpression> Builder<T> builder(T s) {
		return builder(Arrays.asList(s));
	}
	
	public static <T extends RegularExpression> Builder<T> builder(List<T> symbols) {
		return new Builder<T>(symbols);
	}
	
	@SafeVarargs
	public static <T extends RegularExpression> Builder<T> builder(T...symbols) {
		return builder(Arrays.asList(symbols));
	}
	
	public static class Builder<T extends RegularExpression> extends RegexBuilder<Sequence<T>> {

		private List<T> symbols = new ArrayList<>();
		
		public Builder(List<T> symbols) {
			super(getName(symbols));
			this.symbols = symbols;
		}
		
		public Builder(Sequence<T> seq) {
			super(seq);
			this.symbols = seq.symbols;
		}
		
		public Builder<T> add(T s) {
			symbols.add(s);
			return this;
		}
		
		public Builder<T> add(List<T> symbols) {
			this.symbols.addAll(symbols);
			return this;
		}
		
		@Override
		public Sequence<T> build() {
			return new Sequence<>(this);
		}
	}

	@Override
	public <E> E accept(RegularExpressionVisitor<E> visitor) {
		return visitor.visit(this);
	}
	
}
