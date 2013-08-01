package org.jgll.grammar.condition;



public abstract class Condition {
	
	protected ConditionType type;

	public Condition (ConditionType type) {
		this.type = type;
	}
	
	public ConditionType getType() {
		return type;
	}
	
}