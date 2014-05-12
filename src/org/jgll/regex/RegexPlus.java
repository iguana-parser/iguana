package org.jgll.regex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.CollectionsUtil;


public class RegexPlus extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regex;
	
	private final RegularExpression plus;
	
	
	public RegexPlus(RegularExpression regexp) {
		this(regexp, Collections.<Condition>emptySet());
	}
	
	public RegexPlus(RegularExpression regexp, Object object) {
		this(regexp, Collections.<Condition>emptySet(), object);
	}

	public RegexPlus(RegularExpression regex, Set<Condition> conditions) {
		this(regex, conditions, null);
	}
	
	public RegexPlus(RegularExpression regex, Set<Condition> conditions, Object object) {
		this(getName(regex), regex, conditions, object);
	}
	
	public RegexPlus(String name, RegularExpression regex, Set<Condition> conditions, Object object) {
		super(name, conditions, object);
		this.regex = regex;
		List<RegularExpression> list = new ArrayList<>();
		list.add(new RegexStar(regex.withoutConditions()));
		list.add(regex.withoutConditions());
		this.plus = new Sequence<>(list);
	}
	
	private static String getName(RegularExpression regexp) {
		return regexp.getName() + "+";
	}
	
	@Override
	protected Automaton createAutomaton() {
		return plus.getAutomaton().setName(name);
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
	public Set<Range> getNotFollowSet() {
		return plus.getFirstSet();
	}

	@Override
	public RegexPlus withConditions(Set<Condition> conditions) {
		return new RegexPlus(regex, CollectionsUtil.union(conditions, this.conditions));
	}

	@Override
	public RegexPlus withoutConditions() {
		return new RegexPlus(regex);
	}

}
