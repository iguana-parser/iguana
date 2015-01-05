package org.jgll.datadependent.ast;

import org.jgll.datadependent.env.EvalContext;

public abstract class AbstractAST {
	
	public abstract Object interpret(EvalContext ctx);

}
