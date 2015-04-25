package org.iguana.datadependent.ast;

import org.iguana.datadependent.attrs.AbstractAttrs;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.datadependent.traversal.IAbstractASTVisitor;

public abstract class AbstractAST extends AbstractAttrs {
	
	private static final long serialVersionUID = 1L;

	public abstract Object interpret(IEvaluatorContext ctx);
	
	public abstract String getConstructorCode();
	
	public abstract <T> T accept(IAbstractASTVisitor<T> visitor);

}
