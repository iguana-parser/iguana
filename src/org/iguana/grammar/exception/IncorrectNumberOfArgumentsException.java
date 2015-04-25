package org.iguana.grammar.exception;

import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.symbol.Nonterminal;

public class IncorrectNumberOfArgumentsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public IncorrectNumberOfArgumentsException(Nonterminal nonterminal, Expression[] arguments) {
		super("Incorrect number of arguments passed to nonterminal " + nonterminal + ": " + arguments.length + " instead of " + nonterminal.getParameters().length);
	}

}
