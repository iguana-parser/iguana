package org.jgll.grammar;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.condition.Condition;

public abstract class AbstractTerminal implements Terminal {

	private static final long serialVersionUID = 1L;
	
	private List<Condition> conditions;
	
	public AbstractTerminal() {
		conditions = new ArrayList<>();
	}

	@Override
	public Terminal addCondition(Condition condition) {
		conditions.add(condition);
		return this;
	}
	
	@Override
	public Iterable<Condition> getConditions() {
		return conditions;
	}

}
