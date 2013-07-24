package org.jgll.grammar.condition;

import java.util.Arrays;

import org.jgll.grammar.CharacterClass;
import org.jgll.grammar.Symbol;

public class ConditionFactory {

	@SafeVarargs
	public static <T extends Symbol> Condition follow(T...symbols) {
		return new ContextFreeCondition(ConditionType.FOLLOW, Arrays.asList(symbols));
	}
	
	public static Condition follow(int[]...strings) {
		return new LiteralCondition(ConditionType.FOLLOW, Arrays.asList(strings));
	}

	@SafeVarargs
	public static <T extends Symbol> Condition notFollow(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_FOLLOW, Arrays.asList(symbols));
	}
	
	public static Condition notFollow(int[]...strings) {
		return new LiteralCondition(ConditionType.NOT_FOLLOW, Arrays.asList(strings));
	}
	
	public static Condition notFollow(CharacterClass...characterClasses) {
		return new CharacterClassCondition(ConditionType.NOT_FOLLOW, Arrays.asList(characterClasses));
	}
	
	@SafeVarargs
	public static <T extends Symbol> Condition precede(T...symbols) {
		return new ContextFreeCondition(ConditionType.PRECEDE, Arrays.asList(symbols));
	}
	
	@SafeVarargs
	public static <T extends Symbol> Condition notPrecede(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_PRECEDE, Arrays.asList(symbols));
	}

	@SafeVarargs
	public static <T extends Symbol> Condition match(T...symbols) {
		return new ContextFreeCondition(ConditionType.MATCH, Arrays.asList(symbols));
	}

	@SafeVarargs
	public static <T extends Symbol> Condition notMatch(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_MATCH, Arrays.asList(symbols));
	}
	
}
