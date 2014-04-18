package org.jgll.regex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;


public class RegexPlus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression plus;
	
	public RegexPlus(RegularExpression regexp, Set<Condition> conditions) {
		super(regexp.getName() + "+", conditions);
		List<RegularExpression> list = new ArrayList<>();
		list.add(new RegexStar(regexp));
		list.add(regexp);
		this.plus = new Sequence<>(list, conditions);
	}
	
	public RegexPlus(RegularExpression regexp) {
		this(regexp, Collections.<Condition>emptySet());
	}
	
	@Override
	protected Automaton createAutomaton() {
		return plus.toAutomaton().setName(name);
	}
	
	@Override
	public boolean isNullable() {
		return plus.isNullable();
	}
	
	@Override
	public Set<Range> getFirstSet() {
		return plus.getFirstSet();
	}

	@Override
	public RegexPlus withConditions(Set<Condition> conditions) {
		return new RegexPlus(plus, conditions);
	}

}
