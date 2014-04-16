package org.jgll.grammar.symbol;

public class Opt extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private Symbol s;
	
	public Opt(Symbol s) {
		super(s.getName() + "?");
		this.s = s.clone();
	}
	
	public Symbol getSymbol() {
		return s;
	}
	
	@Override
	public Opt clone() {
		Opt clone = (Opt) super.clone();
		clone.s = s.clone();
		return clone;
	}
	
}
