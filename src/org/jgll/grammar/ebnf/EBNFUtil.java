package org.jgll.grammar.ebnf;

import java.util.List;

import org.jgll.grammar.Nonterminal;
import org.jgll.grammar.Plus;
import org.jgll.grammar.Rule;
import org.jgll.grammar.Symbol;

public class EBNFUtil {
	
	public static boolean isEBNF(Symbol s) {
		return s instanceof Plus;
	}
	
	public static Rule rewrite(Rule rule, List<Rule> rules) {
		
		Rule.Builder builder = new Rule.Builder(rule.getHead());
		
		for(Symbol s : rule.getBody()) {
			builder.addSymbol(rewrite(s, rules));
		}
		return builder.build();
	}
	
	public static Symbol rewrite(Symbol s, List<Rule> rules) {
		
		if(!isEBNF(s)) {
			return s;
		}
		
		/**
		 * S+ ::= S+ S
		 *      | S
		 */
		if(s instanceof Plus) {
			Symbol in = ((Plus) s).getSymbol();
			Nonterminal newNt = new Nonterminal(in.getName() + "+", true);
			rules.add(new Rule(newNt, newNt, in));
			rules.add(new Rule(newNt, in));
			return newNt;
		}
		
		throw new IllegalStateException("Should not be here!");
	}

}
