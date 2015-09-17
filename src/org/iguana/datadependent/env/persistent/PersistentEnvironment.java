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

package org.iguana.datadependent.env.persistent;

import org.eclipse.imp.pdb.facts.util.ImmutableMap;
import org.eclipse.imp.pdb.facts.util.TrieMap;
import org.iguana.datadependent.ast.VariableDeclaration;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.exception.UndeclaredVariableException;
import org.iguana.grammar.exception.UndefinedRuntimeValueException;

public class PersistentEnvironment implements Environment {
	
	private final PersistentEnvironment parent;
	
	final private ImmutableMap<String, Object> bindings;
	
	static public final Environment EMPTY = new PersistentEnvironment(null, (TrieMap<String, Object>) TrieMap.<String, Object>of());
	
	private PersistentEnvironment(PersistentEnvironment parent, ImmutableMap<String, Object> bindings) {
		this.parent = parent;
		this.bindings = bindings;
	}

	@Override
	public boolean isEmpty() {
		return bindings.isEmpty() && (parent == null || parent.isEmpty());
	}
	
	@Override
	public Environment pop() {
		return parent;
	}

	@Override
	public Environment push() {
		return new PersistentEnvironment(this, (TrieMap<String, Object>) TrieMap.<String, Object>of());
	}
	
	@Override
	public Environment _declare(String name, Object value) {
		return new PersistentEnvironment(parent, bindings.__put(name, value));
	}

	@Override
	public Environment declare(String[] names, Object[] values) {
		ImmutableMap<String, Object> bindings = this.bindings;
		int i = 0;
		while (i < names.length) {
			bindings = bindings.__put(names[i], values[i]);
			i++;
		}
		return new PersistentEnvironment(parent, bindings);
	}

	@Override
	public Environment store(String name, Object value) {
		
		Object result = bindings.get(name);
		
		if (result == null) {
			
			if (parent == null) {
				throw new UndeclaredVariableException(name);
			}
			
			Environment parent = this.parent.store(name, value);
			
			if (parent == this.parent) {
				return this;
			}
			
			return new PersistentEnvironment((PersistentEnvironment) parent, bindings);
		}
		
		ImmutableMap<String, Object> bindings = this.bindings.__put(name, value);
		if (bindings == this.bindings) {
			return this;
		}
		
		return new PersistentEnvironment(parent, bindings);
	}
	
	@Override
	public Object lookup(String name) {
		
		Object value = bindings.get(name);
		
		if (value == null && parent != null) {
			value = parent.lookup(name);
		}
		
		if (value != null) {
			
			if (value == VariableDeclaration.defaultValue) {
				throw UndefinedRuntimeValueException.instance;
			}
			
			return value;
		}
		
		throw new UndeclaredVariableException(name);
	}
	
	@Override
	public int hashCode() {
		return (parent == null ? 0 : parent.hashCode()) + bindings.hashCode();   
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof PersistentEnvironment)) return false;
		
		PersistentEnvironment that = (PersistentEnvironment) other;
		
		if (this.bindings == that.bindings || this.bindings.equals(that.bindings)) {
			
			if (this.parent == that.parent) {
				return true;
			}
			
			if (this.parent != null) {
				return this.parent.equals(that.parent);
			}
			
			return false;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return (parent != null? parent.toString() + " -> " : "() -> ")
				+ (bindings != null? bindings.toString(): "()");
	}

}
