package org.jgll.grammar;

import static java.util.Arrays.asList;
import junit.framework.Assert;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.ToJavaCode;
import org.junit.Test;

// A ::= epsilon
public class Test1 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule r1 = new Rule.Builder().head(new Nonterminal("A")).body().build();
		return Grammar.fromRules("gamma1", asList(r1));
	}
	
	@Test
	public void test() {
		NonterminalSymbolNode sppf = rdParser.parse("", grammar, "A");
		ToJavaCode toJavaCode = new ToJavaCode();
		sppf.accept(toJavaCode);
		System.out.println(toJavaCode.getString());
		Assert.assertEquals(true, sppf.deepEquals(expectedSPPF()));
	}
	
	
	private SPPFNode expectedSPPF() {
		TerminalSymbolNode node0 = new TerminalSymbolNode(-2, 0);
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminal(0), 0, 0);
		node1.addChild(node0);
		return node1;
	}

}