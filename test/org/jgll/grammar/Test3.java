package org.jgll.grammar;

import static org.junit.Assert.*;

import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * A ::= B C
 * B ::= 'b'
 * C ::= 'c'
 * 
 * @author Afroozeh
 *
 */
public class Test3 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule(new Nonterminal("A"), list(new Nonterminal("B"), new Nonterminal("C")));
		Rule r2 = new Rule(new Nonterminal("B"), list(new Character('b')));
		Rule r3 = new Rule(new Nonterminal("C"), list(new Character('c')));
		return new GrammarBuilder("test3").addRule(r1).addRule(r2).addRule(r3).build();
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("bc"), grammar, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode('b', 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1);
		node1.addChild(node0);
		TerminalSymbolNode node2 = new TerminalSymbolNode('c', 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("C"), 1, 2);
		node3.addChild(node2);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 2);
		node4.addChild(node1);
		node4.addChild(node3);
		
		return node4;
	}
	
}