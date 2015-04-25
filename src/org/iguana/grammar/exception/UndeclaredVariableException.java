package org.iguana.grammar.exception;

public class UndeclaredVariableException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UndeclaredVariableException(String name) {
		super("Undeclared variable: " + name);
	}

}
