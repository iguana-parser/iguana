package org.jgll.grammar.condition;

public enum ConditionType {
	
 	FOLLOW(" >> "),
	NOT_FOLLOW(" !>> "),
	PRECEDE(" << "),
	NOT_PRECEDE(" !<< "),
	MATCH(" & "),
	NOT_MATCH(" \\ ");
	
	private String symbol;
	
	private ConditionType(String symbol) {
		this.symbol = symbol;
	}
	
	@Override
	public String toString() {
		return symbol;
	}
	
}
