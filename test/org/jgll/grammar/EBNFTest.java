package org.jgll.grammar;

import org.jgll.sppf.NonterminalSymbolNode;
import org.junit.Test;

public class EBNFTest extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule rule1 = new Rule.Builder().head(new Nonterminal("A*", true)).body(new Nonterminal("A*"), new Nonterminal("A")).build();
		Rule rule2 = new Rule.Builder().head(new Nonterminal("A*", true)).body().build();
		Rule rule3 = new Rule.Builder().head(new Nonterminal("A")).body(new Character('a')).build();
		return Grammar.fromRules("A*", set(rule1, rule2, rule3));
	}
	
	@Test
	public void test() {
		NonterminalSymbolNode sppf = rdParser.parse("aaa", grammar, "A*");
		generateGraphWithIntermeiateAndListNodes(sppf);
	}

}
