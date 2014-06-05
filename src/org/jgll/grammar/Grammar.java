package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgll.grammar.exception.NonterminalNotDefinedException;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;
import org.jgll.util.CollectionsUtil;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class Grammar implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<Nonterminal, List<List<Symbol>>> definitions;

	private final Map<Nonterminal, List<Object>> objects;
	
	private final Map<Nonterminal, Set<RegularExpression>> firstSets;
	
	private final Map<Nonterminal, Set<RegularExpression>> followSets;
	
	private final Map<Nonterminal, List<Set<RegularExpression>>> predictionSets;
	
	private final List<Rule> rules;
	
	public Grammar(Map<Nonterminal, List<List<Symbol>>> definitions,
				   List<Rule> rules,
				   Map<Nonterminal, List<Object>> objects,
				   Map<Nonterminal, Set<RegularExpression>> firstSets,
				   Map<Nonterminal, Set<RegularExpression>> followSets,
				   Map<Nonterminal, List<Set<RegularExpression>>> predictionSets) {
		this.definitions = new HashMap<>(definitions);
		this.rules = new ArrayList<>(rules);
		this.objects = new HashMap<>(objects);
		this.firstSets = new HashMap<>(firstSets);
		this.followSets = new HashMap<>(followSets);
		this.predictionSets = new HashMap<>(predictionSets);
	}
	
	public Map<Nonterminal, List<List<Symbol>>> getDefinitions() {
		return definitions;
	}
	
	public List<List<Symbol>> getAlternatives(Nonterminal nonterminal) {
		return definitions.get(nonterminal);
	}
	
	public Set<Nonterminal> getNonterminals() {
		return definitions.keySet();
	}
	
	public List<Rule> getRules() {
		return rules;
	}
	
	public int sizeRules() {
		int num = 0;
		for(Nonterminal head : definitions.keySet()) {
			num += definitions.get(head).size();
		}
		return num;
	}
	
	public Map<Nonterminal, Set<RegularExpression>> getFirstSets() {
		return Collections.unmodifiableMap(firstSets);
	}
	
	public Map<Nonterminal, Set<RegularExpression>> getFollowSets() {
		return Collections.unmodifiableMap(followSets);
	}
	
	public Map<Nonterminal, List<Set<RegularExpression>>> getPredictionSets() {
		return Collections.unmodifiableMap(predictionSets);
	}
	
	public Set<RegularExpression> getFirstSet(Nonterminal nonterminal) {
		return firstSets.get(nonterminal);
	}
	
	public Set<RegularExpression> getFollowSet(Nonterminal nonterminal) {
		return followSets.get(nonterminal);
	}
	
	public Set<RegularExpression> getPredictionSet(Nonterminal nonterminal, int alternativeIndex) {
		return predictionSets.get(nonterminal).get(alternativeIndex);
	}
	
	public boolean isNullable(Nonterminal nonterminal) {
		return firstSets.get(nonterminal).contains(Epsilon.getInstance());
	}
	 
	public GrammarGraph toGrammarGraph() {
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarGraphBuilder builder = new GrammarGraphBuilder(this, factory);
		return builder.build();
	}
	
	public Set<RuntimeException> validate() {
		
		Set<RuntimeException> validationExceptions = new HashSet<>();
		
		for (Entry<Nonterminal, List<List<Symbol>>> e : definitions.entrySet()) {
			for (List<Symbol> alternative : e.getValue()) {
				if (alternative == null) continue; // Rewritten priority alternatives
				for (Symbol s : alternative) {
					if (s instanceof Nonterminal) {
						if (!definitions.containsKey(s)) {
							validationExceptions.add(new NonterminalNotDefinedException((Nonterminal) s));
						}						
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
			for (List<Symbol> alternatives : definitions.get(nonterminal)) {
				if (alternatives == null) continue;
				sb.append(CollectionsUtil.listToString(alternatives)).append("\n");
			}
		}
		
		return sb.toString();
	}
	
	public Object getObject(Nonterminal nonterminal, int alternateIndex) {
		return objects.get(nonterminal).get(alternateIndex);
	}
 	
	public static class Builder {
		
		private Set<Rule> addedRules;
		private List<Rule> rules;
		private final Map<Nonterminal, List<List<Symbol>>> definitions;
		private final Map<Nonterminal, List<Object>> objects;

		public Builder() {
			this.addedRules = new HashSet<>();
			this.rules = new ArrayList<>();
			this.definitions = new HashMap<>();
			this.objects = new HashMap<>();
		}
		
		public Grammar build() {
			GrammarOperations op = new GrammarOperations(definitions);
			return new Grammar(definitions, rules, objects, op.getFirstSets(), op.getFollowSets(), op.getPredictionSets());
		}
		
		public Builder addRule(Rule rule) {
			
			if (addedRules.contains(rule)) {
				return this;
			}
			
			Nonterminal head = rule.getHead();
			List<List<Symbol>> definition = definitions.get(head);
			List<Object> list = objects.get(head);
			
			if (definition == null) {
				definition = new ArrayList<>();
				definitions.put(head, definition);
				list = new ArrayList<>();
				objects.put(head, list);
			}

			rules.add(rule);
			definition.add(rule.getBody());
			list.add(rule.getObject());
		
			return this;
		}
		
		public Builder addRules(Iterable<Rule> rules) {
			for (Rule rule : rules) {
				addRule(rule);
			}
			return this;
		}
	}
	
}
