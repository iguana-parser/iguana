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

public class Return extends AbstractSymbol {
	
	private static final long serialVersionUID = 1L;
	
	private final Expression expression;
	
	public Return(Builder builder) {
		super(builder);
		this.expression = builder.expression;
	}
	
	public static Return ret(Expression expression) {
		return builder(expression).build();
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String toString() {
		return String.format("{%s}", expression.toString());
	}
	
	public static Builder builder(Expression expression) {
		return new Builder(expression);
	}
	
	public static class Builder extends SymbolBuilder<Return> {
		
		private final Expression expression;

		public Builder(Return ret) {
			super(ret);
			this.expression = ret.expression;
		}
		
		public Builder(Expression expression) {
			super(String.format("{%s}", expression.toString()));
			this.expression = expression;
		}

		@Override
		public Return build() {
			return new Return(this);
		}
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
