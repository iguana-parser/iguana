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

package org.iguana.grammar.patterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.parser.HashFunctions;
import org.iguana.util.generator.ConstructorCode;

public class AbstractPattern implements Serializable, ConstructorCode {
	
	private static final long serialVersionUID = 1L;
	
	protected final List<Symbol> parent;
	protected final List<Symbol> child;
	protected final int position;
	protected final Nonterminal nonterminal;
	
	public AbstractPattern(Nonterminal nonteriminal, List<Symbol> parent, int position, List<Symbol> child) {
		
		if(parent == null || child == null) {
			throw new IllegalArgumentException("parent or child alternates cannot be null.");
		}
		
		this.parent = new ArrayList<>(parent);
		
		if (position != 0) {
			this.position = parent.size() - 1;
		} else {
			this.position = 0;
		}
		this.child = new ArrayList<>(child);
		this.nonterminal = nonteriminal;
	}
		
	public int getPosition() {
		return position;
	}
	
	public List<Symbol> getParent() {
		return parent;
	}
	
	public List<Symbol> getChild() {
		return child;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
	
	public String getFilteredNontemrinalName() {
		return parent.get(position).getName();
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(nonterminal.hashCode(), position, parent.hashCode());
	}
	
	/**
	 * Two filters are equal if they can be applied to the same alternate.
	 * Let F1 be (nonterminal1, parent1, position1, child1) and
	 * F2 be (nonterminal2, parent2, position2, child2). Then F1 equals F2
	 * if nontermainl1 == nontermianl1, parent1 == parent2 and
	 * position1 == position2.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof AbstractPattern)) {
			return false;
		}
		
		AbstractPattern other = (AbstractPattern) obj;
		
		return other.nonterminal.equals(this.nonterminal) &&
			   other.position == this.position && 
			   other.parent.equals(this.parent);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(nonterminal);
		sb.append(", ");
		
		int i = 0;
		for (Symbol symbol : parent) {
			if (i == position) {
				sb.append(". ");
			}
			sb.append(symbol).append(" ");
			i++;
		}

		sb.delete(sb.length() - 1, sb.length());
		sb.append(", ");

		i = 0;
		for (Symbol symbol : child) {
			sb.append(symbol).append(" ");
		}
		sb.delete(sb.length() - 1, sb.length());

		sb.append(")");
		return sb.toString();
	}

	@Override
	public String getConstructorCode() {
		return nonterminal.getConstructorCode() + ", " + 
			   asList(parent) + ", " +
			   position + ", " +
			   asList(child);
	}
}
