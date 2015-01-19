package org.jgll.regex;

import java.io.Serializable;
import java.util.Set;
import java.util.regex.Pattern;

import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.generator.ConstructorCode;
import org.jgll.grammar.symbol.Character;

public interface RegularExpression extends Serializable, Symbol, ConstructorCode {

	default Matcher getMatcher() {
		return getDFAMatcher();
	}
	
	public Automaton getAutomaton();
	
	public boolean isNullable();
	
	public Set<CharacterRange> getFirstSet();
	
	default Matcher getDFAMatcher() {
		return (input, i) -> getAutomaton().getRunnableAutomaton().match(input, i);
	}
	
	default Matcher getBackwardsMatcher() {
		return (input, i) -> getAutomaton().getRunnableAutomaton().matchBackwards(input, i);
	}
	
	default Matcher getJavaRegexMatcher() {
		return (input, i) -> {
								java.util.regex.Matcher matcher = getPattern().matcher("");
								matcher.find(i);
								return matcher.end();
							 };
	}	
	/**
	 * The set of characters (ranges) that cannot follow this regular expressions. 
	 */
	public Set<CharacterRange> getNotFollowSet();
	
	public Pattern getPattern();
	
	default boolean isSingleChar() {
		return false;
	}

	default Character asSingleChar() {
		return Character.from(0);
	}
}
