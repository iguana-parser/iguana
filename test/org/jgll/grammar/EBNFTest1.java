package org.jgll.grammar;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.ebnf.EBNFUtil;
import org.jgll.parser.ParseError;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

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
		List<Rule> rules = new ArrayList<>();
		rule1 = EBNFUtil.rewrite(rule1, rules);
		builder.addRule(rule1);
		builder.addRules(rules);
		
		Rule rule2 = new Rule(A, list(a));
		builder.addRule(rule2);
		
		return builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aaa"), grammar, "S");
		generateSPPFGraphWithIntermeiateAndListNodes(sppf);
	}

}
