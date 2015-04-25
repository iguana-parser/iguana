package org.iguana.grammar.condition;

public enum ConditionType {
	
 	FOLLOW(" >> "),
	NOT_FOLLOW(" !>> "),
	FOLLOW_IGNORE_LAYOUT(" >>> "),
	NOT_FOLLOW_IGNORE_LAYOUT(" !>>> "),
	PRECEDE(" << "),
	NOT_PRECEDE(" !<< "),
	MATCH(" & "),
	NOT_MATCH(" \\ "),
	END_OF_LINE("$"),
	START_OF_LINE("^"),
	END_OF_FILE("$$"),
	
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
