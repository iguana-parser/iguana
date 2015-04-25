package org.iguana.grammar.symbol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.iguana.grammar.condition.Condition;

public abstract class SymbolBuilder<T extends Symbol> {
	
	protected String name;
	
	protected String label;
	
	protected Object object;
	
	protected Set<Condition> preConditions = new HashSet<>();
	
	protected Set<Condition> postConditions = new HashSet<>();

	public SymbolBuilder(T symbol) {
		this.name = symbol.getName();
		this.label = symbol.getLabel();
		this.object = symbol.getObject();
		this.preConditions = new HashSet<>(symbol.getPreConditions());
		this.postConditions = new HashSet<>(symbol.getPostConditions());
	}
	
	public SymbolBuilder(String name) {
		this.name = name;
	}
	
	public SymbolBuilder<T> setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public SymbolBuilder<T> addPreCondition(Condition condition) {
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
 	
 	public SymbolBuilder<T> removePreConditions(Collection<Condition> conditions) {
 		preConditions.removeAll(conditions);
 		return this;
 	}
 	
 	public SymbolBuilder<T> removePostConditions(Collection<Condition> conditions) {
 		postConditions.removeAll(conditions);
 		return this;
 	} 	
 	
	public abstract T build();
	
}
