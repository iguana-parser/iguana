package org.jgll.grammar.condition;

import java.util.List;

import org.jgll.grammar.Symbol;

public class ConditionFactory {

	public static Condition follow(List<? extends Symbol> symbols) {
		return new Condition(symbols, ConditionType.FOLLOW);
	}

	public static Condition notFollow(List<? extends Symbol> symbols) {
		return new Condition(symbols, ConditionType.NOT_FOLLOW);
	}
	
	public static Condition precede(List<? extends Symbol> symbols) {
		return new Condition(symbols, ConditionType.PRECEDE);
	}
	
	public static Condition notPrecede(List<? extends Symbol> symbols) {
		return new Condition(symbols, ConditionType.NOT_PRECEDE);
	}

	public static Condition match(List<? extends Symbol> symbols) {
		return new Condition(symbols, ConditionType.MATCH);
	}

	public static Condition notMatch(List<? extends Symbol> symbols) {
		return new Condition(symbols, ConditionType.NOT_MATCH);
	}
	
}
