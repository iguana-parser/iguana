package org.jgll.grammar.transformation;

import java.util.ArrayList;
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

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class EBNFToBNF implements GrammarTransformation {
	
	private Map<Symbol, Nonterminal> cache;
	
	public EBNFToBNF() {
		cache = new HashMap<>();
	}
	
	@Override
	public Grammar transform(Grammar grammar) {
		Set<Rule> newRules = new LinkedHashSet<>();
		List<Rule> newLayoutRules = new ArrayList<>();
		
		grammar.getDefinitions().values().forEach(r -> newRules.addAll(transform(r)));
		grammar.getLayoutRules().forEach(r -> newLayoutRules.addAll(transform(r)));
		
		return Grammar.builder().addRules(newRules).setLayoutNonterminal(grammar.getLayout()).addLayoutRules(newLayoutRules).build();
	}
	
	private Set<Rule> transform(Rule rule) {
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
			builder.addSymbol(rewrite(s, newRules, rule.getLayout()));
		}
		
		return builder.build();
	}
	
	private Symbol rewrite(Symbol symbol, Set<Rule> addedRules, Nonterminal layout) {
		
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
			newNt = Nonterminal.withName(getName(S, star.getSeparators(), layout) + "*");
			addedRules.add(Rule.withHead(newNt).addSymbols(rewrite(Plus.builder(S).addSeparators(star.getSeparators()).build(), addedRules, layout)).build());
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
			List<Symbol> seperators = plus.getSeparators().stream().map(sep -> rewrite(sep, addedRules, layout)).collect(Collectors.toList());
			newNt = Nonterminal.withName(getName(S, plus.getSeparators(), layout) + "+");
			addedRules.add(Rule.withHead(newNt).addSymbol(newNt).addSymbols(seperators).addSymbols(rewrite(S, addedRules, layout)).build());
			addedRules.add(Rule.withHead(newNt).addSymbol(rewrite(S, addedRules, layout)).build());
		} 
		
		/**
		 * S? ::= S 
		 *      | epsilon
		 */
		else if (symbol instanceof Opt) {
			Symbol in = ((Opt) symbol).getSymbol();
			newNt = Nonterminal.withName(symbol.getName());
			addedRules.add(Rule.withHead(newNt).addSymbol(rewrite(in, addedRules, layout)).setLayout(layout).build());
			addedRules.add(Rule.withHead(newNt).build());
		} 
		
		/**
		 * (S) ::= S
		 */
		else if (symbol instanceof Sequence) {
			@SuppressWarnings("unchecked")
			List<Symbol> symbols = ((Sequence<Symbol>) symbol).getSymbols().stream().map(x -> rewrite(x, addedRules, layout)).collect(Collectors.toList());
			newNt = Nonterminal.withName(symbol.getName());
			addedRules.add(Rule.withHead(newNt).addSymbols(symbols).setLayout(layout).build());
		} 
		
		/**
		 * (A | B) ::= A 
		 *           | B
		 */
		else if (symbol instanceof Alt) {
			newNt = Nonterminal.withName(symbol.getName());
			@SuppressWarnings("unchecked")
			List<Symbol> symbols = ((Alt<Symbol>) symbol).getSymbols().stream().map(x -> rewrite(x, addedRules, layout)).collect(Collectors.toList());
			symbols.forEach(x -> addedRules.add(Rule.withHead(newNt).addSymbol(x).setLayout(layout).build()));
		}
		
		else {
			throw new IllegalStateException("Unknown symbol type.");			
		}
		
		cache.put(symbol, newNt);
		return newNt;
	}
	
	private String getName(Symbol symbol, List<Symbol> separators, Nonterminal layout) {
		if (separators.isEmpty() && layout == null) {
			return symbol.getName();
		} else {
			return "{" + symbol.getName() +
					  ", " + separators.stream().map(s -> s.getName()).collect(Collectors.joining(", ")) + 
					  ", " + layout + 
				   "}";			
		}
	}

}
