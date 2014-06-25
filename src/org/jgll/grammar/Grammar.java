package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgll.grammar.exception.GrammarValidationException;
import org.jgll.grammar.exception.NonterminalNotDefinedException;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactoryFirstFollowChecksImpl;
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
		
		// Forcing all nested collectios to be immutable
		Map<Nonterminal, List<List<Symbol>>> tmp = new HashMap<>();
		for (Entry<Nonterminal, List<List<Symbol>>> e : definitions.entrySet()) {
			List<List<Symbol>> tmpList = new ArrayList<>();
			for (List<Symbol> list : e.getValue()) {
				tmpList.add(Collections.unmodifiableList(list));
			}
			tmp.put(e.getKey(), Collections.unmodifiableList(tmpList));
		}
		this.definitions = Collections.unmodifiableMap(tmp);
		
		this.rules = Collections.unmodifiableList(rules);
		this.objects = Collections.unmodifiableMap(objects);
		
		Map<Nonterminal, Set<RegularExpression>> tmpFirstSets= new HashMap<>();
		for (Entry<Nonterminal, Set<RegularExpression>> e : firstSets.entrySet()) {
			tmpFirstSets.put(e.getKey(), Collections.unmodifiableSet(e.getValue()));
		}
		this.firstSets = Collections.unmodifiableMap(tmpFirstSets);
		
		Map<Nonterminal, Set<RegularExpression>> tmpFollowSets= new HashMap<>();
		for (Entry<Nonterminal, Set<RegularExpression>> e : followSets.entrySet()) {
			tmpFollowSets.put(e.getKey(), Collections.unmodifiableSet(e.getValue()));
		}
		this.followSets = Collections.unmodifiableMap(tmpFollowSets);
		
		Map<Nonterminal, List<Set<RegularExpression>>> tmpPredictionSets = new HashMap<>();
		for (Entry <Nonterminal, List<Set<RegularExpression>>> e : predictionSets.entrySet()) {
			List<Set<RegularExpression>> tmpList = new ArrayList<>();
			for (Set<RegularExpression> set : e.getValue()) {
				tmpList.add(Collections.unmodifiableSet(set));
			}
			tmpPredictionSets.put(e.getKey(), Collections.unmodifiableList(tmpList));
		}
		this.predictionSets = Collections.unmodifiableMap(tmpPredictionSets);
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
		return firstSets;
	}
	
	public Map<Nonterminal, Set<RegularExpression>> getFollowSets() {
		return followSets;
	}
	
	public Map<Nonterminal, List<Set<RegularExpression>>> getPredictionSets() {
		return predictionSets;
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
		GrammarSlotFactory factory = new GrammarSlotFactoryFirstFollowChecksImpl();
		GrammarGraphBuilder builder = new GrammarGraphBuilder(this, factory);
		return builder.build();
	}
	
	public GrammarGraph toGrammarGraphWithoutFirstFollowChecks() {
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarGraphBuilder builder = new GrammarGraphBuilder(this, factory);
		return builder.build();
	}
	
	private static Set<RuntimeException> validate(Map<Nonterminal, List<List<Symbol>>> definitions) {
		
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
			Set<RuntimeException> exceptions = validate(definitions);
			if (!exceptions.isEmpty()) {
				throw new GrammarValidationException(exceptions);
			}
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
