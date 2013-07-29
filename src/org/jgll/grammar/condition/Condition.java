package org.jgll.grammar.condition;

import java.util.List;


public abstract class Condition {
	
	protected ConditionType type;

	public Condition (ConditionType type) {
		this.type = type;
	}
	
	public ConditionType getType() {
		return type;
	}
	
	protected static <T> String listToString(List<T> elements) {
		StringBuilder sb = new StringBuilder();
		for(T t : elements) {
			sb.append(t.toString()).append(" ");
		}
		return sb.toString();
	}
}