package org.iguana.regex;

import java.io.Serializable;
import java.util.Set;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.matcher.Matcher;
import org.iguana.util.generator.ConstructorCode;

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
	
	public void initMatcher();
}
