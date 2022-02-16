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

import iguana.regex.visitor.RegularExpressionVisitor;

import java.util.*;

public class Plus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regex;
	
	private final List<RegularExpression> separators;
	
	public static Plus from(RegularExpression s) {
		return builder(s).build();
	}
	
	private Plus(Builder builder) {
		super(builder);
		this.regex = builder.regex;
		this.separators = Collections.unmodifiableList(builder.separators);
	}
	
	@Override
	public int length() {
		return regex.length();
	}
	
	@Override
	public boolean isNullable() {
		return regex.isNullable();
	}
	
	@Override
	public Set<CharRange> getFirstSet() {
		return regex.getFirstSet();
	}
	
	@Override
	public Set<CharRange> getNotFollowSet() {
		return regex.getFirstSet();
	}
	
	public List<RegularExpression> getSeparators() {
		return separators;
	}

	@Override
	public RegexBuilder<Plus> copyBuilder() {
		return new Builder(this);
	}

	public RegularExpression getSymbol() {
		return regex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof Plus))
			return false;
		
		Plus other = (Plus) obj;
		return regex.equals(other.regex) && separators.equals(other.separators);
	}
	
	@Override
	public int hashCode() {
		return regex.hashCode();
	}

	@Override
	public String toString() {
		return regex.toString() + "*";
	}

	public static Builder builder(RegularExpression s) {
		return new Builder(s);
	}

	public static class Builder extends RegexBuilder<Plus> {

		private RegularExpression regex;

		private final List<RegularExpression> separators = new ArrayList<>();

		private Builder() {}

		public Builder(RegularExpression regex) {
			this.regex = regex;
		}
		
		public Builder(Plus plus) {
			super(plus);
			this.regex = plus.regex;
            this.addSeparators(plus.getSeparators());
		}

        public Builder addSeparator(RegularExpression symbol) {
			separators.add(symbol);
			return this;
		}
		
		public Builder addSeparators(List<RegularExpression> symbols) {
			separators.addAll(symbols);
			return this;
		}
		
		public Builder addSeparators(RegularExpression...symbols) {
			separators.addAll(Arrays.asList(symbols));
			return this;
		}
		
		@Override
		public Plus build() {
			return new Plus(this);
		}
	}

	@Override
	public <T> T accept(RegularExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}
