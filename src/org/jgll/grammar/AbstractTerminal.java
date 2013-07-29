package org.jgll.grammar;

import org.jgll.grammar.condition.Condition;

public abstract class AbstractTerminal implements Terminal {

	private static final long serialVersionUID = 1L;
	
	@Override
	public Iterable<Condition> getPreConditions() {
		return null;
	}
	
	@Override
	public Iterable<Condition> getPostConditions() {
		return null;
	}

}
