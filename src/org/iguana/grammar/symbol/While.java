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

public class While extends AbstractSymbol {

	private final Expression expression;
	private final Symbol body;

	While(Builder builder) {
		super(builder);
		this.expression = builder.expression;
		this.body = builder.body;
	}
	
	public static While whileLoop(Expression expression, Symbol body) {
		return new Builder(expression, body).build();
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public Symbol getBody() {
		return body; 
	}
	
	@Override
	public Builder copy() {
		return new Builder(this);
	}

	@Override
	public List<Symbol> getChildren() {
		return Collections.singletonList(body);
	}

	@Override
	public int size() {
		return body.size();
	}
	
	@Override
	public String toString() {
		return String.format("while (%s) %s", expression.toString(), body.toString());
	}
	
	@Override
	public String toString(int j) {
		return String.format("while (%s) %s", expression.toString(), body.toString(j));
	}
	
	public static class Builder extends SymbolBuilder<While> {
		
		private Expression expression;
		private Symbol body;

		public Builder(While whileSymbol) {
			super(whileSymbol);
			this.expression = whileSymbol.expression;
			this.body = whileSymbol.body;
		}
		
		public Builder(Expression expression, Symbol body) {
			this.expression = expression;
			this.body = body;
		}

		@Override
		public SymbolBuilder<While> setChildren(List<Symbol> symbols) {
			this.body = symbols.get(0);
			return this;
		}

		@Override
		public While build() {
			this.name = String.format("while (%s) %s", expression.toString(), body.toString());
			return new While(this);
		}
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
