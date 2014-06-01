package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;

public abstract class SymbolBuilder<T extends Symbol> {
	
	protected String label;
	
	protected Object object;
	
	protected Set<Condition> conditions;
	
	public SymbolBuilder() {
		this.conditions = new HashSet<>();
	}
	
	public SymbolBuilder(T t) {
		this();
		this.label = t.getLabel();
		this.object = t.getObject();
		this.conditions = t.getConditions() == null ? new HashSet<Condition>() : new HashSet<>(t.getConditions());
	}
	
	public SymbolBuilder<T> setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public SymbolBuilder<T> addCondition(Condition condition) {
		conditions.add(condition);
		return this;
	}
	
	public SymbolBuilder<T> setObject(Object object) {
		this.object = object;
		return this;
	}
	
	public SymbolBuilder<T> removeConditions() {
		this.conditions.clear();
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
