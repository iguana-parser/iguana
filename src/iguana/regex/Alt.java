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

public class Alt<T extends RegularExpression> extends AbstractRegularExpression implements Iterable<T> {

	private static final long serialVersionUID = 1L;

	protected final List<T> symbols;
	
	public Alt(Builder<T> builder) {
		super(builder);
		this.symbols = builder.symbols;
	}	
	
	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <T extends RegularExpression> Alt<T> from(T...symbols) {
		return from(Arrays.asList(symbols));
	}
	
	public static <T extends RegularExpression> Alt<T> from(List<T> list) {
		return builder(list).build();
	}
	
	@Override
	public boolean isNullable() {
		return symbols.stream().anyMatch(e -> e.isNullable());
	}
	
	@Override
	public Iterator<T> iterator() {
		return symbols.iterator();
	}
	
	public int size() {
		return symbols.size();
	}
	
	@Override
	public int length() {
		Optional<T> max = symbols.stream().max(RegularExpression.lengthComparator());
		if (max.isPresent()) 
			return max.get().length();
		else
		return 0;
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
	public String toString() {
		return "(" + symbols.stream().map(a -> a.toString()).collect(Collectors.joining(" | ")) + ")";
	}

	@Override
	public Set<CharRange> getFirstSet() {
		return symbols.stream().flatMap(x -> x.getFirstSet().stream()).collect(Collectors.toSet());
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
	
	public static Alt<CharRange> not(Char...chars) {
		List<CharRange> ranges = Arrays.stream(chars).map(c -> CharRange.in(c.getValue(), c.getValue())).collect(Collectors.toList());
		return not(ranges);
	}
	
	public static Alt<CharRange> not(CharRange...ranges) {
		return not(Arrays.asList(ranges));
	}
	
	public static Alt<CharRange> not(Alt<CharRange> alt) {
		return not(alt.symbols);
	}
	
	public static Alt<CharRange> not(List<CharRange> ranges) {
		List<CharRange> newRanges = new ArrayList<>();
		
		int i = 0;
		
		Collections.sort(ranges);
		
		if(ranges.get(i).getStart() >= 1) {
			newRanges.add(CharRange.in(1, ranges.get(i).getStart() - 1));
		}
		
		for (; i < ranges.size() - 1; i++) {
			CharRange r1 = ranges.get(i);
			CharRange r2 = ranges.get(i + i);
			
			if(r2.getStart() > r1.getEnd() + 1) {
				newRanges.add(CharRange.in(r1.getEnd() + 1, r2.getStart() - 1));
			}
		}
		
		if(ranges.get(i).getEnd() < CharacterRanges.MAX_UTF32_VAL) {
			newRanges.add(CharRange.in(ranges.get(i).getEnd() + 1, CharacterRanges.MAX_UTF32_VAL));
		}
		
		return builder(newRanges).build();
	}
	
	public static <T extends RegularExpression> Builder<T> builder(T t1, T t2) {
		return builder(Arrays.asList(t1, t2));
	}
	
	public static <T extends RegularExpression> Builder<T> builder(List<T> symbols) {
		return new Builder<T>(symbols);
	}
	
	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <T extends RegularExpression> Builder<T> builder(T...symbols) {
		return new Builder<>(Arrays.asList(symbols));
	}
	
	public static class Builder<T extends RegularExpression> extends RegexBuilder<Alt<T>> {

		private final List<T> symbols = new ArrayList<>();

		private Builder() {}
		
		public Builder(List<T> symbols) {
			this.addAll(symbols);
		}

		public Builder(Alt<T> alt) {
			super(alt);
			this.addAll(alt.getSymbols());
		}

		public Builder<T> add(T symbol) {
			symbols.add(symbol);
			return this;
		}

		public Builder<T> addAll(List<T> symbols) {
			this.symbols.addAll(symbols);
			return this;
		}

		@Override
		public Alt<T> build() {
			return new Alt<>(this);
		}

    }

	@Override
	public <E> E accept(RegularExpressionVisitor<E> visitor) {
		return visitor.visit(this);
	}
	
}
