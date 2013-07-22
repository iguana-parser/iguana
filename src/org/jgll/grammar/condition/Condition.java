package org.jgll.grammar.condition;

import java.util.List;

import org.jgll.grammar.Symbol;

public class Condition {
	
	private List<? extends Symbol> symbols;
	
	private ConditionType type;

	public Condition(List<? extends Symbol> symbols, ConditionType type) {
		this.symbols = symbols;
		this.type = type;
	}
	public List<? extends Symbol> getSymbols() {
		return symbols;
	}
	
	public ConditionType getType() {
		return type;
	}
	
}