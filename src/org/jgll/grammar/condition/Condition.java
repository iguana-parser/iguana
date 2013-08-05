package org.jgll.grammar.condition;

import java.io.Serializable;



public abstract class Condition implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected ConditionType type;

	public Condition (ConditionType type) {
		this.type = type;
	}
	
	public ConditionType getType() {
		return type;
	}
	
}