package org.jgll.datadependent.env;

import java.util.HashMap;
import java.util.Map;

public class NonEmptyEnvironment extends Environment {
	
	private Map<String, Object> variables;
	
	protected NonEmptyEnvironment(Environment parent) {
		this.parent = parent;
	}
	
	protected NonEmptyEnvironment(Environment parent, Map<String, Object> variables) {
		this.parent = parent;
		this.variables = variables;
	}

	public boolean isEmpty() {
		return false;
	}
	
	public Environment push() {
		return new NonEmptyEnvironment(this);
	}
	
	public Environment push(Map<String, Object> variables) {
		return new NonEmptyEnvironment(this, variables);
	}

	protected Object lookupVariableLocally(String name) {
		return variables.get(name);
	}

	public Environment storeVariableLocally(String name, Object value) {
		if(variables == null) variables = new HashMap<>();
		variables.put(name, value);
		return this;
	}

}
