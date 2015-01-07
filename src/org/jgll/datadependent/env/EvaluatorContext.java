package org.jgll.datadependent.env;

public class EvaluatorContext {
	
	private Environment env;
	
	public EvaluatorContext(Environment env) {
		this.env = env;
	}
	
	public Environment getCurrentEnv() {
		return env;
	}
	
	public void setCurrentEnv(Environment env) {
		this.env = env;
	}

}
