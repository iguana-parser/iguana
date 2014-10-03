package org.jgll.grammar.transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Opt;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;

public class EBNFToBNF implements GrammarTransformation {
	
	private Map<Symbol, Nonterminal> cache;
	
	public EBNFToBNF() {
		cache = new HashMap<>();
	}
	
	@Override
	public Iterable<Rule> transform(Iterable<Rule> rules) {
		Set<Rule> newRules = new HashSet<>();
		
		for(Rule rule : rules) {
			newRules.add(rewrite(rule, newRules));
		}
		
		return newRules;
	}
	
	@Override
	public Iterable<Rule> transform(Rule rule) {
		Set<Rule> newRules = new HashSet<>();
		newRules.add(rewrite(rule, newRules));
		return newRules;
	}
	
	private boolean isEBNF(Symbol s) {
		return s instanceof Plus ||
			   s instanceof Opt ||
			   s instanceof Group;
	}
	
	private Rule rewrite(Rule rule, Set<Rule> newRules) {
		
		Rule.Builder builder = new Rule.Builder(rule.getHead());
		
		for(Symbol s : rule.getBody()) {
			builder.addSymbol(rewrite(s, newRules));
		}
		
		return builder.build();
	}
	
	public Symbol rewrite(Symbol s, Set<Rule> rules) {
		
		if(!isEBNF(s)) {
			return s;
		}
		
		Nonterminal newNt = cache.get(s);
		if (newNt != null) return newNt;
		
		/**
		 * S+ ::= S+ S
		 *      | S
		 */
		if(s instanceof Plus) {
			Symbol in = ((Plus) s).getSymbol();
			newNt = new Nonterminal.Builder(s.getName()).setEbnfList(true).addConditions(s.getConditions()).build();
			rules.add(new Rule(newNt, newNt, in));
			rules.add(new Rule(newNt, in));
		} 
		
		else if (s instanceof Opt) {
			Symbol in = ((Opt) s).getSymbol();
			newNt = new Nonterminal.Builder(s.getName()).setEbnfList(true).addConditions(s.getConditions()).build();
			rules.add(new Rule(newNt, in));
			rules.add(new Rule(newNt));
		} 
		
		else if (s instanceof Group) {
			List<? extends Symbol> symbols = ((Group) s).getSymbols();
			newNt = new Nonterminal.Builder(s.getName()).addConditions(s.getConditions()).build();
			rules.add(new Rule(newNt, symbols));
		} 
		
		else {
			throw new IllegalStateException("Should not be here!");			
		}
		
		cache.put(s, newNt);
		return newNt;
	}

}
