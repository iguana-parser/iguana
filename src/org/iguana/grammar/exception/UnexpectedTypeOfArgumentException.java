package org.iguana.grammar.exception;

import org.iguana.datadependent.ast.Expression;

public class UnexpectedTypeOfArgumentException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnexpectedTypeOfArgumentException(Expression expression) {
		super("Unexpected type of an operand: " + expression);
	}

}
