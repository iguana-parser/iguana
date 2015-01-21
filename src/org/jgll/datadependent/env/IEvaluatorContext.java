package org.jgll.datadependent.env;

public interface IEvaluatorContext {
	
	public Environment getEnvironment();
	
	public void setEnvironment(Environment env);
	
	/**
	 * 
	 * Operations on environment
	 */
	
	public Object lookupInEnvironment(String name);
	
	public void pushEnvironment();
	
	public void storeInEnvironment(String name, Object value);
	
	public void pushAndStoreInEnvironment(String name, Object value);
	
	public void pushAndStoreInEnvironment(Object... bindings);
	
	public Environment getEmptyEnvironment();

}
