package org.iguana.disambiguation.conditions;

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Alt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * Id ::= [a-z]+ !>> [a-z] \ { "if", "when", "do", "while"}
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordExclusionTest {
	
	private Grammar grammar;

	@Before
	public void init() {
		Nonterminal Id = Nonterminal.withName("Id");
		CharacterRange az = CharacterRange.in('a', 'z');
		
		Sequence<Character> iff = Sequence.from("if");
		Sequence<Character> when = Sequence.from("when");
		Sequence<Character> doo = Sequence.from("do");
		Sequence<Character> whilee = Sequence.from("while");
		Alt<?> alt = Alt.from(iff, when, doo, whilee);
		Plus AZPlus = Plus.builder(az).addPostCondition(RegularExpressionCondition.notFollow(az)).addPostCondition(RegularExpressionCondition.notMatch(alt)).build();
		
		Rule r1 = Rule.withHead(Id).addSymbol(AZPlus).build();
		grammar = Grammar.builder().addRule(r1).build();
	}
	
	@Test
	public void testWhen() {
		Input input = Input.fromString("when");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("Id"));
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testIf() {
		Input input = Input.fromString("if");		
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("Id"));
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testDo() {
		Input input = Input.fromString("do");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("Id"));
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testWhile() {
		Input input = Input.fromString("while");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("Id"));
		assertTrue(result.isParseError());
	}

}
