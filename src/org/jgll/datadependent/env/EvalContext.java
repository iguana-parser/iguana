package org.jgll.datadependent.env;

public class EvalContext {
	
	private Environment env;
	
	public Environment getCurrentEnv() {
		return env;
	}
	
	public void setCurrentEnv(Environment env) {
		this.env = env;
	}

}
