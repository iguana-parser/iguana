package org.jgll.grammar.transformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.symbol.Alt;
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
			   s instanceof Group ||
			   s instanceof Alt;
	}
	
	private Rule rewrite(Rule rule, Set<Rule> newRules) {

		if (rule.getBody() == null) {
			return rule;
		}
		
		Rule.Builder builder = new Rule.Builder(rule.getHead());		
		for(Symbol s : rule.getBody()) {
			builder.addSymbol(rewrite(s, newRules));
		}
		
		return builder.build();
	}
	
	public Symbol rewrite(Symbol s, Set<Rule> rules) {
		
		if(!isEBNF(s))
			return s;
		
		if (cache.get(s) != null) 
			return cache.get(s);
		
		/**
		 * S+ ::= S+ S
		 *      | S
		 */
		
		final Nonterminal newNt;
		
		if(s instanceof Plus) {
			Symbol in = ((Plus) s).getSymbol();
			newNt = new Nonterminal.Builder(s.getName()).setEbnfList(true).addPreConditions(s.getPreConditions()).build();
			rules.add(Rule.withHead(newNt).addSymbols(newNt, rewrite(in, rules)).build());
			rules.add(Rule.withHead(newNt).addSymbol(rewrite(in, rules)).build());
		} 
		
		else if (s instanceof Opt) {
			Symbol in = ((Opt) s).getSymbol();
			newNt = new Nonterminal.Builder(s.getName()).setEbnfList(true).addPreConditions(s.getPreConditions()).build();
			rules.add(Rule.withHead(newNt).addSymbol(rewrite(in, rules)).build());
			rules.add(Rule.withHead(newNt).build());
		} 
		
		else if (s instanceof Group) {
			List<Symbol> symbols = ((Group) s).getSymbols().stream().map(x -> rewrite(x, rules)).collect(Collectors.toList());
			newNt = new Nonterminal.Builder(s.getName()).addPreConditions(s.getPreConditions()).build();
			rules.add(Rule.withHead(newNt).addSymbols(symbols).build());
		} 
		
		else if (s instanceof Alt) {
			newNt = new Nonterminal.Builder(s.getName()).addPreConditions(s.getPreConditions()).build();
			List<Symbol> symbols = ((Alt) s).getSymbols().stream().map(x -> rewrite(x, rules)).collect(Collectors.toList());
			symbols.forEach(x -> rules.add(Rule.withHead(newNt).addSymbol(x).build()));
		}
		
		else {
			throw new IllegalStateException("Unknown symbol type.");			
		}
		
		cache.put(s, newNt);
		return newNt;
	}

}
