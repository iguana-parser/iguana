package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;

public abstract class AbstractSymbol implements Symbol {

	private static final long serialVersionUID = 1L;
	
	protected Set<Condition> conditions;
	
	protected String name;
	
	public AbstractSymbol(String name) {
		this(name, Collections.<Condition>emptySet());
	}
	
	public AbstractSymbol(String name, Set<Condition> conditions) {
		this.name = name;
		this.conditions = new HashSet<>(conditions);
	}
	
	@Override
	public Set<Condition> getConditions() {
		return conditions;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public Symbol withCondition(Condition condition) {
		Set<Condition> conditions = new HashSet<>();
		return withConditions(conditions);
	}
	
}
