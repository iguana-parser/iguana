package org.jgll.grammar.symbol;


public class Plus extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Plus(Symbol s) {
		super(s.getName() + "+");
		this.s = s.clone();
	}
	
	public Symbol getSymbol() {
		return s;
	}

	@Override
	public Plus clone() {
		return (Plus) super.clone();
	}
	
}
