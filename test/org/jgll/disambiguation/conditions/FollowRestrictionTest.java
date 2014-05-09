package org.jgll.disambiguation.conditions;

import static org.jgll.util.CollectionsUtil.*;

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
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
		Nonterminal S = new Nonterminal("S");
		Nonterminal Label = new Nonterminal("Label");
		Range az = new Range('a', 'z');

		grammar = new Grammar();
		
		Rule r1 = new Rule(S, Label.withCondition(RegularExpressionCondition.notFollow(new Keyword(":", new int[] {':'}))));
		
		Rule r2 = new Rule(Label, new Plus(az).withCondition(RegularExpressionCondition.notFollow(az)));
		
		Iterable<Rule> rules = EBNFUtil.rewrite(list(r1, r2));
		grammar.addRules(rules);

		grammarGraph = grammar.toGrammarGraph();
	}
	
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testParser() throws Exception {
		Input input = Input.fromString("abc:");
		parser =  ParserFactory.newParser(grammarGraph, input);
		thrown.expect(ParseError.class);
		parser.parse(input, grammarGraph, "S");
	}

}
