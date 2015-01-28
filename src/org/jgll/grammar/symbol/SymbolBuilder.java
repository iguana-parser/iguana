package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;

public abstract class SymbolBuilder<T extends Symbol> {
	
	protected String name;
	
	protected String label;
	
	protected Object object;
	
	protected Set<Condition> preConditions = new HashSet<>();
	
	protected Set<Condition> postConditions = new HashSet<>();

	public SymbolBuilder() {}
	
	public SymbolBuilder(String name) {
		this.name = name;
	}
	
	public SymbolBuilder<T> setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public <X extends Symbol> SymbolBuilder<T> addPreCondition(Condition condition) {
		preConditions.add(condition);
		return this;
	}
	
	public SymbolBuilder<T> addPostCondition(Condition condition) {
		postConditions.add(condition);
		return this;
	}	
	
	public SymbolBuilder<T> setObject(Object object) {
		this.object = object;
		return this;
	}
	
	public SymbolBuilder<T> addConditions(Symbol s) {
		addPreConditions(s.getPreConditions());
		addPostConditions(s.getPostConditions());
		return this;
	}
	
 	public SymbolBuilder<T> addPreConditions(Iterable<Condition> conditions) {
 		conditions.forEach(c -> preConditions.add(c));
		return this;
	}
 	
 	public SymbolBuilder<T> addPostConditions(Iterable<Condition> conditions) {
 		conditions.forEach(c -> postConditions.add(c));
		return this;
	}
	
	public abstract T build();
	
}
