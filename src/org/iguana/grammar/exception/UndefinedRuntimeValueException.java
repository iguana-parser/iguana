package org.iguana.grammar.exception;

public class UndefinedRuntimeValueException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	private UndefinedRuntimeValueException() {
		super("Undefined runtime value.");
	}
	
	static public UndefinedRuntimeValueException instance = new UndefinedRuntimeValueException();

}
