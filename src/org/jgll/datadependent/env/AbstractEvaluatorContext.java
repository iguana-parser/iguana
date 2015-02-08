package org.jgll.datadependent.env;

import org.jgll.util.Input;

public abstract class AbstractEvaluatorContext implements IEvaluatorContext {
	
	private final Input input;
	
	private Environment env;
	
	public AbstractEvaluatorContext(Input input) {
		this.input = input;
	}
	
	@Override
	public Input getInput() {
		return this.input;
	}

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
