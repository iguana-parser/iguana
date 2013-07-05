package org.jgll.grammar;

import java.util.List;

public class Condition {
	
	private ConditionType type;
	private List<Symbol> symbols;

	public Condition(ConditionType type, List<Symbol> symbols) {
		this.type = type;
		this.symbols = symbols;
	}
	
	public static enum ConditionType {
		AND,
		OR;
	}
	
}