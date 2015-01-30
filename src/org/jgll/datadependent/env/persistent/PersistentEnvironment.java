package org.jgll.datadependent.env.persistent;

import org.eclipse.imp.pdb.facts.util.ImmutableMap;
import org.eclipse.imp.pdb.facts.util.TrieMap;
import org.jgll.datadependent.ast.VariableDeclaration;
import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.exception.UndeclaredVariableException;
import org.jgll.grammar.exception.UndefinedRuntimeValueException;

public class PersistentEnvironment implements Environment {
	
	private final PersistentEnvironment parent;
	
	final private ImmutableMap<String, Object> bindings;
	
	static public final Environment EMPTY = new PersistentEnvironment(null, (TrieMap<String, Object>) TrieMap.<String, Object>of());
	
	public PersistentEnvironment(PersistentEnvironment parent, ImmutableMap<String, Object> bindings) {
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
	public Environment declare(String name, Object value) {
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

}
