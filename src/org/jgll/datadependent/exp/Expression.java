package org.jgll.datadependent.exp;

import org.jgll.datadependent.env.Environment;

public abstract class Expression {
	
	public abstract Object interpret(Environment env); 

}
