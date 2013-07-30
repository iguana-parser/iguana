package org.jgll.grammar.conditions;

import org.jgll.grammar.AbstractGrammarTest;
import org.jgll.grammar.Character;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.Keyword;
import org.jgll.grammar.Nonterminal;
import org.jgll.grammar.Range;
import org.jgll.grammar.Rule;
import org.jgll.grammar.Terminal;

/**
 * 
 * S ::= "for" L Id+
 *     | "forall" L Id+
 *     
 * Id ::= [a-z]+ !>> [a-z]
 * 
 * L ::= " "
 * 
 * @author Ali Afroozeh
 *
 */
public class PrecedeRestriction extends AbstractGrammarTest {

	@Override
	protected Grammar initGrammar() {
		Nonterminal S = new Nonterminal("S");
		Keyword forr = new Keyword("for");
		Keyword forall = new Keyword("forall");
		Nonterminal L = new Nonterminal("L");
		Nonterminal Id = new Nonterminal("Id");
		Terminal s = new Character('s');
		Terminal ws = new Character(' ');
		Terminal az = new Range('a', 'z');
		
		Rule r1 = new Rule(S, forr, L, Id);
		
		Rule r2 = new Rule(S, s);
		Rule r3 = new Rule(L, ws);
		return new GrammarBuilder().addRule(r1).addRule(r2).addRule(r3).build();

	}	
	
	
}
