package org.jgll.grammar.condition;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.CharacterClass;
import org.jgll.grammar.Symbol;

public class ConditionFactory {

	@SafeVarargs
	public static <T extends Symbol> Condition follow(T...symbols) {
		return new ContextFreeCondition(ConditionType.FOLLOW, get(symbols));
	}
	
	public static Condition follow(int[]...strings) {
		return new LiteralCondition(ConditionType.FOLLOW, get(strings));
	}

	@SafeVarargs
	public static <T extends Symbol> Condition notFollow(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_FOLLOW, get(symbols));
	}
	
	public static Condition notFollow(int[]...strings) {
		return new LiteralCondition(ConditionType.NOT_FOLLOW, get(strings));
	}
	
	public static Condition notFollow(CharacterClass...characterClasses) {
		return new CharacterClassCondition(ConditionType.NOT_FOLLOW, get(characterClasses));
	}
	
	@SafeVarargs
	public static <T extends Symbol> Condition precede(T...symbols) {
		return new ContextFreeCondition(ConditionType.PRECEDE, get(symbols));
	}
	
	@SafeVarargs
	public static <T extends Symbol> Condition notPrecede(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_PRECEDE, get(symbols));
	}

	@SafeVarargs
	public static <T extends Symbol> Condition match(T...symbols) {
		return new ContextFreeCondition(ConditionType.MATCH, get(symbols));
	}

	@SafeVarargs
	public static <T extends Symbol> Condition notMatch(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_MATCH, get(symbols));
	}
	
	@SafeVarargs
	private static <T> List<T> get(T...array) {
		List<T> list = new ArrayList<>();
		for(T t : array) {
			list.add(t);
		}
		return list;
	}
	
}
