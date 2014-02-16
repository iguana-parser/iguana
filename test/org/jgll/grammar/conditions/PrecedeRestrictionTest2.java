package org.jgll.grammar.conditions;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
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
import org.jgll.parser.ParseError;
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
	
	private Grammar grammar;

	@Before
	public void createGrammar() {
		Nonterminal S = new Nonterminal("S");
		Keyword forr = new Keyword("for", new int[] { 'f', 'o', 'r' });
		Keyword forall = new Keyword("forall", new int[] { 'f', 'o', 'r', 'a', 'l', 'l' });
		Nonterminal L = new Nonterminal("L");
		Nonterminal Id = new Nonterminal("Id");
		Character ws = new Character(' ');
		Range az = new Range('a', 'z');

		GrammarBuilder builder = new GrammarBuilder();

		Rule r1 = new Rule(S, forr, new Opt(L), Id);

		Rule r2 = new Rule(S, forall);

		Rule r3 = new Rule(Id, new Plus(az).addCondition(RegularExpressionCondition.notFollow(az)).addCondition(RegularExpressionCondition.notPrecede(forr)));

		Rule r4 = new Rule(L, ws);

		Iterable<Rule> rules = EBNFUtil.rewrite(list(r1, r2, r3, r4));
		builder.addRules(rules);

		builder.addRule(GrammarBuilder.fromKeyword(forr));
		builder.addRule(GrammarBuilder.fromKeyword(forall));

		grammar = builder.build();
	}

	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("forall");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");
		assertTrue(sppf.deepEquals(getExpectedSPPF()));
	}

	private SPPFNode getExpectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 6);
		TokenSymbolNode node2 = new TokenSymbolNode(5, 0, 6);
		node1.addChild(node2);
		return node1;
	}

}
