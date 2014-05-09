package org.jgll.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jgll.grammar.exception.NonterminalNotDefinedException;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactoryImpl;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.CollectionsUtil;

public class Grammar {

	private Map<Nonterminal, List<List<Symbol>>> definitions;

	private Map<Nonterminal, List<Object>> objects;
	
	private Map<Nonterminal, Set<List<Symbol>>> addedDefinitions;
	
	private List<Rule> rules;
	
	public Grammar() {
		definitions = new HashMap<>();
		addedDefinitions = new HashMap<>();
		objects = new HashMap<>();
		rules = new ArrayList<>();
	}
	
	public Grammar addRules(Iterable<Rule> rules) {
		for (Rule rule : rules) {
			addRule(rule);
		}
		return this;
	}
	
	public Grammar addRule(Rule rule) {
		if(rule.getBody() != null) {
			Set<List<Symbol>> set = addedDefinitions.get(rule.getHead());
			
			if(set != null && set.contains(rule.getBody())) {
				return this;
			} else {
				if(set == null) {
					set = new HashSet<>();
					addedDefinitions.put(rule.getHead(), set);
				}
				set.add(rule.getBody());
				rules.add(rule);
			}			
		}
		
		Nonterminal head = rule.getHead();
		List<List<Symbol>> definition = definitions.get(head);
		List<Object> list = objects.get(head);
		if(definition == null) {
			// The order in which alternates are added is important
			definition = new ArrayList<>();
			definitions.put(head, definition);
			list = new ArrayList<>();
			objects.put(head, list);
		}
		definition.add(rule.getBody());
		rules.add(rule);
		
		list.add(rule.getObject());
		
		return this;
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
	
	public GrammarGraph toGrammarGraph() {
		GrammarSlotFactory factory = new GrammarSlotFactoryImpl();
		GrammarBuilder builder = new GrammarBuilder(this, factory);
		return builder.build();
	}
	
	public Set<RuntimeException> validate() {
		
		Set<RuntimeException> validationExceptions = new HashSet<>();
		
		for (Entry<Nonterminal, List<List<Symbol>>> e : definitions.entrySet()) {
			for (List<Symbol> alternative : e.getValue()) {
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
	
}
