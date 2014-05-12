package org.jgll.disambiguation.conditions;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
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
		
		Nonterminal Id = new Nonterminal("Id");
		Range az = new Range('a', 'z');
		
		Keyword iff = new Keyword("if");
		Keyword when = new Keyword("when");
		Keyword doo = new Keyword("do");
		Keyword whilee = new Keyword("while");
		
		RegexAlt<Keyword> alt = new RegexAlt<>(iff, when, doo, whilee);
		Rule r1 = new Rule(Id, new RegexPlus(az).withCondition(RegularExpressionCondition.notFollow(az)).withCondition(RegularExpressionCondition.notMatch(alt)));
		
		grammar.addRule(r1);

		grammarGraph = grammar.toGrammarGraph();
	}
	
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();

	
	@Test
	public void testWhen() throws ParseError {
		Input input = Input.fromString("when");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		
		thrown.expect(ParseError.class);
		parser.parse(input, grammarGraph, "Id");
	}
	
	@Test
	public void testIf() throws ParseError {
		Input input = Input.fromString("if");		
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		
		thrown.expect(ParseError.class);
		parser.parse(input, grammarGraph, "Id");
	}
	
	@Test
	public void testDo() throws ParseError {
		Input input = Input.fromString("do");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);

		thrown.expect(ParseError.class);
		parser.parse(input, grammarGraph, "Id");
	}
	
	@Test
	public void testWhile() throws ParseError {
		Input input = Input.fromString("while");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);

		thrown.expect(ParseError.class);
		parser.parse(input, grammarGraph, "Id");
	}

}
