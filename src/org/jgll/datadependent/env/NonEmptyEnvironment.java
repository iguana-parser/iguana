package org.jgll.datadependent.env;

import java.util.HashMap;
import java.util.Map;

public class NonEmptyEnvironment extends AbstractEnvironment {
	
	private Map<String, Object> variables;
	
	protected NonEmptyEnvironment(AbstractEnvironment parent) {
		this.parent = parent;
	}
	
	protected NonEmptyEnvironment(AbstractEnvironment parent, Map<String, Object> variables) {
		this.parent = parent;
		this.variables = variables;
	}
	
	public boolean isEmpty() {
		return false;
	}
	
	public Environment push() {
		return new NonEmptyEnvironment(this);
	}
	
	@Override
	public Environment store(String name, Object value) {
		if(variables == null) variables = new HashMap<>();
		variables.put(name, value);
		return this;
	}
	
	@Override
    public Environment pushAndStore(String name, Object value) {
    	return this.push().store(name, value);
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
		return variables != null? variables.get(name) : null;
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other) return true;
		if(!(other instanceof NonEmptyEnvironment)) return false;
		NonEmptyEnvironment that = (NonEmptyEnvironment) other;
		if(this.parent == that.parent) {
			if(this.variables != null 
					&& this.variables.equals(that.variables)) {
				return true;
			}
		}
		return false;
	}

}
