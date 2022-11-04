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

package org.iguana.datadependent.env.simple;

import org.iguana.datadependent.ast.VariableDeclaration;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.exception.UndeclaredVariableException;
import org.iguana.grammar.exception.UndefinedRuntimeValueException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.iguana.utils.string.StringUtil.listToString;


public class SimpleImmutableEnvironment implements Environment {
	
	final private SimpleImmutableEnvironment parent;
	
	final private Map<String, Object> bindings;
	
	final private int hashCode;
	
	static final Environment EMPTY = new SimpleImmutableEnvironment(null, new HashMap<>(), 0);
	
	private SimpleImmutableEnvironment(SimpleImmutableEnvironment parent, Map<String, Object> bindings, int hashCode) {
		this.parent = parent;
		this.bindings = bindings;
		this.hashCode = hashCode;
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
		return new SimpleImmutableEnvironment(this, new HashMap<>(), this.hashCode());
	}

	@Override
	public Environment _declare(String name, Object value) {
		Map<String, Object> bindings = new HashMap<>(this.bindings);
		bindings.put(name, value);
		return new SimpleImmutableEnvironment(parent, bindings, this.hashCode + (name.hashCode() ^ value.hashCode()));
	}

	@Override
	public Environment declare(String[] names, Object[] values) {
		Map<String, Object> bindings = new HashMap<>(this.bindings);
		int i = 0;
		int hashCode = this.hashCode;
		while (i < names.length) {
			bindings.put(names[i], values[i]);
			hashCode = hashCode + (names[i].hashCode() ^ values[i].hashCode());
			i++;
		}
		return new SimpleImmutableEnvironment(parent, bindings, hashCode);
	}

	@Override
	public Environment store(String name, Object value) {
		
		Object result = bindings.get(name);
		
		if (result == null) {
			
			if (parent == null)
				throw new UndeclaredVariableException(name);
			
			Environment parent = this.parent.store(name, value);
			
			if (parent == this.parent)
				return this;
			
			int hashCode = parent.hashCode();
			for (Map.Entry<String, Object> entry : this.bindings.entrySet())
				hashCode = hashCode + (entry.getKey().hashCode() ^ entry.getValue().hashCode());
			
			return new SimpleImmutableEnvironment((SimpleImmutableEnvironment) parent, bindings, hashCode);
		}
		
		Map<String, Object> bindings = new HashMap<>(this.bindings);
		bindings.put(name, value);
		
		return new SimpleImmutableEnvironment(parent, bindings, hashCode + (name.hashCode() ^ value.hashCode()));
	}

	@Override
	public Object lookup(String name) {
		
		Object value = bindings.get(name);
		
		if (value != null && parent != null)
			return parent.lookup(name);
		
		if (value != null) {
			if (value == VariableDeclaration.defaultValue)
				throw UndefinedRuntimeValueException.instance;
			
			return value;
		}
		
		throw new UndeclaredVariableException(name);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) 
			return true;
		
		if (this.hashCode() != other.hashCode())
			return false;
		
		if (!(other instanceof SimpleImmutableEnvironment))
			return false;
		
		SimpleImmutableEnvironment that = (SimpleImmutableEnvironment) other;
		
		if (bindings == that.bindings || bindings.equals(that.bindings)) {
			
			if (parent == that.parent)
				return true;
			
			if (parent != null)
				return parent.equals(that.parent);
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public String toString() {
		return (parent != null? parent.toString() + " -> " : "() -> ")
				+ (bindings != null? listToString(bindings.entrySet().stream()
            .map(entry -> entry.getKey() + " : " + entry.getValue()).collect(Collectors.toList()), ";")
								   : "()");
	}

	@Override
	public Environment _declare(Object value) {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

	@Override
	public Environment declare(Object[] values) {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

	@Override
	public Environment store(int i, Object value) {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

	@Override
	public Object lookup(int i) {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

}
