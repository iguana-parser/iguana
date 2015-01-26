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
import org.jgll.regex.Star;

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
	
	public Symbol rewrite(Symbol symbol, Set<Rule> addedRules) {
		
		if (!isEBNF(symbol))
			return symbol;
		
		if (cache.get(symbol) != null) 
			return cache.get(symbol);
		
		final Nonterminal newNt;
		
		
		/**
		 * S* ::= S+ 
		 *      | epsilon
		 *        
		 */
		if (symbol instanceof Star) {
			Star star = (Star) symbol;
			Symbol S = star.getSymbol();
			newNt = Nonterminal.withName(star.getName());
			addedRules.add(Rule.withHead(newNt).addSymbols(rewrite(Plus.builder(S).addSeparators(star.getSeparators()).build(), addedRules)).build());
			addedRules.add(Rule.withHead(newNt).build());
		}
		
		/**
		 * S+ ::= S+ S
		 *      | S
		 *      
		 * in case of separators:
		 * 
		 * (S sep)+ ::= (S sep)+ sep S
		 *            | S
		 *      
		 */
		else if (symbol instanceof Plus) {
			Plus plus = (Plus) symbol;
			Symbol S = plus.getSymbol();
			List<Symbol> seperators = plus.getSeparators().stream().map(sep -> rewrite(sep, addedRules)).collect(Collectors.toList());
			newNt = Nonterminal.withName(symbol.getName());
			addedRules.add(Rule.withHead(newNt).addSymbol(newNt).addSymbols(seperators).addSymbols(rewrite(S, addedRules)).build());
			addedRules.add(Rule.withHead(newNt).addSymbol(rewrite(S, addedRules)).build());
		} 
		
		/**
		 * S? ::= S 
		 *      | epsilon
		 */
		else if (symbol instanceof Opt) {
			Symbol in = ((Opt) symbol).getSymbol();
			newNt = Nonterminal.withName(symbol.getName());
			addedRules.add(Rule.withHead(newNt).addSymbol(rewrite(in, addedRules)).build());
			addedRules.add(Rule.withHead(newNt).build());
		} 
		
		/**
		 * (S) ::= S
		 */
		else if (symbol instanceof Sequence) {
			@SuppressWarnings("unchecked")
			List<Symbol> symbols = ((Sequence<Symbol>) symbol).getSymbols().stream().map(x -> rewrite(x, addedRules)).collect(Collectors.toList());
			newNt = Nonterminal.withName(symbol.getName());
			addedRules.add(Rule.withHead(newNt).addSymbols(symbols).build());
		} 
		
		/**
		 * (A | B) ::= A 
		 *           | B
		 */
		else if (symbol instanceof Alt) {
			newNt = Nonterminal.withName(symbol.getName());
			@SuppressWarnings("unchecked")
			List<Symbol> symbols = ((Alt<Symbol>) symbol).getSymbols().stream().map(x -> rewrite(x, addedRules)).collect(Collectors.toList());
			symbols.forEach(x -> addedRules.add(Rule.withHead(newNt).addSymbol(x).build()));
		}
		
		else {
			throw new IllegalStateException("Unknown symbol type.");			
		}
		
		cache.put(symbol, newNt);
		return newNt;
	}

}
