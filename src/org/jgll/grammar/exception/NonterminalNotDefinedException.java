package org.jgll.grammar.exception;

import org.jgll.grammar.symbol.Nonterminal;

public class NonterminalNotDefinedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Nonterminal nonterminal;

	public NonterminalNotDefinedException(Nonterminal nonterminal) {
		super(nonterminal + " not defined.");
		this.nonterminal = nonterminal;
	}
	
	@Override
	public int hashCode() {
		return nonterminal.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof NonterminalNotDefinedException)) {
			return false;
		}
		
		NonterminalNotDefinedException other = (NonterminalNotDefinedException) obj;
		
		return nonterminal.equals(other.nonterminal);
	}
	
}
