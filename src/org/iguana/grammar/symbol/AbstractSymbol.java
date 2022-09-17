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

import org.iguana.datadependent.attrs.AbstractAttrs;
import org.iguana.grammar.condition.Condition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.iguana.utils.string.StringUtil.listToString;

public abstract class AbstractSymbol extends AbstractAttrs implements Symbol {

	protected final String name;

	protected final String label;

	protected final List<Condition> preConditions;

	protected final List<Condition> postConditions;

	private final Map<String, Object> attributes;

	public AbstractSymbol(SymbolBuilder<? extends Symbol> builder) {
		if (builder.name == null) 
			throw new IllegalArgumentException("Name cannot be null");
		this.name = builder.name;
		this.label = builder.label;
		this.preConditions = builder.preConditions.isEmpty() ? Collections.emptyList() : builder.preConditions;
		this.postConditions = builder.postConditions.isEmpty() ? Collections.emptyList() : builder.postConditions;
		this.attributes = builder.attributes.isEmpty() ? Collections.emptyMap() : builder.attributes;
	}
	
	@Override
	public List<Condition> getPreConditions() {
		return preConditions;
	}
	
	@Override
	public List<Condition> getPostConditions() {
		return postConditions;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		String s = label == null ? name : label + ":" + name;
		if (!preConditions.isEmpty())
			s += " " + listToString(preConditions);
		if (!postConditions.isEmpty())
			s += " " + listToString(postConditions);
		return s;
	}
	
	@Override
	public String toString(int j) {
		return this + (j == 1 ? " . " : "");
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
}
