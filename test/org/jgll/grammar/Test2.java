package org.jgll.grammar;

import static java.util.Arrays.asList;
import junit.framework.Assert;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

// A ::= a
public class Test2 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule.Builder().head(new Nonterminal("A")).body(new Character('a')).build();
		return Grammar.fromRules("gamma1", asList(r1));
	}
	
	@Test
	public void test() {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a"), grammar, "A");
		Assert.assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode('a', 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 1);
		node1.addChild(node0);
		return node1;
	}

}