package org.jgll.datadependent.env;


public interface Environment {
	
	public boolean isEmpty();
	
	public Environment push();
	
	public Environment store(String name, Object value);
	
	public Environment store(String[] names, Object[] values);
	
	public Environment pushAndStore(String name, Object value);
	
	public Environment pushAndStore(Object... bindings);
	
	public Object lookup(String name);

}
