package org.jgll.disambiguation.conditions;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.RegexAlt;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * Id ::= [a-z]+ !>> [a-z] \ { "if", "when", "do", "while"}
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordExclusionTest2 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal Id = Nonterminal.withName("Id");
		CharacterRange az = CharacterRange.in('a', 'z');
		
		Keyword iff = Keyword.from("if");
		Keyword when = Keyword.from("when");
		Keyword doo = Keyword.from("do");
		Keyword whilee = Keyword.from("while");
		RegexAlt<Keyword> alt = RegexAlt.from(iff, when, doo, whilee);		
		Plus AZPlus = Plus.builder(az).addPostCondition(RegularExpressionCondition.notFollow(az))
									  .addPostCondition(RegularExpressionCondition.notMatch(alt)).build();
		
		Rule r1 = Rule.builder(Id).addSymbol(AZPlus).build();
		
		grammar = Grammar.builder().addRule(r1).build();
	}
	
	@Test
	public void testWhen() {
		Input input = Input.fromString("when");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "Id");
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testIf() {
		Input input = Input.fromString("if");		
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "Id");
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testDo() {
		Input input = Input.fromString("do");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "Id");
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testWhile() {
		Input input = Input.fromString("while");
		GLLParser parser = ParserFactory.newParser();
		ParseResult result = parser.parse(input, grammar, "Id");
		assertTrue(result.isParseError());
	}

}
