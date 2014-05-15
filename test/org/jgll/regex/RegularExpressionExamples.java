package org.jgll.regex;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Range;

public class RegularExpressionExamples {

	/**
	 * Id ::= [a-zA-Z][a-zA-Z0-9]*
	 */
	public static RegularExpression getId() {
		CharacterClass c1 = CharacterClass.from(Range.in('a', 'z'), Range.in('A', 'Z'));
		CharacterClass c2 = CharacterClass.from(Range.in('a', 'z'), Range.in('A', 'Z'), Range.in('0', '9'));
		return Sequence.from(c1, RegexStar.from(c2));
	}
	
	/**
	 * Float ::= [0-9]+[.][0-9]+
	 */
	public static RegularExpression getFloat() {
		CharacterClass c = CharacterClass.from(Range.in('0', '9'));
		return Sequence.from(RegexPlus.from(c), Character.from('.'), RegexPlus.from(c));
	}
	
	/**
	 * UnicodeEscape ::= "\\" [u]+ [0-9 A-F a-f] [0-9 A-F a-f] [0-9 A-F a-f] [0-9 A-F a-f];
	 */
	public static RegularExpression getJavaUnicodeEscape() {
		List<RegularExpression> regularExpressions = new ArrayList<>();

		regularExpressions.add(Keyword.from("\\"));
		
		regularExpressions.add(RegexPlus.from(Character.from('u')));
		
		CharacterClass c = CharacterClass.from(Range.in('0', '9'), Range.in('a', 'z'), Range.in('A', 'Z'));
		regularExpressions.add(c);
		regularExpressions.add(c);
		regularExpressions.add(c);
		regularExpressions.add(c);
		
		return Sequence.from(regularExpressions);
	}
	
	/**
	 * Character ::= ['] ![']+ ['] 
	 * 
	 * @return
	 */
	public static RegularExpression getCharacter() {
		List<RegularExpression> regularExpressions = new ArrayList<>();
		regularExpressions.add(Character.from('\''));
		regularExpressions.add(RegexPlus.from(Character.from('\'').not()));
		regularExpressions.add(Character.from('\''));
		return Sequence.from(regularExpressions);
	}
	
	/**
	 * StringPart ::= !["\\]+ | "\n"
	 */
	public static RegularExpression getStringPart() {
		Character c1 = Character.from('"');
		Character c2 = Character.from('\\');
		CharacterClass c = CharacterClass.fromChars(c1, c2);
		Keyword newline = Keyword.from("\\n");

		return RegexAlt.from(RegexPlus.from(c.not()), newline);
	}
	
	// "/*" (![*] | [*] !>> [/])* "*/"
	public static RegularExpression getMultilineComment() {
		
		Keyword r1 = Keyword.from("/*");
		Character star = Character.from('*');
		Character slash = Character.from('/');

		RegularExpression r2 = RegexStar.from(Sequence.from(star, slash));
		
		Keyword r3 = Keyword.from("*/");
		
		return Sequence.from(r1, r2, r3);
	}
	 
}
