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

import org.iguana.grammar.condition.Condition;

import java.util.*;

public abstract class SymbolBuilder<T extends Symbol> {
	
	protected String name;
	
	protected String label;
	
	protected Object object;

	protected Set<Condition> preConditions = new LinkedHashSet<>();
	
	protected Set<Condition> postConditions = new LinkedHashSet<>();

	public SymbolBuilder(T symbol) {
		this.name = symbol.getName();
		this.label = symbol.getLabel();
		this.preConditions = new LinkedHashSet<>(symbol.getPreConditions());
		this.postConditions = new LinkedHashSet<>(symbol.getPostConditions());
	}
	
	public SymbolBuilder() { }
	
	public SymbolBuilder<T> setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public SymbolBuilder<T> setName(String name) {
		this.name = name;
		return this;
	}
	
	public SymbolBuilder<T> addPreCondition(Condition condition) {
		preConditions.add(condition);
		return this;
	}
	
	public SymbolBuilder<T> addPostCondition(Condition condition) {
		postConditions.add(condition);
		return this;
	}	
	
	public SymbolBuilder<T> setObject(Object object) {
		this.object = object;
		return this;
	}
	
	public SymbolBuilder<T> addConditions(Symbol s) {
		addPreConditions(s.getPreConditions());
		addPostConditions(s.getPostConditions());
		return this;
	}
	
 	public SymbolBuilder<T> addPreConditions(Iterable<Condition> conditions) {
 		conditions.forEach(c -> preConditions.add(c));
		return this;
	}
 	
 	public SymbolBuilder<T> addPostConditions(Iterable<Condition> conditions) {
 		conditions.forEach(c -> postConditions.add(c));
		return this;
	}
 	
 	public SymbolBuilder<T> removePreConditions(Collection<Condition> conditions) {
 		preConditions.removeAll(conditions);
 		return this;
 	}
 	
 	public SymbolBuilder<T> removePostConditions(Collection<Condition> conditions) {
 		postConditions.removeAll(conditions);
 		return this;
 	}

	public SymbolBuilder<T> setPreConditions(Set<Condition> conditions) {
		preConditions = conditions;
		return this;
	}

	public SymbolBuilder<T> setPostConditions(Set<Condition> conditions) {
		postConditions = conditions;
		return this;
	}

	public SymbolBuilder<T> setChildren(List<Symbol> symbols) {
		return this;
	}
 	
	public abstract T build();
	
}
