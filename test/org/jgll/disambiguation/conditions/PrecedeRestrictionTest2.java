package org.jgll.disambiguation.conditions;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraphBuilder;
import org.jgll.grammar.condition.RegularExpressionCondition;
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
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
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
	
	private Nonterminal S = Nonterminal.withName("S");
	private Keyword forr = Keyword.from("for");
	private Keyword forall = Keyword.from("forall");
	private Nonterminal L = Nonterminal.withName("L");
	private Nonterminal Id = Nonterminal.withName("Id");
	private Character ws = Character.from(' ');
	private Range az = Range.in('a', 'z');
	private Grammar grammar;

	@Before
	public void createGrammar() {
		
		Grammar.Builder builder = new Grammar.Builder();

		Rule r1 = new Rule(S, forr, Opt.from(L), Id);

		Rule r2 = new Rule(S, forall);

		Rule r3 = new Rule(Id, Plus.from(az).builder().addCondition(RegularExpressionCondition.notFollow(az)).addCondition(RegularExpressionCondition.notPrecede(forr)).build());

		Rule r4 = new Rule(L, ws);
		
		builder.addRule(r1);
		builder.addRule(r2);
		builder.addRule(r3);
		builder.addRule(r4);
		builder.addRule(GrammarGraphBuilder.fromKeyword(forr));
		builder.addRule(GrammarGraphBuilder.fromKeyword(forall));

		grammar = builder.build();
	}

	@Test
	public void test() {
		Input input = Input.fromString("forall");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF()));
	}

	private SPPFNode getExpectedSPPF() {
		SPPFNodeFactory factory = new SPPFNodeFactory(grammar.toGrammarGraph());
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 6).init();
		PackedNode node2 = factory.createPackedNode("S ::= f o r a l l .", 0, node1);
		TerminalNode node3 = factory.createTokenNode("f o r a l l", 0, 6);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}

}
