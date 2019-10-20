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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static iguana.utils.string.StringUtil.listToString;

/**
 * A group represents a grouping of symbols, e.g, (A B C) in the EBNF notation.
 */
public class Group extends AbstractSymbol implements Iterable<Symbol> {

	private static final long serialVersionUID = 1L;

	private final List<Symbol> symbols;
	
	public static Group from(List<Symbol> symbols) {
		return new Builder(symbols).build();
	}
	
	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <T extends Symbol> Group from(T...elements) {
		return from(Arrays.asList(elements));
	}
	
	private Group(Builder builder) {
		super(builder);
		this.symbols = builder.symbols;
	}
	
	public int size() {
		return symbols.size();
	}
	
	public Symbol get(int index) {
		return symbols.get(index);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(!(obj instanceof Group))
			return false;
		
		Group other = (Group) obj;
		
		return symbols.equals(other.symbols);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public Iterator<Symbol> iterator() {
		return symbols.iterator();
	}
	
	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}

	@Override
	public List<? extends Symbol> getChildren() {
		return symbols;
	}

	public List<Symbol> getSymbols() {
		return symbols;
	}
	
	@Override
	public String toString() {
				
		String body = "(" + listToString(symbols, " ") + ")";
		
		String s = label == null ? body : label + ":" + body;
		if (!preConditions.isEmpty())
			s += " " + listToString(preConditions);
		if (!postConditions.isEmpty())
			s += " " + listToString(postConditions);
		return s;
	}

	public static class Builder extends SymbolBuilder<Group> {

		private List<Symbol> symbols = new ArrayList<>();

		private Builder() { }

		public Builder(Symbol...symbols) {
			this(Arrays.asList(symbols));
		}

		public Builder(List<Symbol> symbols) {
			this.symbols = symbols;
		}
		
		public Builder(Group seq) {
			super(seq);
			this.symbols = seq.symbols;
		}
		
		public Builder add(Symbol s) {
			symbols.add(s);
			return this;
		}
		
		public Builder add(List<Symbol> symbols) {
			this.symbols.addAll(symbols);
			return this;
		}

		@Override
		public SymbolBuilder<Group> setChildren(List<Symbol> symbols) {
			this.symbols = symbols;
			return this;
		}

		@Override
		public Group build() {
			this.name = "(" + symbols.stream().map(Symbol::getName).collect(Collectors.joining(" ")) + ")";
			return new Group(this);
		}
	}

	@Override
	public <E> E accept(ISymbolVisitor<E> visitor) {
		return visitor.visit(this);
	}
	
}
