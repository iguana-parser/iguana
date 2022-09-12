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


import org.iguana.traversal.ISymbolVisitor;

public class Start extends AbstractSymbol {

	private final String startSymbol;
	
    public static Start from(String startSymbol) {
        return new Builder(startSymbol).build();
    }

	public Start(Builder builder) {
		super(builder);
		this.startSymbol = builder.startSymbol;
	}

	public String getStartSymbol() {
		return startSymbol;
	}

	@Override
	public Builder copy() {
		return new Builder(name);
	}

    @Override
    public <T> T accept(ISymbolVisitor<T> visitor) {
        return visitor.visit(this);
    }

	@Override
	public boolean equals(Object obj) {
    	if (this == obj) return true;
    	if (!(obj instanceof Start)) return false;
    	Start other = (Start) obj;
    	return this.name.equals(other.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public static class Builder extends SymbolBuilder<Start> {

		private String startSymbol;

		private Builder() {}

		public Builder(String startSymbol) {
			this.startSymbol = startSymbol;
		}

		@Override
		public Start build() {
			if (startSymbol == null)
				throw new IllegalArgumentException("startSymbol cannot be null.");
			this.name = "Start(" + startSymbol + ")";
			return new Start(this);
		}
	}
}
