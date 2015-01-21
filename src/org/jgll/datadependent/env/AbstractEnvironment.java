package org.jgll.datadependent.env;

public abstract class AbstractEnvironment implements Environment {
	
	protected AbstractEnvironment parent;
	
	@Override
	public Object lookup(String name) {
		if(this.isEmpty()) return null;
		
		Object value = lookupLocally(name);
		
		return (value != null) ? value : parent.lookup(name);
	}
	
	protected abstract Object lookupLocally(String name);
	
}
