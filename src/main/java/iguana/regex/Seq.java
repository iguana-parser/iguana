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

package iguana.regex;

import java.util.*;
import java.util.stream.Collectors;

public class Seq<T extends RegularExpression> extends AbstractRegularExpression implements Iterable<T> {

	private static final long serialVersionUID = 1L;

	private final List<T> symbols;

	public static Seq<Char> from(String s) {
		return builder(s.chars().mapToObj(Char::from).collect(Collectors.toList())).build();
	}

	public static Seq<Char> from(int[] chars) {
		return builder(Arrays.stream(chars).mapToObj(Char::from).collect(Collectors.toList())).build();
	}
	
	public static <T extends RegularExpression> Seq<T> from(List<T> symbols) {
		return builder(symbols).build();
	}
	
	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <T extends RegularExpression> Seq<T> from(T...elements) {
		return from(Arrays.asList(elements));
	}

	private Seq(Builder<T> builder) {
		super(builder);
		this.symbols = builder.symbols;
	}
	
	@Override
	public int length() {
		return symbols.stream().mapToInt(s -> s.length()).sum();
	}
	
	@Override
	public boolean isNullable() {
		return symbols.stream().allMatch(e -> e.isNullable());
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
		
		if(!(obj instanceof Seq))
			return false;
		
		Seq<?> other = (Seq<?>) obj;
		
		return symbols.equals(other.symbols);
	}

	@Override
	public int hashCode() {
		return symbols.hashCode();
	}

    @Override
    public String toString() {
        if (symbols.stream().allMatch(c -> c instanceof Char))
            return symbols.stream().map(a -> a.toString()).collect(Collectors.joining(""));
        return "(" + symbols.stream().map(a -> a.toString()).collect(Collectors.joining(" ")) + ")";
    }

    @Override
	public Iterator<T> iterator() {
		return symbols.iterator();
	}
	
	@Override
	public Set<CharRange> getFirstSet() {
		Set<CharRange> firstSet = new HashSet<>();
		for(RegularExpression regex : symbols) {
			firstSet.addAll(regex.getFirstSet());
			if(!regex.isNullable()) {
				break;
			}
		}
		return firstSet;
	}
	
	@Override
	public Set<CharRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	
	@Override
	public Builder<T> copyBuilder() {
		return new Builder<T>(this);
	}
	
	public List<T> getSymbols() {
		return symbols;
	}
	
	public static <T extends RegularExpression> Builder<T> builder(T s) {
		return builder(Arrays.asList(s));
	}
	
	public static <T extends RegularExpression> Builder<T> builder(List<T> symbols) {
		return new Builder<T>().setSymbols(symbols);
	}
	
	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <T extends RegularExpression> Builder<T> builder(T...symbols) {
		return builder(Arrays.asList(symbols));
	}
	
	public static class Builder<T extends RegularExpression> extends RegexBuilder<Seq<T>> {

		private List<T> symbols = new ArrayList<>();

		public Builder(Seq<T> seq) {
			super(seq);
			this.symbols = new ArrayList<>(seq.symbols);
		}

		public Builder() {}
		
		public Builder<T> add(T s) {
			symbols.add(s);
			return this;
		}
		
		public Builder<T> addAll(List<T> symbols) {
			this.symbols.addAll(symbols);
			return this;
		}

		public Builder<T> setSymbols(List<T> symbols) {
			this.symbols = new ArrayList<>(symbols);
			return this;
		}
		
		@Override
		public Seq<T> build() {
			return new Seq<>(this);
		}

	}

	@Override
	public <E> E accept(RegularExpressionVisitor<E> visitor) {
		return visitor.visit(this);
	}
	
}
