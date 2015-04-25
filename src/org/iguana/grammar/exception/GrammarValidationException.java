package org.iguana.grammar.exception;

import java.util.Set;

public class GrammarValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public GrammarValidationException(Set<RuntimeException> exceptions) {
		super(getMessage(exceptions));
	}
	
	private static String getMessage(Set<RuntimeException> exceptions) {
		StringBuilder sb = new StringBuilder();
		
		for (RuntimeException exception : exceptions) {
			sb.append(exception.getMessage());
		}
		
		return sb.toString();
	}
 
}
