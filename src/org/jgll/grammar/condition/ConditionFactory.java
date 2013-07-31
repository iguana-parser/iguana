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
	
	@SafeVarargs
	private static <T extends Symbol> boolean allCharacterClass(T...symbols) {
		for(T t : symbols) {
			if(! (t instanceof Terminal)) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Symbol> Condition createCondition(ConditionType type, T...symbols) {
		if(allKeywords(symbols)) {
			return new KeywordCondition(type, (List<Keyword>) Arrays.asList(symbols));
		} else if (allCharacterClass(symbols)) {
			return new TerminalCondition(type, (List<Terminal>) Arrays.asList(symbols));
		} else {
			return new ContextFreeCondition(type, Arrays.asList(symbols));
		}
	}
	
	@SafeVarargs
	private static <T extends Symbol> boolean allKeywords(T...symbols) {
		for(T t : symbols) {
			if(! (t instanceof Keyword)) {
				return false;
			}
		}
		return true;		
	}
	
}
