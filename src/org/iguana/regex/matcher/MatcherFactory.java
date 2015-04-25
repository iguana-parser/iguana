package org.iguana.regex.matcher;

import java.util.List;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;

/**
 * 
 * @author Al Afroozeh
 *
 */
public class MatcherFactory {
	
	public static Matcher getMatcher(RegularExpression regex) {
		
		if (regex instanceof Terminal)
			return getMatcher(((Terminal) regex).getRegularExpression());
		
		if (regex instanceof Sequence<?>)
			return sequenceMatcher((Sequence<?>) regex);
			
		if (regex instanceof Character)
			return characterMatcher((Character) regex);
		
		if (regex instanceof CharacterRange)
			return characterRangeMatcher((CharacterRange) regex);
			
		return createMatcher(regex);
	}
	
	public static Matcher getBackwardsMatcher(RegularExpression regex) {
		
		if (regex instanceof Terminal)
			return getBackwardsMatcher(((Terminal) regex).getRegularExpression());
		
		if (regex instanceof Sequence<?>)
			return sequenceBackwardsMatcher((Sequence<?>) regex);
		
		if (regex instanceof Character)
			return characterBackwardsMatcher((Character) regex);
		
		if (regex instanceof CharacterRange)
			return characterRangeBackwardsMatcher((CharacterRange) regex);
		
		return createBackwardsMatcher(regex);
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
		return createMatcher(seq);
	}
	
	private static Matcher sequenceBackwardsMatcher(Sequence<?> seq) {
		if (seq.isCharSequence()) {
			List<Character> characters = seq.asCharacters();
			return (input, i) -> {
				if (i == 0) return -1;
				--i;
				for (Character c : characters) {
					if (c.getValue() != input.charAt(i--)) {
						return -1;
					}
				}
				return characters.size();
			};
		}
		return createBackwardsMatcher(seq);
	}
	
	private static Matcher characterMatcher(Character c) {
		return (input, i) -> input.charAt(i) == c.getValue() ? 1 : -1;
	}
	
	private static Matcher characterBackwardsMatcher(Character c) {
		return (input, i) ->  i == 0 ? -1 : ( input.charAt(i - 1) == c.getValue() ? 1 : -1 );
	}
	
	private static Matcher characterRangeMatcher(CharacterRange range) {
		return (input, i) -> input.charAt(i) >= range.getStart() && input.charAt(i) <= range.getEnd() ? 1 : -1;
	}
	
	private static Matcher characterRangeBackwardsMatcher(CharacterRange range) {
		return (input, i) -> i == 0 ? -1 : ( input.charAt(i - 1) >= range.getStart() && input.charAt(i - 1) <= range.getEnd() ? 1 : -1 );
	}
	
	private static Matcher createMatcher(RegularExpression regex) {
		return new DFAMatcher(regex.getAutomaton());
	}
	
	private static Matcher createBackwardsMatcher(RegularExpression regex) {
		return new DFABackwardsMatcher(regex.getAutomaton());
	}
	
}
