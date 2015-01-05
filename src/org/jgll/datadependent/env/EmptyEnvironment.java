package org.jgll.datadependent.env;

import java.util.HashMap;
import java.util.Map;

public final class EmptyEnvironment extends Environment {
	
	public static EmptyEnvironment instance = new EmptyEnvironment(); 
	
	private EmptyEnvironment() {}

	public boolean isEmpty() {
		return true;
	}
	
	public Environment push() {
		return this;
	}
	
	public Environment push(Map<String, Object> variables) {
		return new NonEmptyEnvironment(this, variables);
	}

	protected Object lookupVariableLocally(String name) {
		return null;
	}

	public Environment storeVariableLocally(String name, Object value) {
		Map<String, Object> variables = new HashMap<>();
		variables.put(name, value);
		return new NonEmptyEnvironment(this, variables);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other;
	}

}
