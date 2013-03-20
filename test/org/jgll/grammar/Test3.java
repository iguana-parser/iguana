package org.jgll.grammar;

import static java.util.Arrays.asList;
import junit.framework.Assert;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
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
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("bc"), grammar, "A");
		Assert.assertEquals(true, sppf.deepEquals(expectedSPPF()));
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