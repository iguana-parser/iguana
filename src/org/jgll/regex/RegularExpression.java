package org.jgll.regex;

import java.io.Serializable;
import java.util.Set;

import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.generator.ConstructorCode;

public interface RegularExpression extends Serializable, Symbol, ConstructorCode {

	default Matcher getMatcher() {
		return getDFAMatcher();
	}
	
	public Matcher getDFAMatcher();
	
	public Matcher getBackwardsMatcher();
	
	public Matcher getJavaRegexMatcher();
	
	public Automaton getAutomaton();
	
	public boolean isNullable();
	
	public Set<CharacterRange> getFirstSet();
	
	/**
	 * The set of characters (ranges) that cannot follow this regular expressions. 
	 */
	public Set<CharacterRange> getNotFollowSet();
	
}
