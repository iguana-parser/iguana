package org.jgll.grammar;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.exception.GrammarValidationException;
import org.jgll.grammar.exception.NonterminalNotDefinedException;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.regex.RegularExpression;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ListMultimap<Nonterminal, Rule> definitions;

	private final SetMultimap<Nonterminal, RegularExpression> firstSets;
	
	private final SetMultimap<Nonterminal, RegularExpression> followSets;
	
	private GrammarGraph grammarGraph;
	
	public Grammar(ListMultimap<Nonterminal, Rule> definitions,
				   SetMultimap<Nonterminal, RegularExpression> firstSets,
				   SetMultimap<Nonterminal, RegularExpression> followSets) {
		this.definitions = ImmutableListMultimap.copyOf(definitions);
		this.firstSets = ImmutableSetMultimap.copyOf(firstSets);
		this.followSets = ImmutableSetMultimap.copyOf(followSets);
	}
	
	public Multimap<Nonterminal, Rule> getDefinitions() {
		return definitions;
	}
	
	public List<Rule> getAlternatives(Nonterminal nonterminal) {
		return definitions.get(nonterminal);
	}
	
	public Set<Nonterminal> getNonterminals() {
		return definitions.keySet();
	}
	
	public List<Rule> getRules() {
		return new ArrayList<>(definitions.values());
	}
	
	public int sizeRules() {
		int num = 0;
		for(Nonterminal head : definitions.keySet()) {
			num += definitions.get(head).size();
		}
		return num;
	}
	
	public Set<RegularExpression> getFirstSet(Nonterminal nonterminal) {
		return firstSets.get(nonterminal);
	}
	
	public Set<RegularExpression> getFollowSet(Nonterminal nonterminal) {
		return followSets.get(nonterminal);
	}
	
	public Set<RegularExpression> getPredictionSet(Nonterminal nonterminal) {
		Set<RegularExpression> set = getFirstSet(nonterminal);
		if (set.contains(Epsilon.getInstance())) {
			set.addAll(getFollowSet(nonterminal));
		}
		return set;
	}
	
	public Set<RegularExpression> getPredictionSet(Rule rule, int index) {
		return null;
	}
	
	public boolean isNullable(Nonterminal nonterminal) {
		return firstSets.get(nonterminal).contains(Epsilon.getInstance());
	}
	 
	public GrammarGraph toGrammarGraph() {
		if (grammarGraph == null) {
			GrammarGraphBuilder builder = new GrammarGraphBuilder(this);
			grammarGraph =  builder.build();
		}
		return grammarGraph;
	}
	
	private static Set<RuntimeException> validate(ListMultimap<Nonterminal, Rule> definitions) {
		
		Set<RuntimeException> validationExceptions = new HashSet<>();
		
		for (Rule rule : definitions.values()) {
			if (rule.getBody() == null) continue; // Rewritten priority alternatives
			for (Symbol s : rule.getBody()) {
				if (s instanceof Nonterminal) {
					if (!definitions.containsKey(s)) {
						validationExceptions.add(new NonterminalNotDefinedException((Nonterminal) s));
					}						
				}
			}
		}
		
		return validationExceptions;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (Nonterminal nonterminal : definitions.keySet()) {
			sb.append(nonterminal).append(" ::= ");
			for (List<Symbol> alternatives : definitions.get(nonterminal).stream().map(r -> r.getBody()).collect(Collectors.toList())) {
				if (alternatives == null) continue;
				sb.append(listToString(alternatives)).append("\n");
			}
		}
		
		return sb.toString();
	}
	
	public Object getObject(Nonterminal nonterminal, int alternateIndex) {
		return definitions.get(nonterminal).get(alternateIndex).getObject();
	}
	
	public static Builder builder() {
		return new Builder();
	}
 	
	public static class Builder {
		
		private Set<Rule> addedRules;
		private final ListMultimap<Nonterminal, Rule> definitions;
		
		private final EBNFToBNF ebnfToBNF;

		public Builder() {
			this.addedRules = new HashSet<>();
			this.definitions = ArrayListMultimap.create();
			this.ebnfToBNF = new EBNFToBNF();
		}
		
		public Grammar build() {
			Set<RuntimeException> exceptions = validate(definitions);
			if (!exceptions.isEmpty()) {
				throw new GrammarValidationException(exceptions);
			}
			GrammarOperations op = new GrammarOperations(definitions);
			return new Grammar(definitions, op.getFirstSets(), op.getFollowSets());
		}
		
		public Builder addRule(Rule rule) {
			
			Iterable<Rule> newRules = ebnfToBNF.transform(rule);
			
			for (Rule r : newRules) {
				
				if (addedRules.contains(r))
					continue;
				
				definitions.put(r.getHead(), r);
			}
			
			return this;
		}
		
		public Builder addRules(Iterable<Rule> rules) {
			for (Rule rule : rules) {
				addRule(rule);
			}
			return this;
		}
		
		public Builder addRules(Rule...rules) {
			addRules(Arrays.asList(rules));
			return this;
		}
	}
	
}
