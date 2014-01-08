package org.jgll.regex;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;

public class RegularExpressionExamples {

	/**
	 * Id ::= [a-zA-Z][a-zA-Z0-9]*
	 */
	public static RegularExpression getId() {
		CharacterClass c1 = new CharacterClass(new Range('a', 'z'), new Range('A', 'Z'));
		CharacterClass c2 = new CharacterClass(new Range('a', 'z'), new Range('A', 'Z'), new Range('0', '9'));
		RegularExpression regexp = new Sequence<>(c1, new RegexStar(c2));
		return regexp;
	}
	
	/**
	 * Float ::= [0-9]+[.][0-9]+
	 */
	public static RegularExpression getFloat() {
		CharacterClass c = new CharacterClass(new Range('0', '9'));
		return new Sequence<>(new RegexPlus(c), new Character('.'), new RegexPlus(c));
	}
	
}
