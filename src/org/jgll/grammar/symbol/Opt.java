package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;

public class Opt extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	public Opt(Symbol s) {
		this(s, Collections.<Condition>emptySet());
	}
	
	public Opt(Symbol s, Set<Condition> conditions) {
		super(s.getName() + "?", conditions);
		this.s = s;
	}
	
	public Symbol getSymbol() {
		return s;
	}

	@Override
	public Opt withConditions(Set<Condition> conditions) {
		conditions.addAll(this.conditions);
		return new Opt(s, conditions);
	}
}
