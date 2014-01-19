package org.jgll.regex;

import org.jgll.grammar.symbol.AbstractRegularExpression;


public class RegexPlus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexPlus(RegularExpression regexp) {
		super(regexp.getName() + "+");
		this.regexp = new Sequence<>(regexp, new RegexStar(regexp));
	}
	
	@Override
	public Automaton toAutomaton() {
		return regexp.toAutomaton().addFinalStateActions(actions).addRegularExpression(this);
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
