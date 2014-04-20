package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.CollectionsUtil;


public class Star extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final Symbol s;
	
	public Star(Symbol s, Set<Condition> conditions) {
		super(s.getName() + "*", conditions);
		this.s = s;
	}
	
	public Star(Symbol s) {
		this(s, Collections.<Condition>emptySet());
	}
	
	public Symbol getSymbol() {
		return s;
	}

	@Override
	public Star withConditions(Set<Condition> conditions) {
		return new Star(s, CollectionsUtil.union(conditions, this.conditions));
	}
	
}
