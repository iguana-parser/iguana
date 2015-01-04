package org.jgll.datadependent.env;

import java.util.Map;

public abstract class Environment {
	
	protected Environment parent;
	
	public abstract boolean isEmpty();
	
	public abstract Environment push();
	
	public abstract Environment push(Map<String, Object> variables);
	
	protected abstract Object lookupVariableLocally(String name);
	
	public Object lookupVariable(String name) {
		if(this.isEmpty()) return null;
		
		Object value = lookupVariableLocally(name);
		
		return (value != null) ? value : parent.lookupVariable(name);
	}
	
	public abstract Environment storeVariableLocally(String name, Object value);

}
