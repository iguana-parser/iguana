package org.jgll.datadependent.env;

import java.util.HashMap;
import java.util.Map;

import org.jgll.grammar.exception.UndeclaredVariableException;
import org.jgll.util.Input;

public abstract class AbstractEvaluatorContext implements IEvaluatorContext {
	
	private final Input input;
	
	private Environment env;
	
	private Map<String, Object> global;
	
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
	
	@Override
	public void declareGlobalVariables(String[] names, Object[] values) {
		assert names.length == values.length;
		
		if (names.length == 0)
			return;
		
		if (global == null)
			global = new HashMap<>();
		
		int i = 0;
		while (i < names.length) {
			global.put(names[i], values[i]);
			i++;
		}
	}
	
	@Override
	public Object lookupGlobalVariable(String name) {
		if (global == null)
			return null;
		
		Object value = global.get(name);
		
		if (value == null) 
			throw new UndeclaredVariableException(name);
		
		return value;
	}
	
}
