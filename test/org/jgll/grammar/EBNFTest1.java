package org.jgll.grammar;

import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

import static org.jgll.util.collections.CollectionsUtil.*;

/**
 * 
 * S ::= A+
 *      
 * A ::= a
 * 
 * @author Ali Afroozeh
 *
 */
public class EBNFTest1 extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		
		GrammarBuilder builder = new GrammarBuilder("EBNF");
		
		Nonterminal S = new Nonterminal("S");
		Nonterminal A = new Nonterminal("A");
		Terminal a = new Character('a');
		
		Rule rule1 = new Rule(S, list(new Plus(A)));
		
		Rule rule2 = new Rule(A, list(a));
		
		Iterable<Rule> newRules = EBNFUtil.rewrite(list(rule1, rule2));
		
		builder.addRules(newRules);
		
		return builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aaa"), grammar, "S");
		generateSPPFGraphWithIntermeiateAndListNodes(sppf);
	}

}
