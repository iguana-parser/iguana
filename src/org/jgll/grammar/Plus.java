package org.jgll.grammar;

public class Plus extends Nonterminal {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Plus(Symbol s) {
		super(s.getName() + "+");
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}


}
