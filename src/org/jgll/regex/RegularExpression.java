package org.jgll.regex;

import java.io.Serializable;
import java.util.Set;

import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.generator.ConstructorCode;

public interface RegularExpression extends Serializable, Symbol, ConstructorCode {

	public Matcher getMatcher();
	
	public Matcher getBackwardsMatcher();
	
	public Automaton getAutomaton();
	
	public boolean isNullable();
	
	public Set<CharacterRange> getFirstSet();
	
	/**
	 * The set of characters (ranges) that cannot follow this regular expressions. 
	 */
	public Set<CharacterRange> getNotFollowSet();
	
}
