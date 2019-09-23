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

import iguana.regex.Epsilon;
import org.iguana.datadependent.ast.Expression;
import org.iguana.traversal.ISymbolVisitor;

public class IfThen extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Expression expression;
	private final Symbol thenPart;

	IfThen(Builder builder) {
		super(builder);
		this.expression = builder.expression;
		this.thenPart = builder.thenPart;
	}
	
	public static IfThen from(Expression expression, Symbol thenPart) {
		return builder(expression, thenPart).build();
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public Symbol getThenPart() {
		return thenPart;
	}
	
	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public int size() {
		return thenPart.size() + 1;
	}
	
	@Override
	public String toString() {
		return String.format("if (%s) %s", expression.toString(), thenPart.toString());
	}
	
	@Override
	public String toString(int j) {
		return String.format("if (%s) { %s } else %s", 
								expression.toString(), 
								thenPart.toString(j), 
								j - thenPart.size() <= 1? epsilonToString(j - thenPart.size())
														  : Epsilon.getInstance().toString());
	}

	public String epsilonToString(int j) {
		return Epsilon.getInstance().toString() + (j == 1? " . " : "");
	}

	public static Builder builder(Expression expression, Symbol symbol) {
		return new Builder(expression, symbol);
	}
	
	public static class Builder extends SymbolBuilder<IfThen> {
		
		private final Expression expression;
		private final Symbol thenPart;

		public Builder(IfThen ifThen) {
			super(ifThen);
			this.expression = ifThen.expression;
			this.thenPart = ifThen.thenPart;
		}
		
		public Builder(Expression expression, Symbol thenPart) {
			super(String.format("if (%s) %s", expression.toString(), thenPart.toString()));
			this.expression = expression;
			this.thenPart = thenPart;
		}

		@Override
		public IfThen build() {
			return new IfThen(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
