package org.jgll.grammar;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * S ::= a S b S
 *     | a S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Rule rule1 = new Rule.Builder().head(new Nonterminal("S"))
				.body(new Character('a'), new Nonterminal("S"), new Character('b'), new Nonterminal("S")).build();
		Rule rule2 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('a'), new Nonterminal("S")).build();
		Rule rule3 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('s')).build();
		return Grammar.fromRules("DanglingElse", set(rule1, rule2, rule3));
	}
	
	@Test
	public void test() {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aasbs"), grammar, "S");
		generateGraphWithoutIntermeiateNodes(sppf);
	}

}
