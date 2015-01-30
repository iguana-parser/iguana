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


	@Override
	public void popEnvironment() {
		env = env.pop();
	}

	@Override
	public void pushEnvironment() {
		env = env.push();
	}

	@Override
	public void declareVariable(String name, Object value) {
		env = env.declare(name, value);
	}

	@Override
	public void declareVariables(String[] names, Object[] values) {
		env = env.declare(names, values);
	}

	@Override
	public void storeVariable(String name, Object value) {
		env = env.store(name, value);
	}

	@Override
	public Object lookupVariable(String name) {
		return env.lookup(name);
	}
	
}
