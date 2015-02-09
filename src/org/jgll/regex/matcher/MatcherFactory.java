package org.jgll.regex.matcher;

import java.util.List;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.Sequence;

public class MatcherFactory {
	
	public static Matcher getMatcher(RegularExpression regex) {
		if (regex instanceof Sequence<?>)
			return sequenceMatcher((Sequence<?>) regex);
			
		if (regex instanceof Character)
			return characterMatcher((Character) regex);
		
		if (regex instanceof CharacterRange)
			return characterRangeMatcher((CharacterRange) regex);
			
		return new DFAMatcher(regex.getAutomaton());
	}
	
	public static Matcher getBackwardsMatcher(RegularExpression regex) {
		return null;
	}

	private static Matcher sequenceMatcher(Sequence<?> seq) {
		if (seq.isCharSequence()) {
			List<Character> characters = seq.asCharacters();
			return (input, i) -> {
				for (Character c : characters) {
					if (c.getValue() != input.charAt(i++)) {
						return -1;
					}
				}
				return characters.size();
			};
		}
		return getMatcher(seq);
	}
	
	private static Matcher characterMatcher(Character c) {
		return (input, i) -> input.charAt(i) == c.getValue() ? 1 : -1;
	}
	
	private static Matcher characterRangeMatcher(CharacterRange range) {
		return (input, i) -> input.charAt(i) >= range.getStart() && input.charAt(i) <= range.getEnd() ? 1 : -1;
	}
	
}
