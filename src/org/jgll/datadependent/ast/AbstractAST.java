package org.jgll.datadependent.ast;

import org.jgll.datadependent.attrs.AbstractAttrs;
import org.jgll.datadependent.env.IEvaluatorContext;
import org.jgll.datadependent.traversal.IAbstractASTVisitor;

public abstract class AbstractAST extends AbstractAttrs {
	
	public abstract Object interpret(IEvaluatorContext ctx);
	
	public abstract String getConstructorCode();
	
	public abstract <T> T accept(IAbstractASTVisitor<T> visitor);

}
