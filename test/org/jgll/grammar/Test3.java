package org.jgll.grammar;

import static java.util.Arrays.asList;
import junit.framework.Assert;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.junit.Test;

// A ::= B C
// B ::= 'b'
// C ::= 'c'
public class Test3 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule.Builder().head(new Nonterminal("A")).body(new Nonterminal("B"), new Nonterminal("C")).build();
		Rule r2 = new Rule.Builder().head(new Nonterminal("B")).body(new Character('b')).build();
		Rule r3 = new Rule.Builder().head(new Nonterminal("C")).body(new Character('c')).build();
		return Grammar.fromRules("test3", asList(r1, r2, r3));
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse("bc", grammar, "A");
		generateGraph(sppf);
		Assert.assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode('a', 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 1);
		node1.addChild(node0);
		return node1;
	}

}