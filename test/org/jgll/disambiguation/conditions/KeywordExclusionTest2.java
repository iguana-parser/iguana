package org.jgll.disambiguation.conditions;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.RegexPlus;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * 
 * Id ::= [a-z]+ !>> [a-z] \ { "if", "when", "do", "while"}
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordExclusionTest2 {
	
	private GrammarGraph grammarGraph;
	private Grammar grammar;

	@Before
	public void init() {
		
		grammar = new Grammar();
		
		Nonterminal Id = Nonterminal.withName("Id");
		Range az = Range.in('a', 'z');
		
		Keyword iff = Keyword.from("if");
		Keyword when = Keyword.from("when");
		Keyword doo = Keyword.from("do");
		Keyword whilee = Keyword.from("while");
		
		RegexAlt<Keyword> alt = RegexAlt.from(iff, when, doo, whilee);
		Rule r1 = new Rule(Id, RegexPlus.from(az).builder().addCondition(RegularExpressionCondition.notFollow(az)).addCondition(RegularExpressionCondition.notMatch(alt)).build());
		
		grammar.addRule(r1);
		System.out.println(grammar);

		grammarGraph = grammar.toGrammarGraph();
	}
	
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();

	
	@Test
	public void testWhen() {
		Input input = Input.fromString("when");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "Id");
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testIf() {
		Input input = Input.fromString("if");		
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "Id");
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testDo() {
		Input input = Input.fromString("do");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "Id");
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testWhile() {
		Input input = Input.fromString("while");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "Id");
		assertTrue(result.isParseError());
	}

}
