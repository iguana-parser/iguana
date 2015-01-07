package org.jgll.datadependent.ast;

import org.jgll.datadependent.env.EvaluatorContext;

public abstract class AbstractAST {
	
	public abstract Object interpret(EvaluatorContext ctx);

}
