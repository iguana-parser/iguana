package org.jgll.grammar;

import junit.framework.Assert;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

// A ::= epsilon
public class Test1 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule(new Nonterminal("A"), emptyList());
		return new GrammarBuilder("epsilon").addRule(r1).build();
	}
	
	@Test
	public void test() {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString(""), grammar, "A");
		Assert.assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode(-2, 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 0);
		node1.addChild(node0);
		return node1;
	}

}