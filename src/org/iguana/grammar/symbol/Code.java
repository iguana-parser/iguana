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

import org.iguana.datadependent.ast.Statement;
import org.iguana.traversal.ISymbolVisitor;

import java.util.Collections;
import java.util.List;

import static iguana.utils.string.StringUtil.listToString;

public class Code extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol symbol;
	private final Statement[] statements;
	
	Code(Builder builder) {
		super(builder);
		this.symbol = builder.symbol;
		this.statements = builder.statements;
	}
	
	public static Code code(Symbol symbol, Statement... statements) {
		return new Builder(symbol, statements).build();
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public Statement[] getStatements() {
		return statements;
	}
	
	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}

	@Override
	public List<? extends Symbol> getChildren() {
		return Collections.singletonList(symbol);
	}

	@Override
	public String toString() {
		return String.format("%s {%s}", symbol.toString(), listToString(statements, ";"));
	}
		
	public static class Builder extends SymbolBuilder<Code> {
		
		private Symbol symbol;
		private Statement[] statements;

		public Builder(Code code) {
			super(code);
			this.symbol = code.symbol;
			this.statements = code.statements;
		}
		
		public Builder(Symbol symbol, Statement... statements) {
			assert statements.length != 0;
			
			this.symbol = symbol;
			this.statements = statements;
		}

		@Override
		public SymbolBuilder<Code> setChildren(List<Symbol> symbols) {
			this.symbol = symbols.get(0);
			return this;
		}

		@Override
		public Code build() {
			this.name = String.format("%s {%s}", symbol.toString(), listToString(statements, ";"));
			return new Code(this);
		}
		
	}
	
	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
