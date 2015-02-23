package org.jgll.regex;

import java.io.Serializable;
import java.util.Set;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.matcher.Matcher;
import org.jgll.util.generator.ConstructorCode;

public interface RegularExpression extends Serializable, Symbol, ConstructorCode {

	public Automaton getAutomaton();
	
	public boolean isNullable();
	
	public Set<CharacterRange> getFirstSet();
	
	/**
	 * The set of characters (ranges) that cannot follow this regular expressions. 
	 */
	public Set<CharacterRange> getNotFollowSet();
	
	public String getPattern();
	
	default boolean isSingleChar() {
		return false;
	}

	default Character asSingleChar() {
		return Character.from(0);
	}
	
	public Matcher getMatcher();
	
	public Matcher getBackwardsMatcher();
}
