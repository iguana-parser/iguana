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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class IfThenElse extends AbstractSymbol {

	private final Expression expression;
	private final Symbol thenPart;
	private final Symbol elsePart;

	IfThenElse(Builder builder) {
		super(builder);
		this.expression = builder.expression;
		this.thenPart = builder.thenPart;
		this.elsePart = builder.elsePart;
	}
	
	public static IfThenElse ifThenElse(Expression expression, Symbol thenPart, Symbol elsePart) {
		return new Builder(expression, thenPart, elsePart).build();
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public Symbol getThenPart() {
		return thenPart;
	}
	
	public Symbol getElsePart() {
		return elsePart;
	}
	
	@Override
	public Builder copy() {
		return new Builder(this);
	}

	@Override
	public List<Symbol> getChildren() {
		return Arrays.asList(thenPart, elsePart);
	}

	@Override
	public String toString() {
		return String.format("if (%s) %s else %s", expression.toString(), thenPart.toString(), elsePart.toString());
	}
	
	@Override
	public int size() {
		return thenPart.size() + elsePart.size();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IfThenElse)) return false;
		IfThenElse that = (IfThenElse) o;
		return Objects.equals(expression, that.expression) &&
			Objects.equals(thenPart, that.thenPart) &&
			Objects.equals(elsePart, that.elsePart);
	}

	@Override
	public int hashCode() {
		return Objects.hash(expression, thenPart, elsePart);
	}

	@Override
	public String toString(int j) {
		return String.format("if (%s) { %s } else { %s }", 
								expression.toString(), 
								thenPart.toString(j), 
								j - thenPart.size() <= 1? elsePart.toString(j - thenPart.size())
											           : elsePart.toString());
	}
	
	public static class Builder extends SymbolBuilder<IfThenElse> {
		
		private Expression expression;
		private Symbol thenPart;
		private Symbol elsePart;

		public Builder() { }

		public Builder(IfThenElse ifThenElse) {
			super(ifThenElse);
			this.expression = ifThenElse.expression;
			this.thenPart = ifThenElse.thenPart;
			this.elsePart = ifThenElse.elsePart;
		}
		
		public Builder(Expression expression, Symbol thenPart, Symbol elsePart) {
			this.expression = expression;
			this.thenPart = thenPart;
			this.elsePart = elsePart;
		}

		@Override
		public SymbolBuilder<IfThenElse> setChildren(List<Symbol> symbols) {
			this.thenPart = symbols.get(0);
			this.elsePart = symbols.get(1);
			return this;
		}

		@Override
		public IfThenElse build() {
			this.name = String.format("if (%s) %s else %s;", expression, thenPart, elsePart);
			return new IfThenElse(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
