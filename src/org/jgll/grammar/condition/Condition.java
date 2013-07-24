package org.jgll.grammar.condition;


public abstract class Condition {
	
	private ConditionType type;

	public Condition (ConditionType type) {
		this.type = type;
	}
	
	public ConditionType getType() {
		return type;
	}
}