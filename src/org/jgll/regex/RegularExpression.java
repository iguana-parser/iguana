package org.jgll.regex;

import java.io.Serializable;
import java.util.Set;
import java.util.regex.Pattern;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.DFAMatcher;
import org.jgll.util.IntArrayCharSequence;
import org.jgll.util.generator.ConstructorCode;

public interface RegularExpression extends Serializable, Symbol, ConstructorCode {

	default Matcher getMatcher() {
		return getDFAMatcher();
	}
	
	public Automaton getAutomaton();
	
	public boolean isNullable();
	
	public Set<CharacterRange> getFirstSet();
	
	default Matcher getDFAMatcher() {
		return new DFAMatcher(getAutomaton());
	}
	
	default Matcher getBackwardsMatcher() {
		throw new UnsupportedOperationException();
	}
	
	default Matcher getJavaRegexMatcher() {
		Pattern pattern = Pattern.compile(getPattern());
		java.util.regex.Matcher matcher = pattern.matcher("");
		
		return (input, i) -> {
			                    IntArrayCharSequence charSeq = input.asCharSequence();
								matcher.reset(charSeq);
								if (matcher.find(i)) {
									int end = i + matcher.end();
									return charSeq.logicalIndexAt(end - 1);									
								}
								return -1;
							 };
	}
	
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
}
