package org.jgll.grammar.conditions;

import org.jgll.grammar.AbstractGrammarTest;
import org.jgll.grammar.Character;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.Keyword;
import org.jgll.grammar.Nonterminal;
import org.jgll.grammar.Opt;
import org.jgll.grammar.Plus;
import org.jgll.grammar.Range;
import org.jgll.grammar.Rule;
import org.jgll.grammar.Terminal;
import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

import static org.jgll.grammar.condition.ConditionFactory.*;
import static org.junit.Assert.*;

/**
 * 
 * S ::= "for" L? Id
 *     | "forall"
 *     
 * Id ::= !<< [a-z]+ !>> [a-z]
 * 
 * L ::= " "
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedeRestrictionTest extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Nonterminal S = new Nonterminal("S");
		Keyword forr = new Keyword("for");
		Keyword forall = new Keyword("forall");
		Nonterminal L = new Nonterminal("L");
		Nonterminal Id = new Nonterminal("Id");
		Terminal ws = new Character(' ');
		Terminal az = new Range('a', 'z');
		
		GrammarBuilder builder = new GrammarBuilder();
		
		Rule r1 = new Rule(S, forr, new Opt(L), Id);
		
		Rule r2 = new Rule(S, forall);
		
		Rule r3 = new Rule(Id, new Plus(az).addCondition(notFollow(az)).addCondition(notPrecede(az)));
		
		Rule r4 = new Rule(L, ws);
		
		Iterable<Rule> rules = EBNFUtil.rewrite(list(r1, r2, r3, r4));
		builder.addRules(rules);
		
		return builder.build();
	}	
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("forall"), grammar, "S");
		assertTrue(sppf.deepEquals(getExpectedSPPF()));
	}
	
	private SPPFNode getExpectedSPPF() {
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 6);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("forall"), 0, 6);
		TerminalSymbolNode node3 = new TerminalSymbolNode(102, 0);
		TerminalSymbolNode node4 = new TerminalSymbolNode(111, 1);
		TerminalSymbolNode node5 = new TerminalSymbolNode(114, 2);
		TerminalSymbolNode node6 = new TerminalSymbolNode(97, 3);
		TerminalSymbolNode node7 = new TerminalSymbolNode(108, 4);
		TerminalSymbolNode node8 = new TerminalSymbolNode(108, 5);
		node2.addChild(node3);
		node2.addChild(node4);
		node2.addChild(node5);
		node2.addChild(node6);
		node2.addChild(node7);
		node2.addChild(node8);
		node1.addChild(node2);
		return node1;
	}
	
}
