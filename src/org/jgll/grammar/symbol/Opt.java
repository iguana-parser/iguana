package org.jgll.grammar.symbol;

public class Opt extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Opt(Symbol s) {
		super(s.getName() + "?");
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}

	@Override
	public Symbol copy() {
		return null;
	}
	
}
