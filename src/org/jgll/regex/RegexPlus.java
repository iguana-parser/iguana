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
		this.regexp = new Sequence<>(regexp, new RegexStar(regexp));
	}
	
	@Override
	public Automaton toAutomaton() {
		return combineConditions(regexp.toAutomaton().addFinalStateActions(actions).addRegularExpression(this));
	}
	
	@Override
	public boolean isNullable() {
		return regexp.isNullable();
	}
	
	@Override
	public RegexPlus copy() {
		return new RegexPlus(regexp.copy());
	}

	@Override
	public Set<Range> getFirstSet() {
		return regexp.getFirstSet();
	}

}
