package org.jgll.grammar.transformation;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.Alt;
import org.jgll.regex.Opt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;

public class EBNFToBNF implements GrammarTransformation {
	
	private Map<Symbol, Nonterminal> cache;
	
	public EBNFToBNF() {
		cache = new HashMap<>();
	}
	
	@Override
	public Grammar transform(Grammar grammar) {
		Set<Rule> newRules = new LinkedHashSet<>();
		Set<Rule> newLayout = new LinkedHashSet<>();
		
		grammar.getDefinitions().values().forEach(r -> newRules.addAll(transform(r)));
		grammar.getLayout().values().forEach(r -> newLayout.addAll(transform(r)));
		
		return Grammar.builder().addRules(newRules).addLayouts(newLayout).build();
	}
	
	public Set<Rule> transform(Rule rule) {
		Set<Rule> newRules = new LinkedHashSet<>();
		newRules.add(rewrite(rule, newRules));
		return newRules;
	}
	
	private boolean isEBNF(Symbol s) {
		return s instanceof Plus ||
			   s instanceof Opt ||
			   s instanceof Sequence ||
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
		
		
		final Nonterminal newNt;

		/**
		 * S+ ::= S+ S
		 *      | S
		 */
		if(s instanceof Plus) {
			Symbol in = ((Plus) s).getSymbol();
			newNt = Nonterminal.withName(s.getName());
			rules.add(Rule.withHead(newNt).addSymbols(newNt, rewrite(in, rules)).build());
			rules.add(Rule.withHead(newNt).addSymbol(rewrite(in, rules)).build());
		} 
		
		/**
		 * S? ::= S 
		 *      | epsilon
		 */
		else if (s instanceof Opt) {
			Symbol in = ((Opt) s).getSymbol();
			newNt = Nonterminal.withName(s.getName());
			rules.add(Rule.withHead(newNt).addSymbol(rewrite(in, rules)).build());
			rules.add(Rule.withHead(newNt).build());
		} 
		
		/**
		 * (S) ::= S
		 * 
		 */
		else if (s instanceof Sequence) {
			@SuppressWarnings("unchecked")
			List<Symbol> symbols = ((Sequence<Symbol>) s).getSymbols().stream().map(x -> rewrite(x, rules)).collect(Collectors.toList());
			newNt = Nonterminal.withName(s.getName());
			rules.add(Rule.withHead(newNt).addSymbols(symbols).build());
		} 
		
		/**
		 * (A | B) ::= A 
		 *           | B
		 */
		else if (s instanceof Alt) {
			newNt = Nonterminal.withName(s.getName());
			@SuppressWarnings("unchecked")
			List<Symbol> symbols = ((Alt<Symbol>) s).getSymbols().stream().map(x -> rewrite(x, rules)).collect(Collectors.toList());
			symbols.forEach(x -> rules.add(Rule.withHead(newNt).addSymbol(x).build()));
		}
		
		else {
			throw new IllegalStateException("Unknown symbol type.");			
		}
		
		cache.put(s, newNt);
		return newNt;
	}

}
