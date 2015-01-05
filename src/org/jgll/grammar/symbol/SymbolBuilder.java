package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;

public abstract class SymbolBuilder<T extends Symbol> {
	
	protected String name;
	
	protected String label;
	
	protected Object object;
	
	protected Set<Condition> preConditions;
	
	protected Set<Condition> postConditions;
	
	public SymbolBuilder(String name) {
		this.name = name;
		this.preConditions = new HashSet<>();
		this.postConditions = new HashSet<>();
	}
	
	public SymbolBuilder(T t) {
		this(t.getName());
		this.label = t.getLabel();
		this.object = t.getObject();
		this.preConditions = t.getPreConditions() == null ? new HashSet<Condition>() : new HashSet<>(t.getPreConditions());
		this.postConditions = t.getPostConditions() == null ? new HashSet<Condition>() : new HashSet<>(t.getPostConditions());
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
