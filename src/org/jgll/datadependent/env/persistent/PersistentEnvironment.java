package org.jgll.datadependent.env.persistent;

import org.eclipse.imp.pdb.facts.util.ImmutableMap;
import org.eclipse.imp.pdb.facts.util.TrieMap;
import org.jgll.datadependent.env.Environment;

public class PersistentEnvironment implements Environment {
	
	final private ImmutableMap<String, Object> bindings;
	
	static public final Environment empty = new PersistentEnvironment((TrieMap<String, Object>) TrieMap.<String, Object>of());
	
	public PersistentEnvironment(ImmutableMap<String, Object> bindings) {
		this.bindings = bindings;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof PersistentEnvironment)) return false;
		PersistentEnvironment that = (PersistentEnvironment) other;
		return this.bindings == that.bindings || this.bindings.equals(that.bindings);
	}

	@Override
	public boolean isEmpty() {
		return bindings.isEmpty();
	}

	@Override
	public Environment push() {
		return this;
	}

	@Override
	public Environment store(String name, Object value) {
		return new PersistentEnvironment(bindings.__put(name, value));
	}

	@Override
	public Environment pushAndStore(String name, Object value) {
		return this.store(name, value);
	}

	@Override
	public Environment pushAndStore(Object... bindings) {
		Environment newEnv = this;
		for (int i = 0; i < bindings.length; i += 2) {
			newEnv = newEnv.store((String) bindings[i], bindings[i + 1]);
		}
		return newEnv;
	}

	@Override
	public Object lookup(String name) {
		return bindings.get(name);
	}

}
