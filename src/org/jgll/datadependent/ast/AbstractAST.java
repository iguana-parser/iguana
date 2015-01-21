package org.jgll.datadependent.ast;

import org.jgll.datadependent.env.IEvaluatorContext;

public abstract class AbstractAST {
	
	public abstract Object interpret(IEvaluatorContext ctx);

}
