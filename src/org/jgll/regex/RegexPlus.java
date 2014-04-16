package org.jgll.regex;

import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;


public class RegexPlus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private RegularExpression plus;
	
	public RegexPlus(RegularExpression regexp) {
		super(regexp.getName() + "+");
		this.plus = new Sequence<>(new RegexStar(regexp.clone()), regexp.clone());
	}
	
	@Override
	protected Automaton createAutomaton() {
		plus.addConditions(conditions);
		return plus.toAutomaton().setName(name);
	}
	
	@Override
	public boolean isNullable() {
		return plus.isNullable();
	}
	
	@Override
	public RegexPlus clone() {
		RegexPlus clone = (RegexPlus) super.clone();
		clone.plus = plus.clone();
		return clone;
	}

	@Override
	public Set<Range> getFirstSet() {
		return plus.getFirstSet();
	}

}
