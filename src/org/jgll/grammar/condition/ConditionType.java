package org.jgll.grammar.condition;

public enum ConditionType {
	
 	FOLLOW(" >> "),
	NOT_FOLLOW(" !>> "),
	NOT_FOLLOW_IGNORE_LAYOUT(" !>>> "),
	PRECEDE(" << "),
	NOT_PRECEDE(" !<< "),
	MATCH(" & "),
	NOT_MATCH(" \\ "),
	END_OF_LINE("$"),
	START_OF_LINE("^"),
	
	DATA_DEPENDENT(" ? ")
	;
	
	private String symbol;
	
	private ConditionType(String symbol) {
		this.symbol = symbol;
	}
	
	@Override
	public String toString() {
		return symbol;
	}
	
}
