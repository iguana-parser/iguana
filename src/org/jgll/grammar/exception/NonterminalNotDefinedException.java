package org.jgll.grammar.exception;

import org.jgll.grammar.symbol.Nonterminal;

public class NonterminalNotDefinedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NonterminalNotDefinedException(Nonterminal nonterminal) {
		super(nonterminal + " not defined.");
	}
	
}
