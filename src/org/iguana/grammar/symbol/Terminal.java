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

import org.iguana.regex.Epsilon;
import org.iguana.regex.RegularExpression;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.traversal.ISymbolVisitor;

public class Terminal extends AbstractSymbol {

	private final TerminalNodeType nodeType;

	private final RegularExpression regex;

    private static final Terminal epsilon = Terminal.from(Epsilon.getInstance());

    public static Terminal epsilon() {
        return epsilon;
    }

	public static Terminal from(RegularExpression regex) {
		return new Builder(regex).build();
	}

	public Terminal(Builder builder) {
		super(builder);
		this.regex = builder.regex;
		this.nodeType = builder.nodeType;
	}

    @Override
	public Builder copy() {
		return new Builder(this);
	}

	public RegularExpression getRegularExpression() {
		return regex;
	}

	public TerminalNodeType getNodeType() {
		return nodeType;
	}

	@Override
	public int hashCode() {
		return regex.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (!(obj instanceof Terminal))
			return false;

		Terminal other = (Terminal) obj;

		return regex.equals(other.regex);
	}

	public static class Builder extends SymbolBuilder<Terminal> {

		private TerminalNodeType nodeType;
		private RegularExpression regex;

		public Builder(RegularExpression regex) {
			this.regex = regex;
		}

		private Builder() {}

		public Builder(Terminal terminal) {
			super(terminal);
			this.regex = terminal.regex;
			this.nodeType = terminal.getNodeType();
		}

		public Builder setNodeType(TerminalNodeType nodeType) {
			this.nodeType = nodeType;
			return this;
		}

		@Override
		public Terminal build() {
			if (name == null)
				name = regex.toString();
			return new Terminal(this);
		}
	}

	public boolean isNullable() {
		return regex.isNullable();
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
