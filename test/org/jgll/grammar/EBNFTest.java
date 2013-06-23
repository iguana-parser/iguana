package org.jgll.grammar;

import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

public class EBNFTest extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("A*");
		
		Rule rule1 = new Rule(new Nonterminal("A*", true), list(new Nonterminal("A*"), new Nonterminal("A")));
		builder.addRule(rule1);
		
		Rule rule2 = new Rule(new Nonterminal("A*", true), emptyList());
		builder.addRule(rule2);
		
		Rule rule3 = new Rule(new Nonterminal("A"), list(new Character('a')));
		builder.addRule(rule3);
		
		return builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aaa"), grammar, "A*");
		generateGraphWithIntermeiateAndListNodes(sppf);
	}

}
