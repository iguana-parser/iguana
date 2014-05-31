package org.jgll.grammar.ebnf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Opt;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;

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
			Nonterminal newNt = new Nonterminal.Builder(s.getName()).setEbnfList(true).addConditions(s.getConditions()).build();
			rules.add(new Rule(newNt, newNt, in));
			rules.add(new Rule(newNt, in));
			return newNt;
		} 
		
		else if (s instanceof Opt) {
			Symbol in = ((Opt) s).getSymbol();
			Nonterminal newNt = new Nonterminal.Builder(s.getName()).setEbnfList(true).addConditions(s.getConditions()).build();
			rules.add(new Rule(newNt, in));
			rules.add(new Rule(newNt));
			return newNt;
		} 
		
		else if (s instanceof Group) {
			List<? extends Symbol> symbols = ((Group) s).getSymbols();
			Nonterminal newNt = new Nonterminal.Builder(s.getName()).addConditions(s.getConditions()).build();
			rules.add(new Rule(newNt, symbols));
			return newNt;
		}
		
		throw new IllegalStateException("Should not be here!");
	}
	

}
