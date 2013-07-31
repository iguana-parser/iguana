package org.jgll.grammar.ebnf;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.Nonterminal;
import org.jgll.grammar.Opt;
import org.jgll.grammar.Plus;
import org.jgll.grammar.Rule;
import org.jgll.grammar.Symbol;
import org.jgll.grammar.condition.Condition;

public class EBNFUtil {
	
	public static boolean isEBNF(Symbol s) {
		return s instanceof Plus ||
			   s instanceof Opt;
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
			Nonterminal newNt = new Nonterminal(in.getName() + "+", true);
			copyConditions(s, newNt);
			rules.add(new Rule(newNt, newNt, in));
			rules.add(new Rule(newNt, in));
			return newNt;
		} 
		
		else if (s instanceof Opt) {
			Symbol in = ((Opt) s).getSymbol();
			Nonterminal newNt = new Nonterminal(in.getName() + "?", true);
			copyConditions(s, newNt);
			rules.add(new Rule(newNt, in));
			rules.add(new Rule(newNt));
			return newNt;
		}
		
		throw new IllegalStateException("Should not be here!");
	}
	
	private static void copyConditions(Symbol source, Symbol destination) {
		for(Condition condition : source.getConditions()) {
			destination.addCondition(condition);
		}
	}

}
