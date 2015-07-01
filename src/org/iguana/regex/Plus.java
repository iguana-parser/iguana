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
import java.util.List;
import java.util.Set;

import org.iguana.grammar.symbol.AbstractRegularExpression;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.SymbolBuilder;
import org.iguana.regex.automaton.Automaton;
import org.iguana.traversal.ISymbolVisitor;

public class Plus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	private final List<Symbol> separators;
	
	private final boolean allRegularExpression;
	
	public static Plus from(Symbol s) {
		return builder(s).build();
	}
	
	private Plus(Builder builder) {
		super(builder);
		this.s = builder.s;
		this.separators = Collections.unmodifiableList(builder.separators);
		this.allRegularExpression = s instanceof RegularExpression;
	}
	
	private static String getName(Symbol s) {
		return s.getName() + "+";
	}
	
	@Override
	public int length() {
		if (allRegularExpression) {
			return ((RegularExpression) s).length();
		}
		return 0;
	}
	
	@Override
	protected Automaton createAutomaton() {
		
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");

		return Sequence.from(s, Star.from(s)).getAutomaton();		
	}
	
	@Override
	public boolean isNullable() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");

		return ((RegularExpression) s).isNullable();
	}
	
	@Override
	public Set<CharacterRange> getFirstSet() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");

		return ((RegularExpression) s).getFirstSet();
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		if (!allRegularExpression)
			throw new RuntimeException("Only applicable to regular expressions");

		return ((RegularExpression) s).getFirstSet();
	}
	
	public List<Symbol> getSeparators() {
		return separators;
	}
	
	public boolean isAllRegularExpression() {
		return allRegularExpression;
	}
	
	@Override
	public String getConstructorCode() {
		return Plus.class.getSimpleName() + 
			   ".builder(" + s.getConstructorCode() + ")" + 
			   super.getConstructorCode() + 
			   (separators.isEmpty() ? "" : ".addSeparators(" + asList(separators) + ")") +
			   ".build()";
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return builder(s);
	}

	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof Plus))
			return false;
		
		Plus other = (Plus) obj;
		return s.equals(other.s) && separators.equals(other.separators);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public static Builder builder(Symbol s) {
		return new Builder(s);
	}

	public static class Builder extends SymbolBuilder<Plus> {

		private Symbol s;
		
		private final List<Symbol> separators = new ArrayList<>();

		public Builder(Symbol s) {
			super(getName(s));
			this.s = s;
		}
		
		public Builder(Plus plus) {
			super(plus);
			this.s = plus.s;
		}
		
		public Builder addSeparator(Symbol symbol) {
			separators.add(symbol);
			return this;
		}
		
		public Builder addSeparators(List<Symbol> symbols) {
			separators.addAll(symbols);
			return this;
		}
		
		public Builder addSeparators(Symbol...symbols) {
			separators.addAll(Arrays.asList(symbols));
			return this;
		}
		
		@Override
		public Plus build() {
			return new Plus(this);
		}
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}
