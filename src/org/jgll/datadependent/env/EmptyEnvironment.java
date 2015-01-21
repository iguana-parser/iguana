package org.jgll.datadependent.env;

import java.util.HashMap;
import java.util.Map;

public final class EmptyEnvironment extends AbstractEnvironment {
	
	public static EmptyEnvironment instance = new EmptyEnvironment(); 
	
	private EmptyEnvironment() {}
	

	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public Environment push() {
		return this;
	}
	
	@Override
	public Environment store(String name, Object value) {
		Map<String, Object> bindings = new HashMap<>();
		bindings.put(name, value);
		return new NonEmptyEnvironment(this, bindings);
	}
	
	@Override
	public Environment pushAndStore(String name, Object value) {
		return this.store(name, value);
	}
	
	@Override
	public Environment pushAndStore(Object... bindings) {
		if (bindings.length == 0) return this.push();
		Map<String, Object> variables = new HashMap<>();
		for (int i = 0; i < bindings.length; i += 2) {
			variables.put((String) bindings[i], bindings[i + 1]);
		}
		return new NonEmptyEnvironment(this, variables);
	}
	
	@Override
	protected Object lookupLocally(String name) {
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other;
	}

}
