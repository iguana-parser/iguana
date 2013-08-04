package org.jgll.grammar.ebnf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.Group;
import org.jgll.grammar.Nonterminal;
import org.jgll.grammar.Opt;
import org.jgll.grammar.Plus;
import org.jgll.grammar.Rule;
import org.jgll.grammar.Symbol;

public class EBNFUtil {
	
	public static boolean isEBNF(Symbol s) {
		return s instanceof Plus ||
			   s instanceof Opt ||
			   s instanceof Group;
	}
	
	public static Iterable<Rule> rewrite(Rule...rules) {
		return rewrite(Arrays.asList(rules));
	}
	
	public static Iterable<Rule> rewrite(Iterable<Rule> rules) {
		Set<Rule> set = new HashSet<>();
		
		for(Rule rule : rules) {
			set.add(rewrite(rule, set));
		}
		
		return set;
	}
	
	public static Rule rewrite(Rule rule, Set<Rule> rules) {
		
		Rule.Builder builder = new Rule.Builder(rule.getHead());
		
		for(Symbol s : rule.getBody()) {
			builder.addSymbol(rewrite(s, rules));
		}
		return builder.build();
	}
	
	public static Symbol rewrite(Symbol s, Set<Rule> rules) {
		
		if(!isEBNF(s)) {
			return s;
		}
		
		/**
		 * S+ ::= S+ S
		 *      | S
		 */
		if(s instanceof Plus) {
			Symbol in = ((Plus) s).getSymbol();
			Nonterminal newNt = new Nonterminal(s.getName(), true);
			rules.add(new Rule(newNt, newNt, in));
			rules.add(new Rule(newNt, in));
			return newNt.addConditions(s.getConditions());
		} 
		
		else if (s instanceof Opt) {
			Symbol in = ((Opt) s).getSymbol();
			Nonterminal newNt = new Nonterminal(s.getName(), true);
			rules.add(new Rule(newNt, in));
			rules.add(new Rule(newNt));
			return newNt.addConditions(s.getConditions());
		} 
		
		else if (s instanceof Group) {
			List<? extends Symbol> symbols = ((Group) s).getSymbols();
			Nonterminal newNt = new Nonterminal(s.getName(), false);
			rules.add(new Rule(newNt, symbols));
			return newNt.addConditions(s.getConditions());
		}
		
		throw new IllegalStateException("Should not be here!");
	}
	

}
