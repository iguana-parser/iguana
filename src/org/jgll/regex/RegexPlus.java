package org.jgll.regex;

import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;


public class RegexPlus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexPlus(RegularExpression regexp) {
		super(regexp.getName() + "+");
		RegularExpression copy = regexp.clone();
		this.regexp = new Sequence<>(new RegexStar(regexp), copy);
	}
	
	@Override
	protected Automaton createAutomaton() {
		return regexp.toAutomaton();
	}
	
	@Override
	public boolean isNullable() {
		return regexp.isNullable();
	}
	
	@Override
	public RegexPlus clone() {
		return (RegexPlus) super.clone();
	}

	@Override
	public Set<Range> getFirstSet() {
		return regexp.getFirstSet();
	}

}
