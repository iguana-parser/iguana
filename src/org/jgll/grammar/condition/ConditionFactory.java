package org.jgll.grammar.condition;

import java.util.Arrays;
import java.util.List;

import org.jgll.grammar.Keyword;
import org.jgll.grammar.Symbol;
import org.jgll.grammar.Terminal;

public class ConditionFactory {

	@SafeVarargs
	public static <T extends Symbol> Condition follow(T...symbols) {
		return createCondition(ConditionType.FOLLOW, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> Condition notFollow(T...symbols) {
		return createCondition(ConditionType.NOT_FOLLOW, symbols);
	}
	
	public static <T extends Symbol> Condition notFollow(List<T> symbols) {
		return createCondition(ConditionType.NOT_FOLLOW, symbols);
	}
	
	
	@SafeVarargs
	public static <T extends Symbol> Condition precede(T...symbols) {
		return createCondition(ConditionType.PRECEDE, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> Condition notPrecede(T...symbols) {
		return createCondition(ConditionType.NOT_PRECEDE, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> Condition match(T...symbols) {
		return createCondition(ConditionType.MATCH, symbols);
	}

	@SafeVarargs
	public static <T extends Symbol> Condition notMatch(T...symbols) {
		return createCondition(ConditionType.NOT_MATCH, symbols);
	}
	
	private static <T extends Symbol> boolean allTerminal(List<T> symbols) {
		for(T t : symbols) {
			if(! (t instanceof Terminal)) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Symbol> Condition createCondition(ConditionType type, List<T> symbols) {
		if(allKeywords(symbols)) {
			return new KeywordCondition(type, (List<Keyword>) symbols);
		} else if (allTerminal(symbols)) {
			return new TerminalCondition(type, (List<Terminal>) symbols);
		} else {
			return new ContextFreeCondition(type, symbols);
		}		
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Symbol> Condition createCondition(ConditionType type, T...symbols) {
		return createCondition(type, Arrays.asList(symbols));
	}
	
	private static <T extends Symbol> boolean allKeywords(List<T> symbols) {
		for(T t : symbols) {
			if(! (t instanceof Keyword)) {
				return false;
			}
		}
		return true;		
	}
	
}
