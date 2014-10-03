package org.jgll.disambiguation.conditions;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= Label !>> ":" 
 *
 * Label ::= [a-z]+ !>> [a-z]
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FollowRestrictionTest {
	
	private GLLParser parser;
	private Grammar grammar;
	
	@Before
	public void init() {
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal Label = Nonterminal.withName("Label");
		Range az = Range.in('a', 'z');

		Grammar.Builder builder = new Grammar.Builder();
		
		Rule r1 = new Rule(S, Label.builder().addCondition(RegularExpressionCondition.notFollow(Keyword.from(":"))).build());		
		Rule r2 = new Rule(Label, Plus.from(az).builder().addCondition(RegularExpressionCondition.notFollow(az)).build());
		builder.addRule(r1).addRule(r2);
		
		grammar = builder.build();
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("abc:");
		parser =  ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseError());
	}

}
