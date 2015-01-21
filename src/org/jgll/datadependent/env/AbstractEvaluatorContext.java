package org.jgll.datadependent.env;

public abstract class AbstractEvaluatorContext implements IEvaluatorContext {
	
	private Environment env;

	@Override
	public Environment getEnvironment() {
		return env;
	}

	@Override
	public void setEnvironment(Environment env) {
		this.env = env;
	}
	
	/**
	 * 
	 * Operations on environment
	 */

	@Override
	public Object lookupInEnvironment(String name) {
		return env.lookup(name);
	}

	@Override
	public void pushEnvironment() {
		env = env.push();
	}

	@Override
	public void storeInEnvironment(String name, Object value) {
		env = env.store(name, value);
	}

	@Override
	public void pushAndStoreInEnvironment(String name, Object value) {
		env = env.pushAndStore(name, value);
	}

	@Override
	public void pushAndStoreInEnvironment(Object... bindings) {
		env = env.pushAndStore(bindings);
	}

}
