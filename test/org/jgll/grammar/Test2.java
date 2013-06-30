package org.jgll.grammar;

import static org.junit.Assert.*;

import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * A ::= 'a'
 */
public class Test2 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule(new Nonterminal("A"), list(new Character('a')));
		return new GrammarBuilder("a").addRule(r1).build();
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("a"), grammar, "A");
		assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode('a', 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 1);
		node1.addChild(node0);
		return node1;
	}

}