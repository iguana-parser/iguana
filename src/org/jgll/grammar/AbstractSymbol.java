package org.jgll.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jgll.grammar.condition.Condition;

public abstract class AbstractSymbol implements Symbol {

	private static final long serialVersionUID = 1L;
	
	protected final List<Condition> conditions;
	
	public AbstractSymbol(Iterable<Condition> conditions) {
		this.conditions = new ArrayList<>();
		for(Condition condition : conditions) {
			this.conditions.add(condition);
		}
	}
	
	public AbstractSymbol() {
		this.conditions = new ArrayList<>();
	}
	
	@Override
	public Collection<Condition> getConditions() {
		return conditions;
	}
	
	@Override
	public Symbol addCondition(Condition condition) {
		return addConditions(Arrays.asList(condition));
	}

}
