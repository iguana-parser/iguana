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

package org.iguana.grammar.patterns;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.RuntimeRule;
import org.iguana.grammar.symbol.Symbol;

import java.io.Serializable;
import java.util.List;


public class PrecedencePattern extends AbstractPattern implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public PrecedencePattern(Nonterminal nonteriminal, List<Symbol> parent, int position, List<Symbol> child) {
		super(nonteriminal, parent, position, child);
	}
	
	public static PrecedencePattern from(RuntimeRule parent, int position, RuntimeRule child) {
		return new PrecedencePattern(parent.getHead(), parent.getBody(), position, child.getBody());
	}

	/*
	 * A direct filter is of the form (E, alpha .E beta, gamma).
	 * In other words, the filtered nonterminal is the same
	 * as the filter's nonterminal.
	 */
	public boolean isDirect() {
		return nonterminal.equals(parent.get(position));
	}
	
	public boolean isParentBinary() {
		return nonterminal.equals(parent.get(0)) && nonterminal.equals(parent.get(parent.size() - 1));
	}
	
	public boolean isChildBinary() {
		return nonterminal.equals(child.get(0)) && nonterminal.equals(child.get(child.size() - 1));
	}
	
	public boolean isLeftMost() {
		return position == 0;
	}
	
	public boolean isRightMost() {
		return position == parent.size() - 1;
	}

}
