package org.jgll.regex;

import org.jgll.grammar.symbol.AbstractSymbol;


public class RegexPlus extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexPlus(RegularExpression regexp) {
		super(regexp.getName() + "+");
		this.regexp = new Sequence<>(regexp, new RegexStar(regexp));
	}
	
	@Override
	public Automaton toNFA() {
		return regexp.toNFA();
	}
	
	@Override
	public boolean isNullable() {
		return regexp.isNullable();
	}
	
	@Override
	public RegexPlus copy() {
		return new RegexPlus(regexp.copy());
	}

}
