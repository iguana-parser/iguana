package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;

public abstract class SymbolBuilder<T> {
	
	protected String label;
	
	protected Object object;
	
	protected Set<Condition> conditions = new HashSet<>();
	
	public SymbolBuilder<T> addLabel(String label) {
		this.label = label;
		return this;
	}
	
	public SymbolBuilder<T> addCondition(Condition condition) {
		conditions.add(condition);
		return this;
	}
	
	public SymbolBuilder<T> addObject(Object object) {
		this.object = object;
		return this;
	}
	
	public SymbolBuilder<T> addConditions(Iterable<Condition> conditions) {
		for (Condition condition : conditions) {
			this.conditions.add(condition);
		}
		return this;
	}
	
	public abstract T build();
	
}
