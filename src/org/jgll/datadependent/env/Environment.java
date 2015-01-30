package org.jgll.datadependent.env;


public interface Environment {
	
	public boolean isEmpty();
	
	public Environment pop();
	
	public Environment push();
	
	public Environment declare(String name, Object value);
	
	public Environment declare(String[] names, Object[] values);
	
	public Environment store(String name, Object value);
	
	public Object lookup(String name);

}
