package org.jgll.grammar.condition;

import java.util.List;

import org.jgll.grammar.Symbol;

public class ContextFreeCondition extends Condition {
	
	private List<? extends Symbol> symbols;

	public ContextFreeCondition(ConditionType type, List<? extends Symbol> symbols) {
		super(type);
		this.symbols = symbols;
	}
	
	public List<? extends Symbol> getSymbols() {
		return symbols;
	}
}
