package org.jgll.regex;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Range;

public class RegularExpressionExamples {

	/**
	 * Id ::= [a-zA-Z][a-zA-Z0-9]*
	 */
	public static RegularExpression getId() {
		CharacterClass c1 = new CharacterClass(new Range('a', 'z'), new Range('A', 'Z'));
		CharacterClass c2 = new CharacterClass(new Range('a', 'z'), new Range('A', 'Z'), new Range('0', '9'));
		return new Sequence<>(c1, new RegexStar(c2));
	}
	
	/**
	 * Float ::= [0-9]+[.][0-9]+
	 */
	public static RegularExpression getFloat() {
		CharacterClass c = new CharacterClass(new Range('0', '9'));
		return new Sequence<>(new RegexPlus(c), new Character('.'), new RegexPlus(c));
	}
	
	/**
	 * UnicodeEscape ::= "\\" [u]+ [0-9 A-F a-f] [0-9 A-F a-f] [0-9 A-F a-f] [0-9 A-F a-f];
	 */
	public static RegularExpression getJavaUnicodeEscape() {
		List<RegularExpression> regularExpressions = new ArrayList<>();

		regularExpressions.add(new Keyword("\\"));
		
		regularExpressions.add(new RegexPlus(Character.from('u')));
		
		CharacterClass c = new CharacterClass(new Range('0', '9'), new Range('a', 'z'), new Range('A', 'Z'));
		regularExpressions.add(c);
		regularExpressions.add(c);
		regularExpressions.add(c);
		regularExpressions.add(c);
		
		return new Sequence<>(regularExpressions);
	}
	
	/**
	 * Character ::= ['] ![']+ ['] 
	 * 
	 * @return
	 */
	public static RegularExpression getCharacter() {
		List<RegularExpression> regularExpressions = new ArrayList<>();
		regularExpressions.add(Character.from('\''));
		regularExpressions.add(new RegexPlus(Character.from('\'').not()));
		regularExpressions.add(Character.from('\''));
		return new Sequence<>(regularExpressions);
	}
	
	/**
	 * StringPart ::= !["\\]+ | "\n"
	 */
	public static RegularExpression getStringPart() {
		Character c1 = Character.from('"');
		Character c2 = Character.from('\\');
		CharacterClass c = CharacterClass.fromChars(c1, c2);
		Keyword newline = new Keyword("\\n");

		return new RegexAlt<>(new RegexPlus(c.not()), newline);
	}
	
	// "/*" (![*] | [*] !>> [/])* "*/"
	public static RegularExpression getMultilineComment() {
		
		Keyword r1 = new Keyword("/*");
		Character star = new Character('*');
		Character slash = new Character('/');

		RegularExpression r2 = new RegexStar(new RegexAlt<>(new Sequence<>(star, slash)));
		
		Keyword r3 = new Keyword("*/");
		
		return new Sequence<>(r2, r3);
	}
	 
}
