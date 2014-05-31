package org.jgll.disambiguation.conditions;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.GrammarGraphBuilder;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Opt;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= "for" L? Id | "forall"
 * 
 * Id ::= "for" !<< [a-z]+ !>> [a-z]
 * 
 * L ::= " "
 * 
 * @author Ali Afroozeh
 * 
 */
public class PrecedeRestrictionTest2 {
	
	private GrammarGraph grammarGraph;
	private Grammar grammar;
	
	private Nonterminal S = Nonterminal.withName("S");
	private Keyword forr = Keyword.from("for");
	private Keyword forall = Keyword.from("forall");
	private Nonterminal L = Nonterminal.withName("L");
	private Nonterminal Id = Nonterminal.withName("Id");
	private Character ws = Character.from(' ');
	private Range az = Range.in('a', 'z');

	@Before
	public void createGrammar() {
		
		grammar = new Grammar();

		Rule r1 = new Rule(S, forr, Opt.from(L), Id);

		Rule r2 = new Rule(S, forall);

		Rule r3 = new Rule(Id, Plus.from(az).builder().addCondition(RegularExpressionCondition.notFollow(az)).addCondition(RegularExpressionCondition.notPrecede(forr)).build());

		Rule r4 = new Rule(L, ws);

		Iterable<Rule> rules = EBNFUtil.rewrite(list(r1, r2, r3, r4));
		grammar.addRules(rules);

		grammar.addRule(GrammarGraphBuilder.fromKeyword(forr));
		grammar.addRule(GrammarGraphBuilder.fromKeyword(forall));

		grammarGraph = grammar.toGrammarGraph();
	}

	@Test
	public void test() {
		Input input = Input.fromString("forall");
		GLLParser parser = ParserFactory.newParser(grammarGraph, input);
		ParseResult result = parser.parse(input, grammarGraph, "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getSPPFNode().deepEquals(getExpectedSPPF()));
	}

	private SPPFNode getExpectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammarGraph.getNonterminalId(S), 2, 0, 6);
		TokenSymbolNode node2 = new TokenSymbolNode(grammarGraph.getRegularExpressionId(forall), 0, 6);
		node1.addChild(node2);
		return node1;
	}

}
