package org.jgll.disambiguation.conditions;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.ebnf.EBNFUtil;
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
	
	private GrammarGraph grammarGraph;
	private Grammar grammar;
	private GLLParser parser;

	
	@Before
	public void init() {
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal Label = Nonterminal.withName("Label");
		Range az = Range.in('a', 'z');

		grammar = new Grammar();
		
		Rule r1 = new Rule(S, Label.builder().addCondition(RegularExpressionCondition.notFollow(Keyword.from(":"))).build());
		
		Rule r2 = new Rule(Label, Plus.from(az).builder().addCondition(RegularExpressionCondition.notFollow(az)).build());
		
		Iterable<Rule> rules = EBNFUtil.rewrite(list(r1, r2));
		grammar.addRules(rules);

		grammarGraph = grammar.toGrammarGraph();
	}
	
	@Test
	public void testParser() {
		Input input = Input.fromString("abc:");
		parser =  ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "S");
		assertTrue(result.isParseError());
	}

}
