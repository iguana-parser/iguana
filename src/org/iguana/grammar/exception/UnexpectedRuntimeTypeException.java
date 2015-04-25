package org.iguana.grammar.exception;

import org.iguana.datadependent.ast.Expression;

public class UnexpectedRuntimeTypeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnexpectedRuntimeTypeException(Expression expression) {
		super("Unexpected runtime type of a value: " + expression);
	}

}
