/*
 * Copyright (c) 2015, CWI
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

import org.iguana.traversal.ISymbolVisitor;
import org.iguana.util.generator.GeneratorUtil;

public class Block extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol[] symbols;

	Block(Builder builder) {
		super(builder);
		this.symbols = builder.symbols;
	}
	
	public static Block block(Symbol... symbols) {
		return builder(symbols).build();
	}
	
	public Symbol[] getSymbols() {
		return symbols;
	}
	
	@Override
	public String getConstructorCode() {
		String[] codes = new String[symbols.length];
		
		int j = 0;
		for (Symbol symbol : symbols) {
			codes[j] = symbol.getConstructorCode();
			j++;
		}
		
		return "Block.builder(" + GeneratorUtil.listToString(codes, ",") + ")" 
							    + super.getConstructorCode()
								+ ".build()";
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public int size() {
		int size = 0;
		for (Symbol symbol: symbols)
			size = size + symbol.size();
		return size;
	}
	
	@Override
	public String toString() {
		return String.format("{ %s }", GeneratorUtil.listToString(symbols, " "));
	}
	
	@Override
	public String toString(int j) {
		String[] strings = new String[symbols.length];
		
		int k = 0;
		for (Symbol symbol : symbols) {
			strings[k] = j <= 1? symbol.toString(j) : symbol.toString();
			j = j - symbol.size();
			k++;
		}
		
		return String.format("{ %s }", GeneratorUtil.listToString(strings, " "));
	}
	
	public static Builder builder(Symbol... symbols) {
		return new Builder(symbols);
	}
	
	public static class Builder extends SymbolBuilder<Block> {
		
		private final Symbol[] symbols;

		public Builder(Block block) {
			super(block);
			this.symbols = block.symbols; 
		}
		
		public Builder(Symbol... symbols) {
			super(String.format("{ %s }", GeneratorUtil.listToString(symbols, " ")));
			
			assert symbols.length != 0;
			
			this.symbols = symbols;
		}

		@Override
		public Block build() {
			return new Block(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
