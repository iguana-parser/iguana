package org.jgll.grammar.symbol;


public class Star extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Star(Symbol s) {
		super(s.getName() + "*");
		this.s = s.clone();
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public Star clone() {
		return (Star) super.clone();
	}
	
}
