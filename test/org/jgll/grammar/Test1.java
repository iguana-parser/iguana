package org.jgll.grammar;

import static java.util.Arrays.asList;

import org.jgll.sppf.NonterminalSymbolNode;
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
		generateGraph(sppf);
	}

}