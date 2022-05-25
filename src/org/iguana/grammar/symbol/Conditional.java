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

import org.iguana.datadependent.ast.Expression;
import org.iguana.traversal.ISymbolVisitor;

import java.util.Collections;
import java.util.List;

public class Conditional extends AbstractSymbol {

	private final Symbol symbol;
	private final Expression expression;

	Conditional(Builder builder) {
		super(builder);
		this.symbol = builder.symbol;
		this.expression = builder.expression;
	}
	
	public static Conditional when(Symbol symbol, Expression expression) {
		return new Builder(symbol, expression).build();
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public Builder copy() {
		return new Builder(this);
	}

	@Override
	public List<Symbol> getChildren() {
		return Collections.singletonList(symbol);
	}

	@Override
	public int size() {
		return symbol.size();
	}
	
	@Override
	public String toString() {
		return String.format("%s when %s", symbol.toString(), expression.toString());
	}
	
	@Override
	public String toString(int j) {
		return String.format(" %s when %s", symbol.toString(j), expression.toString());
	}
	
	public static class Builder extends SymbolBuilder<Conditional> {
		
		private Symbol symbol;
		private Expression expression;

		public Builder(Conditional conditional) {
			super(conditional);
			this.symbol = conditional.symbol;
			this.expression = conditional.expression;
		}
		
		public Builder(Symbol symbol, Expression expression) {
			this.symbol = symbol;
			this.expression = expression;
		}

		@Override
		public SymbolBuilder<Conditional> setChildren(List<Symbol> symbols) {
			this.symbol = symbols.get(0);
			return this;
		}

		@Override
		public Conditional build() {
			this.name = String.format("%s when %s;", symbol.toString(), expression.toString());
			return new Conditional(this);
		}
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
